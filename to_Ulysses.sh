#!/bin/bash

# javac -classpath ./libs/moonlight.jar:./libs/JGEA.jar:main ./main/Main.java
#scp -r /Users/federicopigozzi/IdeaProjects/STLRealTrafficRulesEvolutionaryInference/main fpigozzi@login.galileo.cineca.it:~/STLRealTrafficRulesEvolutionaryInference
scp -r /Users/federicopigozzi/Downloads/jdk-14.0.1/ fpigozzi@login.galileo.cineca.it:~/STLRealTrafficRulesEvolutionaryInference
rm ./main/*.class
rm ./main/BuildingBlocks/*.class
rm ./main/Expressions/*.class
rm ./main/Expressions/MonitorExpressions/*.class
rm ./main/Expressions/ValueExpressions/*.class
