#!/bin/bash

START=0
END=9

for i in $(seq $START $END); do
	java -cp ./target/STLRealTrafficRuleEvolutionaryInference.jar Main seed=$i grammar=./grammars/grammar_maritime.bnf output=./output/maritime/$i.csv input=./data/naval local_search=false;
done
