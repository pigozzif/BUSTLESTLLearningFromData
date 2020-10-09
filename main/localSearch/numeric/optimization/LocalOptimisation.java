package localSearch.numeric.optimization;


public abstract class LocalOptimisation {

	abstract public PointValue optimise(ObjectiveFunction func,
			double[] init);

}
