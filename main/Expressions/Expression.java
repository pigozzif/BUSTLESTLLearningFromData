package Expressions;

import java.util.HashSet;
import java.util.Set;


public interface Expression {

    String EXPRESSION_STRING = "<expr_first_level>";
    Set<String> singletonExpressions = new HashSet<String>() {{ add(EXPRESSION_STRING); add("<comp>"); add("<digit>");
                add("<sign>"); add("<bool_var>"); add("<num_var>"); add("<prop>"); add("<logic_first_level>");add("<expr_second_level>");
                add("<future_first_level>"); add("<future_second_level>"); add("<expr_third_level_onwards>"); add("<logic_second_level>");
                add("<logic_third_level_onwards>"); }};

    String toString();

}
