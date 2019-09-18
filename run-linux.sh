#!/bin/bash

mvn clean install

cd ./client/target
tar -xzf rmi-client-1.0-SNAPSHOT-bin.tar.gz
chmod -R +x ./rmi-client-1.0-SNAPSHOT

cd ../../server/target
tar -xzf rmi-server-1.0-SNAPSHOT-bin.tar.gz
chmod -R +x ./rmi-server-1.0-SNAPSHOT
cd ../..

gnome-terminal --command="`pwd`/run-registry.sh"
gnome-terminal --command="`pwd`/run-server.sh"
