#!/bin/bash

START=0
END=9
#NUM_PROCS=10
#javac -classpath ./libs/moonlight.jar:./libs/JGEA.jar:main:libs/jblas-1.2.4.jar ./main/Main.java

for i in $(seq $START $END); do
	#java -classpath libs/moonlight.jar:libs/JGEA.jar:main:libs/jblas-1.2.4.jar Main random=$i output_name=$i;
	java -cp ./target/STLRealTrafficRuleEvolutionaryInference.jar Main seed=$i grammar=./grammars/grammar_maritime.bnf output=./output/maritime/$i.csv input=./data/naval local_search=false;
done

#rm ./main/**/**/*.class
#rm ./main/**/*.class
#rm ./main/*.class
