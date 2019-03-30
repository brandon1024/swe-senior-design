#!/bin/bash

##
## usage: server-validate.sh
##
## Description:
## This script is used by the Code Deploy agent to verify that the server started successfully.
##
## Polls http://localhost:8080 every 3 seconds until 'Success' is received, or exists after 3 minutes.
##
## Author: Brandon Richardson
## Note: This script should be used with caution. Use of this script may cause unintended behavior.
##

# Just in case this is run in an environment without a proper shell
. /home/ec2-user/configure_shell.sh

# Retrieve CodeDeployAdmin user password
password=$(aws ssm get-parameter --region us-east-1 --name KTBCodeDeployAdminUserPass --with-decryption --query Parameter.Value)
CD_ADMIN_PASS=$(echo $password | sed -e 's/^"//' -e 's/"$//')

AUTH_TOKEN=
for run in {1..60}
do
    AUTH_TOKEN=$(curl -sf \
            -H "Content-Type: text/plain" \
            -d "$CD_ADMIN_PASS" \
            http://localhost:8080/auth/authenticate?username=codedeployadmin)

    if [ "$?" -eq "0" ]; then
        echo "Successfully authenticated with server. Fetching server health details..."

        STATUS=$(curl -sf -H "Authorization: Bearer $AUTH_TOKEN" http://localhost:8080/actuator/health)

        KEYS=$(echo $STATUS | grep -i -o 'DOWN\|OUT_OF_SERVICE\|UNKNOWN')
        if [ "$?" -eq "0" ] || [ -z "$KEYS" ]; then
            echo "Health actuator reported server started successfully."
            exit 0
        else
            echo "Health actuator reported that a service failed to start. Details:"
            echo $STATUS
            exit 1
        fi
    fi

    echo "Could not authenticate with server at http://localhost:8080/auth/authenticate. Trying again in 3s..."
    sleep 3s
done

echo "Could not reach server. Exiting..."
exit 1