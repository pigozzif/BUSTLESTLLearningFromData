
import eu.quanticol.moonlight.formula.BooleanDomain;
import eu.quanticol.moonlight.formula.DoubleDomain;
import eu.quanticol.moonlight.formula.Interval;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;
import eu.quanticol.moonlight.util.Pair;
import eu.quanticol.moonlight.util.TestUtils;


public class Test {

    public static void main(String[] args) {
        /*int n = 1000000;
        int numBounds = 2;
        int[] lbounds = new int[]{0, 0};
        int[] ubounds = new int[]{30, 30};
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < numBounds; j += 2) {
                double temp = lbounds[j] + (int) (Math.random() * (ubounds[j] - lbounds[j]));
                System.out.println("#####");
                System.out.println(temp);
                System.out.println(temp + Math.max((int) (Math.random() * (ubounds[j] - temp)), 1));
            }
        }*/
        fromJava();
    }

    private static void fromJava() {
        // Get signal
        Signal<Pair<Double,Double>> signal = TestUtils.createSignal(0, 60, 1.0, x -> new Pair<>( x, 3 * x));

        // Build the property (Boolean Semantics)
        TemporalMonitor<Pair<Double,Double>,Boolean> mB = TemporalMonitor.globallyMonitor(
                TemporalMonitor.atomicMonitor(x -> x.getFirst() > x.getSecond()), new BooleanDomain(), new Interval(0, 9));

        // Monitoring
        Signal<Boolean> soutB = mB.monitor(signal);
        double[][] monitorValuesB = soutB.arrayOf((Boolean x) -> x ? 1.0 : -1.0);
        // Print results
        System.out.print("fromJava Boolean\n");
        // System.out.println(signal);
        //printResults(monitorValuesB);
        // Build the property (Quantitative Semantics)
        TemporalMonitor<Pair<Double,Double>,Double> mQ = TemporalMonitor.globallyMonitor(
                TemporalMonitor.atomicMonitor(x -> {if (x.getFirst() % 46 != 0) {return 1.0;} else {return -1.0;}}), new DoubleDomain(), new Interval(10, 0));
        //TemporalMonitor<Pair<Double,Double>,Double> mQ = TemporalMonitor.historicallyMonitor(TemporalMonitor.onceMonitor(
        //                TemporalMonitor.notMonitor(TemporalMonitor.atomicMonitor(x -> x.getFirst() - 2.8E9), new DoubleDomain()), new DoubleDomain(), new Interval(60, 116)),
        //                new DoubleDomain(), new Interval(, 36));
        //TemporalMonitor<Pair<Double,Double>,Double> mQ = TemporalMonitor.sinceMonitor(
        //        TemporalMonitor.atomicMonitor(x -> {if (x.getFirst() % 46 != 0) {return 1.0;} else {return -1.0;}}), new Interval(7, 53), TemporalMonitor.atomicMonitor(x -> {if (x.getFirst() % 46 != 0) {return 1.0;} else {return -1.0;}}), new DoubleDomain());
        Signal<Double> soutQ = mQ.monitor(signal);
        System.out.println(soutQ.valueAt(soutQ.end()));
        double[][] monitorValuesQ = soutQ.arrayOf((Double x) -> (double) x);
        // Print results
        System.out.print("fromJava Quantitative \n");
        printResults(monitorValuesQ);
    }

    public static void printResults(double[][] monitorValues) {
        for (double[] monitorValue : monitorValues) {
            for (double v : monitorValue) {
                System.out.print(v);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
