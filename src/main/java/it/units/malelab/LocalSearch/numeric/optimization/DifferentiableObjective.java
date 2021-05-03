package it.units.malelab.LocalSearch.numeric.optimization;

public interface DifferentiableObjective extends ObjectiveFunction {

	double[] getGradientAt(double... point);

}
