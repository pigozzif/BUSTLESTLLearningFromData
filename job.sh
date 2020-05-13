#!/bin/bash

NUM_PROCS=10

for i in $(seq 2 $NUM_PROCS); do
	java -classpath libs/moonlight.jar:libs/JGEA-1.0-SNAPSHOT.jar:main Main random=$i output_name=$i;
done
