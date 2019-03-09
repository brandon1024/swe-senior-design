#!/bin/bash

##
## usage: server-start.sh
##
## Description:
## This script is used by the Code Deploy agent to configure and start the server in the production environment.
## The script fetches secure strings from SSM parameter store, and invokes the kick-the-bucket executable.
##
## Author: Brandon Richardson
## Note: This script should be used with caution. Use of this script may cause unintended behavior.
##

set -e

# Just in case this is run in an environment without a proper shell
. /home/ec2-user/configure_shell.sh

cd /home/ec2-user/server/bin

# Set Production Externalized Configuration Profile
export SPRING_PROFILES_ACTIVE=prod

# Retrieve DB Password
password=$(aws ssm get-parameters --region us-east-1 --names KTBPostgreSQLDBPASS --with-decryption --query Parameters[0].Value)
DB_PASS=$(echo $password | sed -e 's/^"//' -e 's/"$//')

# Retrieve JWT Secret
password=$(aws ssm get-parameters --region us-east-1 --names KTBJWTSecretKey --with-decryption --query Parameters[0].Value)
JWT_SECRET=$(echo $password | sed -e 's/^"//' -e 's/"$//')

# Run Server
SERVER_LOGS_PATH=/home/ec2-user/logs
./kick-the-bucket --spring.datasource.password=$DB_PASS --jwt.secret=$JWT_SECRET --logging.path=$SERVER_LOGS_PATH & echo $! > ./pid.file &