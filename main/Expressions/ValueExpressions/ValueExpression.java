package Expressions.ValueExpressions;


import Expressions.Expression;

public interface ValueExpression<T> extends Expression {

    T getValue();

}
