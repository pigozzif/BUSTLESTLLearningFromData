package Expressions;


public interface ValueExpression<T> {

    String EXPRESSION_STRING = "expr";

    String toString();

    T getValue();

}
