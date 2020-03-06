package Expressions;


public class Variable implements Expression {

    private final String string;

    public Variable(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

}
