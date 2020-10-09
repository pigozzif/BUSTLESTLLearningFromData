#!/bin/bash

NUM_PROCS=10
javac -classpath ./libs/moonlight.jar:./libs/JGEA.jar:main:libs/jblas-1.2.4.jar ./main/Main.java

for i in $(seq 10 $NUM_PROCS); do
	java -classpath libs/moonlight.jar:libs/JGEA.jar:main:libs/jblas-1.2.4.jar Main random=$i output_name=$i;
done

rm ./main/**/**/*.class
rm ./main/**/*.class
rm ./main/*.class
