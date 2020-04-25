package Expressions;

import java.util.HashSet;
import java.util.Set;


public interface Expression {

    String EXPRESSION_STRING = "<expr>";
    Set<String> singletonExpressions = new HashSet<>() {{ add(EXPRESSION_STRING); add("<comp>"); add("<digit>");
                add("<sign>"); add("<bool>"); add("<var>"); add("<prop>"); add("<logic>");add("<expr2>"); add("<future1>");
                add("<future2>"); add("<special>"); add("<logic2>"); }};

    String toString();

}
