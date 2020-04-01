package Expressions.ValueExpressions;

import it.units.malelab.jgea.core.Node;
import BuildingBlocks.STLFormulaMapper;

import java.util.List;

// TODO: Perc should not be allowed to start at 1.0. There is also the
//  the problem that a single 0 knocks out the whole number
public class Perc implements ValueExpression<Double> {

    private final Double value;

    public Perc(List<Node<String>> leaves) {
        Digit firstLeaf = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(0)).get();
        Digit secondLeaf = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(1)).get();
        Digit thirdLeaf = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(2)).get();
        Digit fourthLeaf = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(3)).get();
        this.value = firstLeaf.getValue() * secondLeaf.getValue() * thirdLeaf.getValue() * fourthLeaf.getValue() *
                Math.pow(10, -4);
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
