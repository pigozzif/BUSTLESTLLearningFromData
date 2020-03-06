package Expressions;


public enum Operator implements Expression {

    NOT(".not"),
    OR(".or");

    private final String string;

    Operator(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

}
