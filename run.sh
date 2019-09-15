#!/bin/bash

mvn clean install

cd ./client/target
tar -xzf rmi-client-1.0-SNAPSHOT-bin.tar.gz
chmod -R +x ./rmi-client-1.0-SNAPSHOT

cd ../../server/target
tar -xzf rmi-server-1.0-SNAPSHOT-bin.tar.gz
chmod -R +x ./rmi-server-1.0-SNAPSHOT
cd ../..

open -a Terminal "`pwd`/run-registry.sh"
open -a Terminal "`pwd`/run-server.sh"
