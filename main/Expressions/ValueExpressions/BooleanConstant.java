package Expressions.ValueExpressions;

import it.units.malelab.jgea.core.Node;


public class BooleanConstant implements ValueExpression<Boolean> {

    private final String constant;

    public BooleanConstant(Node<String> string) {
        this.constant = string.getContent();
        //System.out.println(this.constant + " " + Boolean.parseBoolean(this.constant));
    }

    @Override
    public String toString() {
        return this.constant;
    }

    @Override
    public Boolean getValue() {
        return Boolean.parseBoolean(this.constant);
    }

}
