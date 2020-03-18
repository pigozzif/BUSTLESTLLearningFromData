package Expressions.ValueExpressions;


import Expressions.Expression;

public interface ValueExpression<T> extends Expression {

    String EXPRESSION_STRING = "expr";

    //String toString();

    T getValue();

}
