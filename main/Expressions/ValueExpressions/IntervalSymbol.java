package Expressions.ValueExpressions;

import BuildingBlocks.STLFormulaMapper;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;


public class IntervalSymbol implements ValueExpression<Double> {

    private Double value = 0.0;

    public IntervalSymbol(List<Tree<String>> leaves) {
        int k = leaves.size() - 1;
        for (Tree<String> leaf : leaves) {
            Digit temp = (Digit) STLFormulaMapper.fromStringToValueExpression(leaf).get();
            this.value += temp.getValue() * Math.pow(10, k--);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public Double getValue() {
        return this.value;
    }

}
