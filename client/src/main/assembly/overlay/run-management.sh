#!/bin/bash

java -DserverAddress=$1:$2 -Daction=$3 -cp 'lib/jars/*' "ar.edu.itba.pod.management.ManagementClient" $*

