package Expressions.ValueExpressions;

import java.util.function.Function;


public enum Sign implements ValueExpression<Function<Double, Double>> {

    PLUS("+", num -> num),
    MINUS("-", num -> -num);

    private final String string;
    private final Function<Double, Double> converter;

    Sign(String string, Function<Double, Double> converter) {
        this.string = string;
        this.converter = converter;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public Function<Double, Double> getValue() {
        return this.converter;
    }

}
