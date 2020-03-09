package Expressions;


public enum BooleanConstant implements Expression<Boolean> {

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

    @Override
    public Boolean getValue() {
        return this.value;
    }

}
