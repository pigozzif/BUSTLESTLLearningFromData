package localSearch.gpOptimisation;

import localSearch.numeric.optimization.ObjectiveFunction;

public interface NoisyObjectiveFunction extends ObjectiveFunction {
	
	double getVarianceAt(double... point);

}
