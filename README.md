# Software Engineering Senior Design Project 

# Setup Application for Development
You will need to install the [IntelliJ IDE from JetBrains](https://www.jetbrains.com/idea/). You can download the community edition for free, or use your student email to get the complete version. This walkthrough also assumes you are developing on a Unix-based system.

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

Next you will need to add your SSH public key to your GitLab account. 

Clone the repository into your IntelliJ working directory.

```
git clone git@gitlab.com:brandon1024/swe-senior-design.git
cd swe-senior-design
```

Once complete, you can import the project into IntelliJ by selecting `Open Project` and selecting the `swe-senior-design` directory.

## Database Configuration
This project relies on PostgreSQL, so you will need to ensure you have it installed. If you don't, here are some links to get you started:
- [Download PostgreSQL](https://www.postgresql.org/download/)

Although it isn't necessarily required, JetBrains DataGrip is a very useful tool for managing the database users and schemas. Once you have DataGrip installed, create a new connection with the following parameters:
- Connection Name: swe-senior-design (note: this name is irrelevant)
- Hostname: 127.0.0.1
- Port: 3306
- Username: root
- Password: password

Click the `Test Connection` to ensure you're able to connect to the PostgreSQL server.

TODO

## Database Migrations
TODO

## IDE Configurationn and Run Configurations
First and foremost, you will need to configure the project codestyle settings. These settings are already configured in the repository `.idea` folder, but you will need to ensure you are using the project settings and not the system settings. To do this, navigate to the IntelliJ preferences window, select `Editor`, then select the `Code Style` top level node. In the `Scheme` dropdown, select `Project`.

TODO

## Running the Server
TODO

## Authors
|[<img src="https://avatars3.githubusercontent.com/u/22732449?v=3&s=460" width="128">](https://github.com/brandon1024)|
|:---:|:---:|:---:|:---:|
|[Brandon Richardson](https://github.com/brandon1024)|
|<sup>Software Engineering</sup>|

## License
This software is available under the [MIT License](https://opensource.org/licenses/MIT).