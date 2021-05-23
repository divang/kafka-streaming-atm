#!/bin/sh

export CONTAINER_IP=$(hostname -i)
echo $CONTAINER_IP
java -jar -DCONTAINER_IP=$CONTAINER_IP atm-1.0.jar