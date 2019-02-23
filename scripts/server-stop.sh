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

set -e

# Just in case this is run in an environment without a proper shell
. /etc/environment

REMOVE_INSTALL=1
for arg in "$@"; do
    case $arg in
        -s|--save)
        REMOVE_INSTALL=0
        shift
        ;;
        *)
        echo "Unknown option: $arg"
        help
        ;;
    esac
done

# Stop server if running
if [ -d /home/ec2-user/server ] && [ -f /home/ec2-user/server/bin/pid.file ]; then
    kill $(cat /home/ec2-user/server/bin/pid.file)
    rm /home/ec2-user/server/bin/pid.file
else
    echo "No pid.file found. Assuming server is not running."
fi

# Remove server installation
if [ "$REMOVE_INSTALL" -eq "1" ]; then
    rm -rf /home/ec2-user/server
fi