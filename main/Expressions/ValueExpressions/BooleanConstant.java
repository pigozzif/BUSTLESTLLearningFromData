package Expressions.ValueExpressions;

import it.units.malelab.jgea.representation.tree.Tree;


public class BooleanConstant implements ValueExpression<Boolean> {

    private final String constant;

    public BooleanConstant(Tree<String> string) {
        this.constant = string.content();
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
