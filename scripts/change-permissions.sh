#!/bin/bash
set -e

# Just in case this is run in an environment without a proper shell
. /etc/environment

chmod +x /home/ec2-user/server/bin/*
chmod +x /home/ec2-user/server/lib/*
chmod +x /home/ec2-user/server/scripts/*