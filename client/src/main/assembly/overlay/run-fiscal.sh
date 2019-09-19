#!/bin/bash

java -DserverAddress=$1:$2 -Did=$3 -cp 'lib/jars/*' "ar.edu.itba.pod.fiscal.FiscalClient" $*

