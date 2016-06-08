#!/usr/bin/env bash

# This script feeds data into the James server for the scenario JmapAuthentication
#
# Argment $1 : The cotainer ID of your James server

docker exec $1 java -jar /root/james-cli.jar -h 127.0.0.1 -p 9999 adddomain domain-jmapauthentication.tld
docker exec $1 java -jar /root/james-cli.jar -h 127.0.0.1 -p 9999 adduser username@domain-jmapauthentication.tld password