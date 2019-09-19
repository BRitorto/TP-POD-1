#!/bin/bash

java -DserverAddress=$1:$2 -DvotesPath=$3 -cp 'lib/jars/*' "ar.edu.itba.pod.vote.VoteClient" $*

