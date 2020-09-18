#!/bin/bash

javac -classpath ./libs/moonlight.jar:./libs/JGEA.jar:main ./main/Main.java
scp -r /Users/federicopigozzi/IdeaProjects/STLRealTrafficRulesEvolutionaryInference/main fpigozzi@frontend1.hpc.sissa.it:~/STLRealTrafficRulesEvolutionaryInference/main2
rm ./main/*.class
rm ./main/BuildingBlocks/*.class
rm ./main/Expressions/*.class
rm ./main/Expressions/MonitorExpressions/*.class
rm ./main/Expressions/ValueExpressions/*.class
