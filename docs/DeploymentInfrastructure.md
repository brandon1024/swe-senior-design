# Deployment and Production Infrastructure
## Introduction
The purpose of this document is to provide a comprehensive overview of the deployment infrastructure for the Kick the Bucket project. This document aims to act as the central location for everything related to the project operations, and should be kept up-to-date as the project evolves and the infrastructure changes. References to the "Kick the Bucket", "KTB" or "the project" in this document are meant to refer to the backend server for the "Kick the Bucket" Senior Design project.

`Kick the Bucket` is a [Spring Boot](https://spring.io/projects/spring-boot) REST API backend application for the project. The project is Gradle based, and uses the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html). The project uses the [PostgreSQL 10](https://www.postgresql.org/) database for data persistence.

Continuous Integration (CI) and Continuous Deployment (CD) is provided through [GitLab Pipelines](https://docs.gitlab.com/ee/ci/pipelines.html). The project is hosted through [Amazon Web Services](https://aws.amazon.com/).

## Table of Contents
* [Project Configuration](#project-configuration)
    * [Deploying a New Release to Production](#deploying-a-new-release-to-production)
    * [Merge to Master Branch and Tag](#merge-to-master-branch-and-tag)
    * [Properties and Profiles](#properties-and-profiles)
    * [Building Distribution Artifacts](#building-distribution-artifacts)
    * [Executing Artifacts](#executing-artifacts)
* [GitLab CI Pipeline](#gitlab-ci-pipeline)
    * [Pipeline Configuration](#pipeline-configuration)
        * [Test Stage](#test-stage)
        * [Deploy Stage](#deploy-stage)
    * [Authentication with AWS S3](#authentication-with-aws-s3)
* [AWS IAM and VPC Infrastructure](#aws-iam-and-vpc-infrastructure)
    * [Users](#users)
    * [IAM Roles](#iam-roles)
    * [VPC](#vpc)
* [AWS CodeDeploy](#aws-codedeploy)
    * [Parameter Store](#parameter-store)
* [AWS RDS Instance](#aws-rds-instance)
    * [Instance Details](#instance-details)
    * [Database Users](#database-users)
    * [Accessing Database Console](#accessing-database)
* [AWS EC2 Instance](#aws-ec2-instance)
    * [Instance Details](#instance-details)
    * [SSH into EC2 Instance](#ssh-into-ec2-instance)
    * [Start SSH Session Through Systems Manager](#start-ssh-session-through-systems-manager)
    * [Instance Libraries and Software](#instance-libraries-and-software)
* [AWS Elastic Load Balancer](#aws-elastic-load-balancer)
* [AWS ACM Certificate Management](#aws-acm-certificate-management)
* [AWS Simple Storage Service (S3)](#aws-simple-storage-service-s3)
    * [S3 Bucket Configuration](#s3-bucket-configuration)
* [KTB Server Administration](#ktb-server-administration)
    * [Administrative Users](#administrative-users)
    * [Actuator Endpoints](#actuator-endpoints)
* [Appendix](#appendix)
    * [Manually Deploy Build Artifacts](#manually-deploy-build-artifacts)
    * [Updating Parameter Store Secured Strings Using AWS CLI](#updating-parameter-store-secured-strings-using-aws-cli)
    * [IAM ParameterStorePolicy for CodeDeploy Service Role](#iam-parameterstorepolicy-for-codedeploy-service-role)
    * [IAM S3DevAccessPolicy for S3 Access During Development](#iam-s3devaccesspolicy-for-s3-access-during-development)
    * [Update PostgreSQL Root User Password](#update-postgresql-root-user-password)
    * [Configuring iptables on New Instance](#configuring-iptables-on-new-instance)

Shown below is a high-level diagram which describes how each service in the production infrastructure interact.
```
                              +-----------------+
       NEW                    |                 |
     RELEASE  +-------------->+ GITLAB PIPELINE +--------------------+
       TAG                    |                 |      PUSHES        |
                              +--------+--------+     ARTIFACTS      |
                                       |                             |
                              CREATE   |                             |
                            DEPLOYMENT |                             |
                                       |                             v
                              +--------v--------+           +--------+--------+
                              |                 |           |                 |
                              | AWS CODE DEPLOY +---------->+      AWS S3     |
                              |                 |   FETCH   |                 |
                              +--------+--------+  FROM S3  +-----------------+
                                       |
                               INSTALL |
                               AND RUN |
                                       v
+-----------------+   DATA    +--------+--------+   OBJECT  +-----------------+
|                 |  STORAGE  |                 |  STORAGE  |                 |
|     AWS RDS     +<----------+     AWS EC2     +---------->+      AWS S3     |
|                 |           |                 |           |                 |
+-----------------+           +-----------------+           +-----------------+
```

Shown below is a high-level diagram which describes how remote clients can interact with the server, and how their requests are routed within AWS:
```
                         AWS VIRTUAL PRIVATE CLOUD

+---------------------------------------------------------------------------+
|                                                                           |
+-----------------------------------+  +------------------------------------+
||             AWS EC2              |  |                                   ||
||   +--------------------------+   |  |     AWS ELASTIC LOAD BALANCER     ||
||   |       SPRING BOOT        |   |  |   80                              ||
||   |           WEB            +<---------------+---------------+         ||
||   |       APPLICATION        |   |  |         |               |         ||
||   +--------------------------+   |  |         |               |         ||
||                                  |  |         |               |         ||
+----------------+------------------+  +---------+---------------+----------+
|                ^                               |               |          |
+---------------------------------------------------------------------------+
                 |                               |               |
                 | 22                         80 |               | 443
                 |                               |               |
+---------------------------------------------------------------------------+
|        +---------------+              +---------------------------------+ |
|        |  SSH CLIENT   |              |           HTTP CLIENT           | |
|        +---------------+              +---------------------------------+ |
+---------------------------------------------------------------------------+

                               PUBLIC NETWORK
```

## Project Configuration
### Deploying a New Release to Production
Deploying a new release to the production environment is a fairly simple task. In this section, I would outline the steps necessary to successfully deploy a new release.

### Bump Version Number
First, create a new branch off the `develop` branch with the following naming convention: `vX.Y.Z-Release`. Next, in `build.gradle`, update the version number to reflect the new release version:
```
version = 'X.Y.Z-SNAPSHOT'
```

Next, commit this change, push to branch, and merge to develop.

### Merge to Master Branch and Tag
Open a merge request to master. Once merged, create a new tag on the master branch:
```
$ git checkout master
$ git pull --rebase
$ git tag vX.Y.Z-SNAPSHOT
$ git push origin vX.Y.Z-SNAPSHOT
```

### Properties and Profiles
Each Spring Boot application can define externalized configuration through a `application.properties` file located in `src/main/resources`. This project uses the externalized configuration to specify development and production settings, such as the database connection information, logging preferences, server port numbers, etc.

Each source set can have any number of configurations, organized under different profiles. Profiles are specified through the naming of the properties file, for example `application-<profile>.properties`. By default, the default profile `application.properties` is used. Profiles are activated [a number of ways](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config), but this project typically uses the `SPRING_PROFILES_ACTIVE` environment variable.

Both `main` and `test` source sets have configuration files.
- Under `src/main/resources`, you will find `application.properties` and `application-prod.properties`. The default configuration profile is used for development activities, while the `prod` profile is used for the production environment.
- User `src/test/resources`, you will find `application.properties` and `application-ci.properties`. The default configuration profile is used for running the test suite locally (development), which the `ci` profile is used by GitLab pipelines for running the test suite during the CI phase.

Passwords and sensitive information must **NOT** be stored in these configuration files for use in production. Credentials and sensitive information should be secured through AWS Parameter Store, and fetched during the deployment stage. This topic will be covered in depth later.

The content of the configurations is out of scope for this document, but is covered in more depth in `docs/ProjectOverview.md` and `docs/Security.md`.

For more information on externalized configuration in Spring Boot, read [this documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).

### Building Distribution Artifacts
Build artifacts are produced using the Gradle build tool. The gradle wrapper executable is located at the root of the project.

To build artifacts manually:
```
$ ./gradlew build -x test -x docs -x distTar -x distZip -x bootDistTar
```

The artifact produced by gradle is a compressed `.zip` package, located in `build/distributions`, and has the following structure:
```
.
├── appspec.yml
├── bin
│   ├── kick-the-bucket
│   └── kick-the-bucket.bat
├── lib
│   └── kick-the-bucket-0.0.1-SNAPSHOT.jar
└── scripts
    ├── server-start-silent.sh
    ├── server-start.sh
    ├── server-stop.sh
    └── server-validate.sh
```

`appspec.yml` is a configuration file used by AWS CodeDeploy for deploying the artifacts to EC2. This is covered in more detail later.

Located in `bin/` are the linux and windows executables for running the server. The server can be started using the default profile by running the executable directly through the command line:
```
$ ./kick-the-bucket
```

The executable JAR is located in `lib/`. This is executed by the executables in `/bin`.

The `scripts/` directory is where the scripts are located for executing the server on the EC2 instance. They are simply wrapper scripts for the executables in `bin/` which configure the server correctly to run in the production environment. They are used by CodeDeploy to install and run in the EC2 instance.
- `server-start.sh` configures the server to run with the production configuration profile, and runs the `bin/kick-the-bucket` executable. The server PID is written to `pid.file`, which is used by `server-stop.sh` to stop the server. It also fetches from Parameter Store the password for the PostgreSQL database and the JWT secret, and provides it to the server as a command line argument.
- `server-start-silent.sh` runs `server-start.sh` but redirects stdout and stderr to `server.out` and `server.err`, runs the script in the background, and immediately exits. This script is used to ensure that CodeDeploy doesn't timeout while starting the server.
- `server-stop.sh` attempts to stop the server gracefully using the `/shutdown` actuator endpoint, falling back on a more forceful termination of the server if the server could not be stopped.
- `server-validate.sh` is used by CodeDeploy to very that the server started correctly. The script polls the `/health` actuator endpoint to verify that the server started correctly. If the script can't reach the server after 3 minutes, the script exits with status 1.
- `change-permissions.sh` gives execution permissions to all scripts and executables in this artifact.
- `server-restart.sh` can be used by administrators to restart the server manually.

### Executing Artifacts
Once the build artifacts are produced using the gradle build tool, they can be executed as follows:
```
$ unzip build/distributions/kick-the-bucket-boot-0.0.1-SNAPSHOT.zip
$ ./kick-the-bucket-boot-0.0.1-SNAPSHOT/bin/kick-the-bucket
```

To execute in a production environment:
```
$ unzip kick-the-bucket-boot-0.0.1-SNAPSHOT.zip
$ ./kick-the-bucket-boot-0.0.1-SNAPSHOT/scripts/server-start-silent.sh
```

Note: Running scripts as root user may cause prevent CodeDeploy from deploying new artifacts successfully. If the server is started as root, CodeDeploy (which runs as ec2-user user) will not be able to stop the server. Be careful when running commands as root.

## GitLab CI Pipeline
The project uses GitLab Pipelines for CI and CD. The main purpose of the GitLab pipelines are:
- stage 1 (test):
    - to verify the health of the project by executing the test suites,
    - ensuring the internal documentation can be compiled from source (javadoc),
- stage 2 (deploy):
    - and push build artifacts to S3 for deployment

A pipeline is a group of jobs that get executed in stages. All of the jobs in a stage are executed, and if they all succeed the pipeline moves on to the next stage. If one of the jobs fails, the next stage is not executed.

The test stage job, which executes the test suite compiles the documentation, occur with every new commit to the GitLab repository, on any branch.

The deploy stage, which is configured to run only when new tags matching the format `v1.0.0-My-App-Release` are pushed, pushes build artifacts to S3.

### Pipeline Configuration
The GitLab Pipelines are configured through the `.gitlab-ci.yml` configuration file located at the root of the project. It describes exactly how the pipeline should be run, what docker image to use, what scripts should be run, how information should be cached between jobs, etc.

For more information on GitLab Pipeline configuration, see [Configuration of your pipelines with .gitlab-ci.yml](https://docs.gitlab.com/ee/ci/yaml/).

You can read more about GitLab Pipelines in general [here](https://docs.gitlab.com/ee/ci/pipelines.html).

#### Test Stage
As mentioned earlier, the test stage is used to execute the test suite and compile documentation. To run, the pipeline must use a docker image that has OpenJDK 10 installed. Additionally, the image must also have PostgreSQL 10 installed.

The following environment variables are necessary for the stage to execute:
- POSTGRES_DB: name of the database table to use for the test suite.
- POSTGRES_USER: database user.
- POSTGRES_PASSWORD: database user password.
- SPRING_PROFILES_ACTIVE: Spring Boot configuration profile to use.

The following gradle tasks are executed by the pipeline runner:
```
$ ./gradlew -s docs
$ ./gradlew -s test
```

#### Deploy Stage
The deploy stage is used to copy the build artifacts to AWS S3, which will be used by AWS CodeDeploy to execute on the AWS EC2 instance. The deploy stage is only run when a tag is pushed that has the following format: `v1.0.0-My-App-Release`

To copy the artifacts to S3, the AWS cli must be downloaded and installed onto the runner image. This is done using `apt-get` and `pip`:
```
$ apt-get update -qy
$ apt-get install -y python-dev python-pip
$ pip install -U pip
$ pip install awscli
```

Then, the build artifacts are produced using the gradle wrapper:
```
$ ./gradlew build -x test -x docs -x distTar -x distZip -x bootDistTar
```

Next, the artifacts are copied to S3:
```
$ BUILD_ARTIFACT=$(ls -1 build/distributions | tail -n 1)
$ aws s3 cp build/distributions/$BUILD_ARTIFACT $AWS_S3_LOCATION/$BUILD_ARTIFACT
```

### Authentication with AWS S3
The AWS cli, which is used in the deploy stage, makes use of several environment variables for authentication purposes. These variables are configured through the GitLab web interface, and can be added by going to the project’s Settings > CI/CD, then finding the section called Variables, and check “Protected”. Protected variables are only passed to pipelines running on the protected branches or protected tags. The other pipelines would not get any protected variables.

The following environment variables are used by the aws cli during the deploy stage:
- `AWS_ACCESS_KEY_ID`: access key for the AWS user.
- `AWS_DEFAULT_REGION`: default AWS region.
- `AWS_S3_LOCATION`: S3 URI.
- `AWS_SECRET_ACCESS_KEY`: secret access key for the AWS user.

## AWS IAM and VPC Infrastructure
AWS IAM (identity and access management) is used to configure users, groups, rules and policies for all AWS users and services under a root account. Administrators are NOT to use the root account for managing AWS services, and instead should use a child user with the adequate privileges. New users and services should only be given minimum possible access.

### Users
- admin
    - description: full administrative access to all AWS services.
    - security groups: admin
- ktb-dev
    - description: use for development purposes only, with limited access to AWS services.
    - security groups: dev

### IAM Roles
IAM roles are used primarily to control internal access between AWS services.

- CodeDeployServiceRole
    - AWS Service: CodeDeploy
    - Description: Allows CodeDeploy to call AWS services such as Auto Scaling on your behalf.
    - Customer Managed Policies:
            - ParameterStorePolicy
- kick-the-bucket-role
    - AWS Service: EC2
    - Description: Allows EC2 instances to call AWS services on your behalf.
    - Customer Managed Policies:
        - S3ProdAccessPolicy

### VPC
The VPC (virtual private cloud) infrastructure for Kick the Bucket was designed with security as a priority. All Kick the Bucket AWS services are managed under the `kick-the-bucket` VPC.

Two VPC subnets were created for controlling access, `kick-the-bucket-public` and `kick-the-bucket-private`. The public subnet is used for services that are public facing, such as the EC2 instance, while the private subnet is used for internal-only services, such as RDS.

Below is a list of each security group and their function:
- `kick-the-bucket-db-listener`: Used by RDS to allow inbound connections on port 3306 from the EC2 instance only.
- `kick-the-bucket-ssh-listener`: Used to allow SSH access to select source IPs to port 22. Used by the EC2 instance.
- `kick-the-bucket-db-client`: DB client group.
- `kick-the-bucket-web-listener`: Accepts connections to port 80. Used for the EC2 instance running the Spring Boot server to listen to connections from the load balancer.
- `kick-the-bucket-balanced-web-listener`: Accepts connections to ports 80 and 443. Used by the load balancer to route traffic to the EC2 instance running the Spring Boot server.

## AWS CodeDeploy
CodeDeploy is the most important service in the deployment cycle. CodeDeploy is responsible for fetching build artifacts from AWS S3, which were uploaded by the GitLab deployment job runner, and installs them on the production EC2 instance. CodeDeploy is responsible for stopping the Spring Boot server running in EC2, updating the server installation, and restarting the server. It does so through custom deployment scripts which are located in the project under `scripts/`.

A CodeDeploy Agent running on the EC2 instance orchestrates the deployment.

After new artifacts are uploaded to S3, a new deployment can be created using the web interface, or using the AWS CLI:
```
$ BUILD_ARTIFACT=$(ls -1 build/distributions | tail -n 1)
$ aws deploy create-deployment \
    --application-name kick-the-bucket \
    --deployment-config-name CodeDeployDefault.OneAtATime \
    --deployment-group-name kick-the-bucket-prod \
    --description $BUILD_ARTIFACT \
    --s3-location bucket=kick-the-bucket-codedeploy-deployment,bundleType=zip,key=$BUILD_ARTIFACT
```

A complete example on manually packaging and deploying new artifacts is provided in the appendix.

### Parameter Store
AWS parameter store is used to store secured resources, such as passwords or credentials, that must be accessed by the deployment scripts to properly configure the server. Some of the values stored in Parameter Store include the PostgreSQL root user password and the secret key used for JWT. These values are fetched using the AWS CLI by the deployment scripts.

To access these values, a user or service must be assigned the IAM `ParameterStorePolicy` policy. A JSON representation of this policy can be found in the appendix.

New parameters may be created through the AWS console, or usinng the AWS CLI:
```
$ aws ssm put-parameter --name <name> --value "<new value>" --type SecureString --region us-east-1
```

To update a parameter, simply add the `--overwrite` flag:
```
$ aws ssm put-parameter --name <name> --value "<new value>" --type SecureString --region us-east-1 --overwrite
```

## AWS RDS Instance
Amazon Web Services provides a database hosting solution called Amazon Relational Database Service (RDS) for hosting various SQL-based databases. RDS makes it easy to set up, manage, provision, backup and restore data through the AWS platform.

This project relies on a PostgreSQL database hosted through this service. It is hosted in a private RDS instance which is only made available to the EC2 instance running the Spring Boot server. This ensures that the data stored in the database is kept secure.

### Instance Details
- DB Name: `kick-the-bucket-db`
- Region & AZ: `us-east-1a`
- Instance Type: `db.t2.micro`
- Endpoint: `kick-the-bucket-db.cfmacruxeb8i.us-east-1.rds.amazonaws.com`
- Port: `3306`
- Storage Type: `General Purpose (SSD)`
- Storage: `20 GiB`
- Engine Version: `10.6`

### Database Users
At the moment, only the `root` database user exists for managing the database. The root user is used by the Spring Boot server, meaning that connections to the database under the root account may disrupt the Spring Boot server. In the future, more users will be created.

### Accessing Database Console
To access the database console, first connect to the EC2 instance over SSH. First, you will need to fetch the database root user password from the parameter store. To do this, refer to the section on Parameter Store. Then, execute the following:
```
$ psql --host=kick-the-bucket-db.cfmacruxeb8i.us-east-1.rds.amazonaws.com --port=3306 --username=root --password --dbname=kick_the_bucket_prod
```

## AWS EC2 Instance
Amazon EC2 (Amazon Elastic Compute Cloud) is a web service that provides secure, resizable compute capacity in the cloud. It is designed to make web-scale cloud computing easier for developers. The Kick the Bucket project utilizes EC2 for hosting the Spring Boot backend server and making it available to the public.

### Instance Details
- AMI ID: `KTB Prod Image (ami-066168041d5155b9e)`
- Availability Zone: `us-east-1a`
- Instance Type: `t2.small`
- Public IP: `3.85.174.85`
- IAM Role: kick-the-bucket-role

### SSH into EC2 Instance
1. First must ensure that the VPC `kick-the-bucket-ssh-listener` security group is configured with an inbound rule that accepts connections on port 22 from your current IP address.
2. Must ensure that you have an SSH key pair, and your public SSH key has been imported through the EC2 Dashboard.
3. Must ensure that the EC2 instance is configured to use the correct key pair.

```
ssh ec2-user@<ec2 public ip>
```

### Start SSH Session Through Systems Manager
SSH access is limited to users with a specific IP address as specified as an inbound rule in the `kick-the-bucket-ssh-listener` VPC security group. As a result, an SSH connection to the EC2 instance can only be made if the IP matches the IP address defined in the inbound rule, or another inbound rule needs to be created with the new IP address. The latter option may not be ideal if the IP address is not one that is used frequently, or one that changes regularly (such as a public network).

To avoid this, AWS provides a service that allows a user to SSH into the EC2 instance through the web browser. This feature is available through the AWS System Manager from the AWS Management Console.

To start a new session, from the AWS System Manager, click `Session Manager` from the navigation pane, and chose `Start a Session`.

### Instance Libraries and Software
When migrating to a new EC2 instance, it is important to ensure that the new instance is configured with all the necessary software to correctly run the Spring server. At the moment, there are two important pieces of software that must be installed:
- Java JDK 11 must be installed in `/opt`, and the JAVA_HOME environment variable must be set. The PATH variable should also be updated to include `$JAVA_HOME/bin`.
- In the `ec2-user` home directory, a script `configure_shell.sh` should exist. This script is used by CodeDeploy to ensure that the appropriate environment variables exist. Without this, the deployment will fail.

## AWS Elastic Load Balancer
The EC2 instance that hosts the production server is configured such that only port 22 (SSH) is open to the public network. As such, making requests to the EC2 instance IP on ports 80 or 443 will not work. Instead, traffic on ports 80 and 443 are first routed through the public-facing Elastic Load Balancer (ELB). This is to enable secured connections to the server over HTTPS. By using the load balancer to handle HTTPS connections, there is no need to configure the server to use SSL. This avoids the need to manage certificates manually, and instead rely on AWS ACM for certificate management.

The load balancer is configured to redirect traffic on port 443 to port 80 of the EC2 instance. Traffic on port 80 is redirected to HTTPS to ensure that only secured connections may be made to the server.

## AWS ACM Certificate Management
Amazon Web Services provides a certificate management solution which easily provisions trusted certificates for use in a production environment. This service is used to ensure that HTTPS connections to the production environment are secured with valid certificates. It also removes the need to verify ownership of the domain through a third party certificate authority.

## AWS Simple Storage Service (S3)
The Spring server relies on AWS Simple Storage Service (S3) for general purpose object storage. At the moment, S3 is only used for storage user profile pictures, however S3 may be utilized further in the future for storing static content.

For more details on configuring Spring to communicate with AWS S3 for development purposes, see project `README.md`.

### S3 Bucket Configuration
- dev.s3.ktb.brandonrichardson.ca
    - Region: us-east-1
    - Description: User profile picture storage for development purposes.
- prod.s3.ktb.brandonrichardson.ca
    - Region: us-east-1
    - Description: User profile picture storage for production.

## KTB Server Administration
### Administrative Users
Default administrative users are created automatically through the database initialization script `data.sql`, which can be found in `src/main/resources`. The initialization script is configured to be run only in the production environment.

At the moment, only a single administrative user exists and is used exclusively by the Code Deploy Agent to gracefully stop the server during deployment, and to verify that the server is running as expected.

Administrative users should be created sparingly for security purposes. To create a new administrative user in production:
- generate a secure password (preferably 16-32 characters in length), and encrypt using bcrypt using the same scheme used by the server.
    - a valid approach to encrypt the password is to create a new user with the admin password, and query from the database the `password` field for that user.
- update the `src/main/data.sql` with the new user.
    - be sure to pick an admin username that is not taken.
    - be sure to update the script to delete any users that have a username matching the new admin username.
- restart the server.

Read more about [Database Initialization in Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html).

### Actuator Endpoints
Actuator endpoints let you monitor and interact with the server. Spring Boot includes a number of built-in endpoints and lets you add your own. For example, the health endpoint provides basic application health information.

By default, all actuator endpoints are disabled, with the exception of the `/health` and `/shutdown` endpoints. To use the endpoints, only an authorized administrative user (with ROLE_ADMIN) is permitted to use the actuator endpoints.

Read more about [Actuator Endpoints](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html).

## Appendix
### Manually Deploy Build Artifacts
```
$ # First, add environment variables for using AWS CLI
$ export AWS_ACCESS_KEY_ID=<aws access key>
$ export AWS_DEFAULT_REGION=<aws region>
$ export AWS_SECRET_ACCESS_KEY=<aws secret key>
$
$ ./gradlew build -x test -x docs -x distTar -x distZip -x bootDistTar
$ BUILD_ARTIFACT=$(ls -1 build/distributions | tail -n 1)
$ aws s3 cp build/distributions/$BUILD_ARTIFACT s3://kick-the-bucket-codedeploy-deployment/$BUILD_ARTIFACT
$ aws deploy create-deployment --application-name kick-the-bucket --deployment-config-name CodeDeployDefault.OneAtATime --deployment-group-name kick-the-bucket-prod --description $BUILD_ARTIFACT --s3-location bucket=kick-the-bucket-codedeploy-deployment,bundleType=zip,key=$BUILD_ARTIFACT
```

### Updating Parameter Store Secured Strings Using AWS CLI
```
$ # Update PostgreSQL root password
$ aws ssm put-parameter --name KTBPostgreSQLDBPASS --value "<new value>" --type SecureString --region us-east-1 --overwrite
$ # Update JWT secret
$ aws ssm put-parameter --name KTBJWTSecretKey --value "<new value>" --type SecureString --region us-east-1 --overwrite
```

### IAM ParameterStorePolicy for CodeDeploy Service Role
```
{
    "Version": "2012-10-17",
    "Statement": [{
        "Effect": "Allow",
        "Action": [
            "ssm:DescribeParameters"
        ],
        "Resource": "*"
    },{
        "Effect": "Allow",
        "Action": [
            "ssm:GetParameters"
        ],
        "Resource": [
            "arn:aws:ssm:us-east-1:532191574896:parameter/KTBPostgreSQLDBPASS",
            "arn:aws:ssm:us-east-1:532191574896:parameter/KTBJWTSecretKey"
        ]
    },{
        "Effect": "Allow",
        "Action": [
            "kms:Decrypt"
        ],
        "Resource": "arn:aws:kms:us-east-1:532191574896:alias/aws/ssm"
    }]
}
```

### IAM S3DevAccessPolicy for S3 Access During Development
```
{
    "Version": "2012-10-17",
    "Statement": [{
        "Effect": "Allow",
        "Action": [
            "s3:ListBucket"
        ],
        "Resource": [
            "arn:aws:s3:::dev.s3.ktb.brandonrichardson.ca"
        ]
    },{
        "Effect": "Allow",
        "Action": [
            "s3:PutObject",
            "s3:GetObject",
            "s3:DeleteObject"
        ],
        "Resource": [
            "arn:aws:s3:::dev.s3.ktb.brandonrichardson.ca/*"
        ]
    }]
}
```

### Update PostgreSQL Root User Password
```
$ psql --host=kick-the-bucket-db.cfmacruxeb8i.us-east-1.rds.amazonaws.com --port=3306 --username=root --password --dbname=kick_the_bucket_prod

from psql console:
ALTER USER root PASSWORD '<new password>';
```

### Configuring iptables on New Instance
```
sudo iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080

# view changes
sudo iptables --table nat --list
```