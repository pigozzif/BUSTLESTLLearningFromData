package LocalSearch.gpOptimisation;

import LocalSearch.numeric.optimization.ObjectiveFunction;

public interface NoisyObjectiveFunction extends ObjectiveFunction {
	
	double getVarianceAt(double... point);

}
