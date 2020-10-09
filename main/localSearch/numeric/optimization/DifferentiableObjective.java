package localSearch.numeric.optimization;

public interface DifferentiableObjective extends ObjectiveFunction {

	double[] getGradientAt(double... point);

}
