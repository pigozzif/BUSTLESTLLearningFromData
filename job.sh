#!/bin/bash

NUM_PROCS=10
javac -classpath ./libs/moonlight.jar:./libs/JGEA-1.0-SNAPSHOT.jar:main ./main/Main.java

for i in $(seq 1 $NUM_PROCS); do
	java -classpath libs/moonlight.jar:libs/JGEA-1.0-SNAPSHOT.jar:main Main random=$i output_name=$i;
done

rm ./main/*.class
rm ./main/BuildingBlocks/*.class
rm ./main/Expressions/*.class
rm ./main/Expressions/MonitorExpressions/*.class
rm ./main/Expressions/ValueExpressions/*.class
