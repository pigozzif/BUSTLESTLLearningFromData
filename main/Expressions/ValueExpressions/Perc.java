package Expressions.ValueExpressions;

import it.units.malelab.jgea.core.Node;
import BuildingBlocks.STLFormulaMapper;

import java.util.List;


public class Perc implements ValueExpression<Double> {

    private final Double value;

    public Perc(List<Node<String>> leaves) {
        Digit firstLeaf = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(0)).get();
        Digit secondLeaf = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(1)).get();
        //Digit thirdLeaf = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(2)).get();
        //Digit fourthLeaf = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(3)).get();
        //Digit fifthLeaf = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(3)).get();
        this.value = (firstLeaf.getValue() * Math.pow(10, 1)) + (secondLeaf.getValue() * Math.pow(10, 0));
                //+ (thirdLeaf.getValue() * Math.pow(10, 0)));// + (fourthLeaf.getValue() * 10) + fifthLeaf.getValue();// * Math.pow(10, -4);
        //System.out.println(firstLeaf.getValue() + " " + secondLeaf.getValue() + " " + thirdLeaf.getValue() + " " + fourthLeaf.getValue() + " " + this.value);
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
