# Software Engineering Senior Design Project 
The code is organized as follows:
- `api` contains all the web service controllers that provides various APIs and security features
- `core` is the business logic layer, which includes BO entities and services
- `application` is where the high-level services exist, which includes DAOs and adapters
- `infrastructure` contains all the implementation classes as the technique details


# Setup Environment for Development
You will need to install the [IntelliJ IDE from JetBrains](https://www.jetbrains.com/idea/). You can download the community edition for free, or use your student email to get the complete version. This walkthrough also assumes you are developing on a Unix-based system.

You will also need to download and install [Java JDK 10](http://www.oracle.com/technetwork/java/javase/downloads/jdk10-downloads-4416644.html). You can verify which version you are using by running `java --version` at the command line.

## Environment Setup
First, you'll need to checkout a copy of the repository on your local machine. All communication with the remote repository should be done over SSH, so you must first generate an SSH keypair and link it to your GitLab account. You can find the complete details [here](https://docs.gitlab.com/ee/ssh/).

First, you can check if you have already generated an SSH keypair by running the following in the terminal.
```
ls -la ~/.ssh
```

To generate a new SSH keypair, run the following and follow the prompts:
```
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
```

Next, you can add the SSH key to your key-agent:
```
eval "$(ssh-agent -s)"
ssh-add -K ~/.ssh/id_rsa
```

Next you will need to add your SSH public key to your GitLab account. Once your public key is associated to your GitLab account, you can clone the repository into your IntelliJ working directory.

```
git clone git@gitlab.com:brandon1024/swe-senior-design.git
cd swe-senior-design
```

Once complete, you can import the project into IntelliJ by selecting `Import Project` and selecting the `swe-senior-design` directory. Select `Import Project from External Model` and select `Gradle` from the list. Use the default settings and import the project.

## IDE Configuration and Run Configurations
First and foremost, you will need to configure the project Code Style settings. These settings are already configured in the repository `.idea` folder, but you will need to ensure you are using the project settings and not the system settings. To do this, navigate to the IntelliJ preferences window, select `Editor`, then select the `Code Style` top level node. In the `Scheme` dropdown, select `Project`.

Next you will need to configure Lombok. The [Lombok Project](https://projectlombok.org/) is a Java library that uses annotations to simplify your code. You will first need to install the Lombok plugin by navigating to the preferences window, select `Plugins`, then press the `Browse Repositories` button. Search and install `Lombok Plugin`.

In the preferences window, you will also need to enable `Enable Annotation Processing`, which can be found by selecting `Build, Execution, Deployment`, then `Compiler`, then `Annotation Preprocessors`.

## Database Configuration
This project relies on PostgreSQL, so you will need to ensure you have it installed. If you don't, here are some links to get you started:
- [Download PostgreSQL](https://www.postgresql.org/download/)

Using the PG Admin tool, you can create a new database named `before_i_go_dev`. You will also need to create a new user named `root` with password `password`.

Although it isn't necessarily required, JetBrains DataGrip is a very useful tool for managing the database users and schemas. Once you have DataGrip installed, create a new connection with the following parameters:
- Connection Name: swe-senior-design (note: this name is irrelevant)
- Hostname: 127.0.0.1
- Port: 3306
- Username: root
- Password: password

Click the `Test Connection` to ensure you're able to connect to the PostgreSQL server.

## Running the Server
Run the following at the command line from the root of the project.
```
./gradlew bootRun
```

In your web browser, navigate to `http://localhost:8080`.

## Useful Resources
- [Spring Guides](https://spring.io/guides)
- [Spring JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/2.0.9.RELEASE/reference/html/)
- [Lombok Project](https://projectlombok.org/features/all)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [jUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Microsoft REST API Guidelines](https://github.com/Microsoft/api-guidelines/blob/vNext/Guidelines.md)

## Authors
|[<img src="https://avatars3.githubusercontent.com/u/22732449?v=3&s=460" width="128">](https://github.com/brandon1024)|
|:---:|
|[Brandon Richardson](https://github.com/brandon1024)|
|<sup>Software Engineering</sup>|

## License
This software is available under the [MIT License](https://opensource.org/licenses/MIT).
