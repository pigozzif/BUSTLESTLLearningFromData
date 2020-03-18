package Expressions;

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
        for (String varName : new String[]{"isChangingLane_bool_", "isApproachingOnramp_bool_", "isByGuardrail_bool_"}) {
            res.add(new BooleanVariable(varName));
        }
        for (String varName: new String[]{"V_vel", "Lane_ID", "NE_dist", "N_dist", "NW_dist", "W_dist", "SW_dist",
                "S_dist", "SE_dist", "NE_vel", "N_vel", "NW_vel", "W_vel", "SW_vel", "S_vel", "SE_vel"}) {
            res.add(new NumericalVariable(varName));
        }
        return res;
    }

}
