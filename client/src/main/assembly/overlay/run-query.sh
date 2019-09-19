#!/bin/bash

java -DserverAddress=$1:$2 -Did=$3 -DoutPath=$4 -cp 'lib/jars/*' "ar.edu.itba.pod.query.QueryClient" $*

