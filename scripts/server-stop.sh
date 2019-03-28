#!/bin/bash

##
## usage: server-stop.sh
##
## options:
##  -s, --save: Do not purge server installation
##
## Description:
## This script is used by the Code Deploy agent to stop the server and purge the server installation before a new
## installation.
##
## Author: Brandon Richardson
## Note: This script should be used with caution. Use of this script may cause unintended behavior.
##

# Just in case this is run in an environment without a proper shell
. /home/ec2-user/configure_shell.sh

REMOVE_INSTALL=1
for arg in "$@"; do
    case $arg in
        -s|--save)
        REMOVE_INSTALL=0
        shift
        ;;
        *)
        echo "Unknown option: $arg"
        exit 1
        ;;
    esac
done

# Gracefully stop the server
password=$(aws ssm get-parameters --region us-east-1 --names KTBCodeDeployAdminUserPass --with-decryption --query Parameters[0].Value)
CD_ADMIN_PASS=$(echo $password | sed -e 's/^"//' -e 's/"$//')

AUTH_TOKEN=$(curl -sf \
        -H "Content-Type: text/plain" \
        -d "$CD_ADMIN_PASS" \
        http://localhost:8080/auth/authenticate?username=codedeployadmin)

if [ "$?" -eq "0" ]; then
    echo "Successfully authenticated with server. Fetching server health details..."

    STATUS=$(curl -sf -H "Authorization: Bearer $AUTH_TOKEN" http://localhost:8080/actuator/shutdown)

    if [ "$?" -ne "0" ]; then
        if [ -d /home/ec2-user/server/bin ] && [ -f /home/ec2-user/server/bin/pid.file ]; then
            rm /home/ec2-user/server/bin/pid.file
        fi

        # Remove server installation
        if [ "$REMOVE_INSTALL" -eq "1" ]; then
            rm -rf /home/ec2-user/server
        fi

        exit 0
    else
        echo "Could not stop server using shutdown actuator. Resorting to forceful termination..."
    fi
else
    echo "Could not authenticate with server at http://localhost:8080/auth/authenticate. Resorting to forceful termination..."
fi

# Force stop server if running
if [ -d /home/ec2-user/server/bin ] && [ -f /home/ec2-user/server/bin/pid.file ]; then
    kill $(cat /home/ec2-user/server/bin/pid.file)
    rm /home/ec2-user/server/bin/pid.file
else
    echo "No pid.file found. Assuming server is not running."
fi

# Remove server installation
if [ "$REMOVE_INSTALL" -eq "1" ]; then
    rm -rf /home/ec2-user/server
fi