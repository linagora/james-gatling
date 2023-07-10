#!bin/bash

if [ -z "$1" ]; then
    sbt
else
    echo "Running Gatling tests for $1"
    sbt "gatling:testOnly $1"
fi
