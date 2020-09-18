package Expressions;

import BuildingBlocks.ProblemClass;
import Expressions.MonitorExpressions.BooleanVariable;
import Expressions.MonitorExpressions.MonitorExpression;
import Expressions.MonitorExpressions.NumericalVariable;
import Expressions.MonitorExpressions.Operator;
import Expressions.ValueExpressions.CompareSign;
import Expressions.ValueExpressions.Digit;
import Expressions.ValueExpressions.Sign;
import Expressions.ValueExpressions.ValueExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ExpressionsFactory {

    public static List<ValueExpression<?>> createValueExpressions() {
        List<ValueExpression<?>> res = new ArrayList<>(Arrays.asList(Sign.values()));
        res.addAll(Arrays.asList(CompareSign.values()));
        for (int i = 0; i < 10; ++i) {
            res.add(new Digit(Integer.toString(i)));
        }
        return res;
    }

    public static List<MonitorExpression> createMonitorExpressions() {
        List<MonitorExpression> res = new ArrayList<>(Arrays.asList(Operator.values()));
        for (String varName : ProblemClass.retrieveBooleanNames()) {
            res.add(new BooleanVariable(varName));
        }
        for (String varName: ProblemClass.retrieveNumericalNames()) {
            res.add(new NumericalVariable(varName));
        }
        return res;
    }

}
