package Expressions;


public class Digit implements Expression<Integer> {

    private final int number;

    public Digit(String string) {
        this.number = Integer.parseInt(string);
    }

    @Override
    public String toString() {
        return Integer.toString(this.number);
    }

    @Override
    public Integer getValue() {
        return this.number;
    }

}
