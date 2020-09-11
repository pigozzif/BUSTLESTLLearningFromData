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
        for (String varName : retrieveBooleanNames()) {
            res.add(new BooleanVariable(varName));
        }
        for (String varName: retrieveNumericalNames()) {
            res.add(new NumericalVariable(varName));
        }
        return res;
    }

    public static String[] retrieveBooleanNames() {
        return new String[]{"isChangingLane", "isApproachingOnramp", "isByGuardrail"};
    }

    public static String[] retrieveNumericalNames() {
        return new String[]{"V_vel", "NE_dist", "N_dist", "NW_dist", "W_dist", "SW_dist",
                "S_dist", "SE_dist", "E_dist",/* "angle", "torque", "speed",*/
                "Vehicle_ID", "Global_Time"};
    }

}
