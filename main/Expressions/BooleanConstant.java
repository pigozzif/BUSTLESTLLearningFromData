package Expressions;


public class BooleanConstant implements Expression {

    private final boolean value;

    public BooleanConstant(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    public boolean getValue() {
        return this.value;
    }

}
