#!/bin/bash

##
## usage: server-restart.sh
##
## Description:
## Restart the spring server manually without fetching new artifacts. This script is intended to be executed by administrators
## in the event the server becomes unresponsive, or during testing stages.
##
## This script should be used instead of 'server-stop.sh' and 'server-stop.sh', which were written to be executed
## by the Code Deploy agent.
##
## Author: Brandon Richardson
## Note: This script should be used with caution. Use of this script may cause unintended behavior.
##

set -e

# Just in case this is run in an environment without a proper shell
. /home/ec2-user/configure_shell.sh

cd /home/ec2-user/server/scripts
./server-stop.sh --save
./server-start-silent.sh