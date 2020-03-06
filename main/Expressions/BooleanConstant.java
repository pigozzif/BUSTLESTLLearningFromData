package Expressions;


public enum BooleanConstant implements Expression {

    TRUE(true),
    FALSE(false);

    private final boolean value;

    BooleanConstant(boolean value) {
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
