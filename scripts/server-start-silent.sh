#!/bin/bash

##
## usage: server-start-silent.sh
##
## Description:
## This script is used by the Code Deploy agent to start the spring server in the background. This script redirects
## all console output to '/home/ec2-user/server/logs/', and immediately exits to prevent Code Deploy from stalling.
##
## Author: Brandon Richardson
## Note: This script should be used with caution. Use of this script may cause unintended behavior.
##

set -e

# Just in case this is run in an environment without a proper shell
. /etc/environment

if [ -z "$JAVA_HOME" ]; then
    echo "Could not find java installation."
    exit 1
fi

cd /home/ec2-user/server/scripts
mkdir -p /home/ec2-user/server/logs
nohup ./server-start.sh > /home/ec2-user/server/logs/server.out 2> /home/ec2-user/server/logs/server.err < /dev/null &