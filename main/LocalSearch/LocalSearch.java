package LocalSearch;

import TreeNodes.AbstractTreeNode;
import BuildingBlocks.FitnessFunctions.AbstractFitnessFunction;
import LocalSearch.gpOptimisation.GPOptimisation;
import LocalSearch.gpOptimisation.GpoOptions;
import LocalSearch.numeric.optimization.ObjectiveFunction;
import LocalSearch.sampler.GridSampler;
import LocalSearch.sampler.Parameter;

import java.util.List;
import java.util.stream.IntStream;


public class LocalSearch {

    public static double[] optimize(AbstractTreeNode monitor, AbstractFitnessFunction<?> ff, int maxIterations) {
        double[] timeBounds = ff.getSignalBuilder().getTemporalBounds();
        List<String[]> variables = monitor.getVariables();
        int numVariables = variables.size();
        int numBounds = monitor.getNumBounds();
        //Map<String, double[]> temp = ff.getSignalBuilder().getVarsBounds();
        double[] lb = new double[numBounds + numVariables];
        double[] ub = new double[numBounds + numVariables];
        for (int i = 0; i < numBounds; ++i) {
            lb[i] = (i % 2 == 0) ? timeBounds[0] : 1;
            ub[i] = timeBounds[1];
        }
        for (int j = 0; j < numVariables; ++j) {
            lb[j + numBounds] = 0.0;//temp.get(variables.get(j)[0])[0];
            ub[j + numBounds] = 1.0;//temp.get(variables.get(j)[0])[1];
        }
        ObjectiveFunction function = point -> {
            final double[] p = point;
            point = IntStream.range(0, point.length).mapToDouble(i -> lb[i] + p[i] * (ub[i] - lb[i])).toArray();
            monitor.propagateParameters(point);
            return ff.getObjective().apply(monitor, point);
        };
        GPOptimisation gpo = createOptimizer(numBounds, maxIterations);
        double[] lbU = IntStream.range(0, lb.length).mapToDouble(i -> 0).toArray();
        double[] ubU = IntStream.range(0, ub.length).mapToDouble(i -> 1).toArray();
        double[] v = gpo.optimise(function, lbU, ubU).getSolution();
        return IntStream.range(0, v.length).mapToDouble(i -> lb[i] + v[i] * (ub[i] - lb[i])).toArray();
    }

    public static GPOptimisation createOptimizer(int numBounds, int maxIterations) {
        GridSampler custom = createSampler(numBounds);
        GPOptimisation gpo = new GPOptimisation();
        GpoOptions options = new GpoOptions();
        options.setInitialSampler(custom);
        options.setMaxIterations(maxIterations);
        options.setHyperparamOptimisation(true);
        options.setUseNoiseTermRatio(true);
        options.setNoiseTerm(0);
        options.setGridSampler(custom);
        options.setGridSampleNumber(200);
        gpo.setOptions(options);
        return gpo;
    }

    public static GridSampler createSampler(int numBounds) {
        return new GridSampler() {
            @Override
            public double[][] sample(int n, double[] lbounds, double[] ubounds) {
                double[][] res = new double[n][lbounds.length];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < numBounds; j += 2) {
                        res[i][j] = (int) lbounds[j] + (Math.random() * (ubounds[j] - lbounds[j]));
                        res[i][j + 1] = (int) Math.max(1.0, res[i][j] + (Math.random() * (ubounds[j + 1] - res[i][j])));
                    }
                    for (int j = numBounds; j < res[i].length; j++) {
                        res[i][j] = lbounds[j] + Math.random() * (ubounds[j] - lbounds[j]);
                    }
                }
                return res;
            }

            @Override
            public double[][] sample(int n, Parameter[] params) {
                return new double[0][];
            }
        };
    }

}
