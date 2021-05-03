package it.units.malelab.LocalSearch.gpOptimisation;

import it.units.malelab.LocalSearch.numeric.optimization.ObjectiveFunction;

public interface NoisyObjectiveFunction extends ObjectiveFunction {
	
	double getVarianceAt(double... point);

}
