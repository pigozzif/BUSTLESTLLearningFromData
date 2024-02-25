# BUSTLE: a Versatile Tool for the Evolutionary Learning of STL Specifications from Data

This is the official repository for the *Evolutionary Computation* paper "BUSTLE: a Versatile Tool for the Evolutionary Learning of STL Specifications from Data", hosting all the code necessary to replicate the experiments. This work is mostly based on Federico Pigozzi's master's thesis.

## Scope
By running
```
mkdir output
java -cp libs/JGEA.jar:libs/moonlight.jar:libs/jblas-1.2.4.jar:target/STLRuleEvolutionaryInference.jar it.units.malelab.learningstl.Main {args}
```
Where `{args}` is a placeholder for the arguments you must provide (see below), you will launch a grammar-based evolutionary optimization of the formula structure and parameters for Signal Temporal Logic rules. At the same time, several evolution metadata will be saved inside the `output` folder. The project has been tested with Java `14.0.2`.

### Warning
On more recent Linux versions, dynamic libraries for jblas may not link properly. In this case, run the following command:
```
unzip dlibs.zip
```
to unzip a directory containing the necessary .so files, then
```
sudo mv dlibs/libgfortran.so.3.0.0 /usr/local/lib/
sudo mv dlibs/libquadmath.so.0.0.0 /usr/local/lib/
sudo ldconfig
```
To move the .so files to the appropriate place and configure them.

## Structure
* `src` contains all the source code for the project;
* `libs` contains the .jar files for the dependencies (see below);
* `grammars` contains the .bnf files with the grammars;
* `data` contains three datasets;
* `target` contains the main .jar file;
* `dlibs.zip` is an emergency kit for the aforementioned warning.

## Dependencies
The project relies on:
* [JGEA](https://github.com/ericmedvet/jgea) for the evolutionary optimization;
* [MoonLight](https://github.com/MoonLightSuite/MoonLight) for monitoring signal-temporal logic formulae.

The corresponding jars have already been included in the directory `libs`. See `pom.xml` for more details on dependencies.

## Usage
This is a table of possible command-line arguments:

Argument       | Type                                         | Optional (yes/no) | Default
---------------|----------------------------------------------|-------------------|-------------------------
seed           | integer                                      | no                | -
dataset        | string                                       | no                | -
output         | string                                       | yes               | ./output/
mod            | {anomaly,supervisde}                         | no                | -
alpha          | float                                        | no                | -
local          | {true,false}                                 | no                | -
evolver        | {bustle,roge,random}                         | yes               | bustle
threads        | integer                                      | yes               | # available cores on CPU

The description for each argument is as follows:
* seed: the random seed for the experiment;
* dataset: the name of the dataset;
* output: a (relative) path to the directory to save output files into;
* mod: the modality (semi- or fully-supervised);
* alpha: the alpha penalty;
* local: apply local search or not;
* evolver: the EA for the experiments;
* threads: the number of threads with which to perform evolution. Defaults to the number of available cores on the current CPU. Parallelization is taken care of by JGEA, which implements a distributed fitness assessment.

## Bibliography
Please cite as:
```
@article{pigozzi2024bustle,
  title={BUSTLE: a Versatile Tool for the Evolutionary Learning of STL Specifications from Data},
  author={Pigozzi, Federico and Medvet, Eric and Nenzi, Laura},
  journal={Evolutionary Computation},
  year={2024},
  publisher={MIT Press}
}
```
