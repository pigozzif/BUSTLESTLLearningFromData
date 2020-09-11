package Expressions.MonitorExpressions;

import BuildingBlocks.STLFormulaMapper;
import BuildingBlocks.TreeNode;
import Expressions.ValueExpressions.CompareSign;
import Expressions.ValueExpressions.Digit;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.text.DecimalFormat;
import java.util.List;


public class NumericalVariable implements MonitorExpression {

    private final String string;
    private static final DecimalFormat df = new DecimalFormat("#.###");

    public NumericalVariable(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TreeNode createMonitor(List<Node<String>> siblings, String content) {
        CompareSign firstSibling = (CompareSign) STLFormulaMapper.fromStringToValueExpression(siblings.get(0).getChildren().get(0)).get();
        double number = parseNumber(siblings.get(1).getChildren());
        TreeNode newNode = new TreeNode(content);
        newNode.setSymbol(this.string + " " + firstSibling.toString() + " " + number);
        newNode.setOperator(x -> TemporalMonitor.atomicMonitor(y -> firstSibling.getValue().apply(y.getDouble(this.string),
                number)));
        return newNode;
    }

    private double parseNumber(List<Node<String>> leaves) {
        //df.setRoundingMode(RoundingMode.FLOOR);
        Digit secondSibling = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(0)).get();
        Digit thirdSibling = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(1)).get();
        //Sign fourthSibling = (Sign) STLFormulaMapper.fromStringToValueExpression(leaves.get(2)).get();
        Digit fifthSibling = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(2)).get();
        return (double) Math.round(((secondSibling.getValue() * Math.pow(10, -1)) + (thirdSibling.getValue() * Math.pow(10, -2)) + (fifthSibling.getValue() * Math.pow(10, -3))) * 1000d) / 1000d;/*thirdSibling.getValue()) * (Math.pow(10, fourthSibling.getValue().apply(
                Double.valueOf(fifthSibling.getValue()))));*/
    }

}
