package Expressions.ValueExpressions;

import java.util.function.BiFunction;


public enum CompareSign implements ValueExpression<BiFunction<Double, Double, Double>> {

    SMALLER("<", (Double a, Double b) -> tightnessMetric(b - a)),
    GREATER(">", (Double a, Double b) -> tightnessMetric(a - b)),
    SMALLER_OR_EQUAL("<=", (Double a, Double b) -> tightnessMetric(b - a)),
    GREATER_OR_EQUAL(">=", (Double a, Double b) -> tightnessMetric(a - b)),
    EQUAL("==", (Double a, Double b) -> tightnessMetric(- Math.abs(a - b)));

    private final String sign;
    private final BiFunction<Double, Double, Double> function;

    CompareSign(String type, BiFunction<Double, Double, Double> func) {
        this.sign = type;
        this.function = func;
    }

    @Override
    public String toString() {
        return sign;
    }

    @Override
    public BiFunction<Double, Double, Double> getValue() {
        return this.function;
    }

    private static double tightnessMetric(double r) {
        return (1.0 / (r + Math.exp(-r)))  - Math.exp(-r);
    }

}
