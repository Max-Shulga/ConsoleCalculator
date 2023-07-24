import java.util.*;


import static java.util.List.of;


/**
 * Receives a mathematical expression as input, and the values of the variables.
 * Finds the value of the expression and outputs it to the console
 */

public class ConsoleCalculator implements Constants {

    /**
     * a repository for formulas where only the variables are changed.
     */
    private static final HashMap<String, LinkedList<String>> formulaCache = new HashMap<>();

    /**
     * gets the mathematical expression form args, verify its validity;
     * gets HashMap<variable name, variable value>.
     * performs the calculation and display the result on the console
     *
     * @param args first index a mathematical expression, others (optional) variable name and value;
     */
    public static void main(String[] args) {
        try {
            Validation.emptyArgsCheck(args);
            String formula = Validation.getFormula(args);
            HashMap<String, String> variables = Validation.getVariables(args);
            System.out.println(calculate(formula, variables));
        } catch (Exception e) {
            System.out.println("ERROR");
            System.out.println(e.getMessage());
        }
    }

    /**
     * If the parsed formula is in the cache, takes it,
     * if not - parses the formula and puts it in the cache.
     * After that replace parameters and performs calculations according to mathematical rules
     *
     * @param formula        string format mathematical expression
     * @param variableValues HashMap<Variable name, variable value>
     * @return the result which is obtained from the formula. double
     */
    private static double calculate(String formula, HashMap<String, String> variableValues) {
        LinkedList<String> expression;
        //check parsed formula in cache. If not parse it and put in.
        if (!formulaCache.containsKey(formula)) {
            expression = Parser.parseFormula(formula);
            Validation.checkValidation(expression, formula);
            formulaCache.put(formula, expression);
        }
        expression = formulaCache.get(formula);
        Validation.parametersNumberCheck(expression, variableValues);
        FormulaManipulations.replaceParameters(expression, variableValues);

        String result = calculateFormula(expression);
        return Double.parseDouble(result);
    }

    /**
     * converting a string into a LinkedList and step-by-step calculation by priority
     *
     * @param expression LinkedList format mathematical expression.
     * @return maximum simplified formula, if the formula contains variables.
     * String format Calculation result if there are no variables in the formula
     */
    private static String calculateFormula(List<String> expression) {
        FormulaManipulations.transformNegativeNumbers(expression);
        calculateParenthesis(expression);
        while (expression.contains(EXPONENTIATION)) performCalculation(EXPONENTIATION, expression);
        operationsWithOperator(of(DIVISION, MULTIPLICATION, MODULO), expression);
        operationsWithOperator(of(PLUS, MINUS), expression);
        return expression.get(0);
    }

    /**
     * Takes the sub-expression between the last open bracket and the closest closed bracket and
     * calculates this expression, then calls the method (calculateFunction) the resulting expression
     * is recursively fed into calculateFormula()
     *
     * @param expression LinkedList format mathematical expression.
     */
    private static void calculateParenthesis(List<String> expression) {
        if (expression.contains(OPEN_PARENTHESIS)) {
            int openParenthesis = expression.lastIndexOf(OPEN_PARENTHESIS);
            int closeParenthesis = expression.subList(openParenthesis, expression.size()).indexOf(CLOSE_PARENTHESIS) + openParenthesis;

            expression.remove(closeParenthesis);
            expression.remove(openParenthesis);

            calculateFormula(expression.subList(openParenthesis, closeParenthesis - 1));
            calculateFunction(expression, openParenthesis);
            calculateFormula(expression);
        }
    }

    /**
     * If the expression was preceded by a function name inside the brackets,call
     * functionOperation() method with arguments: 0 - function name; 1-value;
     *
     * @param expression LinkedList format.
     * @param index      of the calculation result inside the brackets
     */
    private static void calculateFunction(List<String> expression, int index) {
        if (index != 0) {
            if (SUPPORTED_FUNCTIONS.contains(expression.get(index - 1))) {
                functionOperation(expression.subList(index - 1, index + 1));
            }
        }
    }


    /**
     * Goes through LinkedList when it encounters one of the operators it checks which one it is
     * and calls the performCalculation method with this operator.
     *
     * @param operators  the mathematical signs corresponding to the operation to be performed.
     * @param expression LinkedList format mathematical expression.
     */
    private static void operationsWithOperator(List<String> operators, List<String> expression) {
        for (int i = 0; i < expression.size(); i++) {
            if (operators.contains(expression.get(i))) {

                for (String operator : operators) {
                    if (expression.get(i).equals(operator)) {
                        performCalculation(operator, expression);
                        i--;
                    }
                }
            }
        }
    }

    /**
     * finds the last occurrence of the operator and performs a mathematical action between the
     * numbers to the left and right of the operator. After that replace first value for new value,
     * and removes operator and second digit from LinkedList
     *
     * @param operator the mathematical sign corresponding to the operation to be performed.
     * @param list     LinkedList containing numbers and the operator between them
     */
    private static void performCalculation(String operator, List<String> list) {
        int index = operator.equals(EXPONENTIATION) ? list.lastIndexOf(operator) : list.indexOf(operator);
        double digitOne = Double.parseDouble(list.get(index - 1));
        double digitTwo = Double.parseDouble(list.get(index + 1));

        double result = switch (operator) {

            case EXPONENTIATION -> Math.pow(digitOne, digitTwo);
            case MULTIPLICATION -> digitOne * digitTwo;
            case DIVISION -> digitOne / digitTwo;
            case PLUS -> digitOne + digitTwo;
            case MINUS -> digitOne - digitTwo;
            case MODULO -> digitOne % digitTwo;
            default -> 0;
        };
        list.remove(index + 1);
        list.remove(index);
        list.set(index - 1, String.valueOf(result));
    }

    /**
     * The function name(0 - index in list) is fed into switch,
     * if sign '-' before function name in the end of method  change function result
     * to the reverse value
     *
     * @param list LinkedList containing function name and function value;
     */
    private static void functionOperation(List<String> list) {
        double number = Double.parseDouble(list.get(1));
        boolean negative = false;
        String function;

        //if "-sin" "-cos"... etc
        if (list.get(0).contains(MINUS)) {
            function = list.get(0).replace("-", "");
            negative = true;
        } else {
            function = list.get(0);
        }

        double value = switch (function) {
            case SIN -> Math.sin(number);
            case COS -> Math.cos(number);
            case TAN -> Math.tan(number);
            case ATAN -> Math.atan(number);
            case LOG_10 -> Math.log10(number);
            case LOG_2 -> Math.log(number) / Math.log(2);
            case SQRT -> Math.sqrt(number);
            default -> 0;
        };

        String result = negative ? String.valueOf((value * -1)) : String.valueOf(value);
        list.set(0, result);
        list.remove(1);
    }

}