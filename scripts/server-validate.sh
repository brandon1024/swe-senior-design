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

set -e

# Just in case this is run in an environment without a proper shell
. /etc/environment

for run in {1..60}
do
    if [ "$(curl -s http://localhost:8080/)" = 'Success' ]
    then
        exit 0
    else
        echo "Could not reach server at http://localhost:8080. Trying again in 3s..."
        sleep 3s
    fi
done

exit 1