package Expressions.MonitorExpressions;

import BuildingBlocks.STLFormulaMapper;
import BuildingBlocks.TrajectoryRecord;
import BuildingBlocks.TreeNode;
import Expressions.ValueExpressions.CompareSign;
import Expressions.ValueExpressions.Digit;
import Expressions.ValueExpressions.Sign;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;


public class NumericalVariable implements MonitorExpression {

    private final String string;

    public NumericalVariable(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TreeNode createMonitor(List<Node<String>> siblings, TreeNode parent) {
        CompareSign firstSibling = (CompareSign) STLFormulaMapper.fromStringToValueExpression(siblings.get(0).getChildren().get(0)).get();
        double number = parseNumber(siblings.get(1).getChildren());
        TreeNode newNode = new TreeNode(parent);
        newNode.setSymbol((this.string + " " + firstSibling.toString() + " " + number).toCharArray());
        newNode.setOperator(x -> TemporalMonitor.atomicMonitor(y -> firstSibling.getValue().apply(y.getDouble(this.string),
                number)));
        return newNode;
    }
    // TODO: correct error
    private double parseNumber(List<Node<String>> leaves) {
        Digit secondSibling = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(0)).get();
        Digit thirdSibling = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(1)).get();
        Sign fourthSibling = (Sign) STLFormulaMapper.fromStringToValueExpression(leaves.get(2)).get();
        Digit fifthSibling = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(3)).get();
        return ((secondSibling.getValue() * 10) + thirdSibling.getValue()) * (Math.pow(10, fourthSibling.getValue().apply(
                Double.valueOf(fifthSibling.getValue()))));
    }

}
