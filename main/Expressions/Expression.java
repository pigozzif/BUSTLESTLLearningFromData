package Expressions;


import java.util.HashSet;
import java.util.Set;

public interface Expression {

    String EXPRESSION_STRING = "<expr>";
    Set<String> singletonExpressions = new HashSet<>() {{ add("<digit>"); add("<sign>"); add("<bool>"); add("<var>"); }};

    String toString();

    static String[] getSingletonExpressions() {
        return new String[] {"<digit>", "<sign>", "<bool>", "<var>"};
    }

}
