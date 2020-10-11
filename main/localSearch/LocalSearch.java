package localSearch;

import BuildingBlocks.FitnessFunctions.AbstractFitnessFunction;
import BuildingBlocks.TreeNode;
import localSearch.gpOptimisation.GPOptimisation;
import localSearch.gpOptimisation.GpoOptions;
import localSearch.numeric.optimization.ObjectiveFunction;
import localSearch.sampler.GridSampler;
import localSearch.sampler.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;


public class LocalSearch {
    // TODO: introduce parameter object
    public static double[] optimize(TreeNode monitor, AbstractFitnessFunction<?> ff, int maxIterations) {
        double[] timeBounds = ff.getSignalBuilder().getTemporalBounds();
        List<String[]> variables = monitor.getVariables();
        int numVariables = variables.size();
        int numBounds = monitor.getNumBounds();
        HashMap<String, double[]> temp = ff.getSignalBuilder().getVarsBounds();
        double[] lb = new double[numBounds + numVariables];
        double[] ub = new double[numBounds + numVariables];
        for (int i = 0; i < numBounds; ++i) {
            lb[i] = (i % 2 == 0) ? timeBounds[0] : 1;
            ub[i] = timeBounds[1];
        }
        for (int j = 0; j < numVariables; ++j) {
            lb[j + numBounds] = temp.get(variables.get(j)[0])[0];
            ub[j + numBounds] = temp.get(variables.get(j)[0])[1];
        }
        ObjectiveFunction function = point -> {
            final double[] p = point;
            point = IntStream.range(0, point.length).mapToDouble(i -> lb[i] + p[i] * (ub[i] - lb[i])).toArray();
            monitor.propagateParameters(point);
            return ff.getObjective().apply(monitor, point);
        };
        GridSampler custom = new GridSampler() {
            @Override
            public double[][] sample(int n, double[] lbounds, double[] ubounds) {
                double[][] res = new double[n][lbounds.length];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < numBounds; j += 2) {
                        res[i][j] = lbounds[j] + (Math.random() * (ubounds[j] - lbounds[j]));
                        res[i][j + 1] = (Math.random() * (ubounds[j + 1] - res[i][j]));
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
        double[] lbU = IntStream.range(0, lb.length).mapToDouble(i -> 0).toArray();
        double[] ubU = IntStream.range(0, ub.length).mapToDouble(i -> 1).toArray();
        double[] v = gpo.optimise(function, lbU, ubU).getSolution();
        double[] vv = IntStream.range(0, v.length).mapToDouble(i -> lb[i] + v[i] * (ub[i] - lb[i])).toArray();
        //double[] p1u1 = this.computeRobustness(monitor, this.positiveTraining, this.numPositive, vv);
        //double[] p2u2 = this.computeRobustness(monitor, this.negativeTraining, this.numNegative, vv);
        //double value;
        /*if (p1u1[0] > p2u2[0]) {
            value = ((p1u1[0] - p1u1[1]) + (p2u2[0] + p2u2[1])) / 2;
        } else {
            value = ((p2u2[0] - p2u2[1]) + (p1u1[0] + p1u1[1])) / 2;
        }
        for (int i = numBounds; i < vv.length; i++) {
            if (variables.get(i - numBounds)[1].equals(">")) {  // TODO: a little bit hardcoded
                vv[i] = Math.max(vv[i] + value, 0);
            } else {
                vv[i] = Math.max(vv[i] - value, 0);
            }
        }*/
        return vv;
    }

}
