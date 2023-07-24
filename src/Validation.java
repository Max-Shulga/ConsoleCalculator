import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * this class contains methods for:
 * array parsing.
 * validity checking, outputting  errors to the console.
 * Sting transform  to the required form.
 */
public class Validation implements Constants {

    /**
     * Blank check, input error in console if argument empty;
     *
     * @param args strings array
     */
    public static void emptyArgsCheck(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Mathematical expression can`t be empty");
        }
    }

    /**
     * takes 0 index where the mathematical expression should be placed, removes all spaces,
     * balances +/- according to mathematical rules, checks for validity.
     *
     * @param args strings array
     * @return mathematical expression given in a form that the program will be able to process.
     */
    public static String getFormula(String[] args) {
        String formula = args[0];
        formula = formula.replaceAll(SPACE, EMPTY);
        formula = formula.replaceAll(",", ".");
        formula = plusMinusBalancing(formula);
        if (formula.startsWith("+")) formula = formula.replaceFirst("\\+", "");
        return formula;
    }

    /**
     * Checks if the value of the formula variable is in the variables.
     *
     * @param expression string format mathematical expression;
     * @param variables  HashMap of variables
     */
    public static void parametersNumberCheck(List<String> expression, HashMap<String, String> variables) {

        for (String value : expression) {
            if (Pattern.matches(LETTERS_PATTERN, value))
                if (!variables.containsKey(value)) {
                    throw new IllegalArgumentException("There is no value for argument: " + value);
                }
        }
    }

    /**
     * Get variables from array.
     *
     * @param args strings array
     * @return HashMap<Variable name, variable value>
     */
    public static HashMap<String, String> getVariables(String[] args) {
        HashMap<String, String> parameters = new HashMap<>();
        if (args.length > 1) {

            for (int i = 1; i < args.length; i++) {
                formatCheck(args[i]);

                String[] parameter = args[i].replaceAll(SPACE, EMPTY).split("=");
                String varName = parameter[0];
                String varValue = parameter[1];

                variableNameCheck(varName);
                if (!isDigit(varValue)) {
                    throw new NumberFormatException("invalid variable value");
                }
                parameters.put(varName, varValue);
            }
        }

        return parameters;
    }

    /**
     * checks if there is an "=" sign in the parameter.
     *
     * @param args string containing parameter and value.
     */
    private static void formatCheck(String args) {
        if (!args.contains("=")) {
            throw new IllegalArgumentException("invalid variable format");
        }
    }

    /**
     * Checks the correct format of the variable name.
     *
     * @param parameter String parameter name.
     */
    private static void variableNameCheck(String parameter) {
        if (!Pattern.matches(LETTERS_PATTERN, parameter)) {
            throw new IllegalArgumentException("Invalid variable name: " + "'" + parameter + "'");
        }
    }

    /**
     * removes unnecessary mathematical signs +/-.
     *
     * @param formula string format mathematical expression.
     * @return +/- balancing formula. String.
     */
    public static String plusMinusBalancing(String formula) {
        while (formula.contains("\\+-|-\\+")) {
            formula.replaceAll("\\+-|-\\+", MINUS);
        }
        while (formula.contains("--")) {
            formula = formula.replaceAll("--", PLUS);
        }
        while (formula.contains("++")) {
            formula = formula.replaceAll("\\+\\+", PLUS);
        }

        return formula;
    }

    /**
     * Checks the validity of the formula.
     *
     * @param expression LinkedList format mathematical expression.
     * @param formula    string format mathematical expression.
     */
    public static void checkValidation(LinkedList<String> expression, String formula) {
        int pointerIndex = 0;

        if (checkParenthesis(expression)) {
            throw new IllegalArgumentException("something wrong with parenthesis" + formula);
        }

        String value;
        for (int i = 0; i < expression.size(); i++) {
            value = expression.get(i);

            if (checkIfOperator(i, value, expression)) {
                throw new IllegalArgumentException("something went wrong, check operators: "
                        + exceptionToString(formula, pointer(pointerIndex)));
            }

            if (checkIfFunction(i, value, expression)) {
                throw new IllegalArgumentException("something went wrong, check function: "
                        + exceptionToString(formula, pointer(pointerIndex)));
            }

            if (checkIfNumberOrParameter(i, value, expression)) {
                throw new IllegalArgumentException("something went wrong, check formula: "
                        + exceptionToString(formula, pointer(pointerIndex)));
            }

            if (checkIfUnknownCharacter(value)) {
                throw new IllegalArgumentException("something went wrong, invalid character: "
                        + exceptionToString(formula, pointer(pointerIndex)));
            }
            pointerIndex += value.length();
        }
    }

    /**
     * Checks if the element is a function, number, parameter or bracket
     *
     * @param value element of list.
     * @return false - element supported by the program; true - element don't supported by the program.
     */
    private static boolean checkIfUnknownCharacter(String value) {
        return !((Pattern.matches(LETTERS_PATTERN, value)) ||
                (SUPPORTED_OPERATORS + SUPPORTED_SYMBOLS).contains(value) ||
                SUPPORTED_FUNCTIONS.contains(value) || isDigit(value));
    }

    /**
     * Checks:
     * -if next symbol not operator or close parenthesis.
     * -if previous symbol not operator or open parenthesis.
     *
     * @param index      of value.
     * @param value      that lies in the expression array in the cell № index.
     * @param expression LinkedList format mathematical expression.
     * @return false - formula is valid; true - one of the tests was failed.
     */
    private static boolean checkIfNumberOrParameter(int index, String value, LinkedList<String> expression) {
        if ((Pattern.matches(LETTERS_PATTERN + "|\\d", value))) {
            String next = index != expression.size() - 1 ? expression.get(index + 1) : PLUS;
            String previous = index != 0 ? expression.get(index - 1) : PLUS;
            if (!(SUPPORTED_OPERATORS + CLOSE_PARENTHESIS).contains(next)) return true;
            return !(SUPPORTED_OPERATORS + OPEN_PARENTHESIS).contains(previous);
        }
        return false;
    }

    /**
     * Checks:
     * - if close parenthesis before open parenthesis.
     * - if number of close parenthesis != open parenthesis.
     * - if wrong sequence of parenthesis
     *
     * @param expression LinkedList format mathematical expression.
     * @return false - formula is valid; true - one of the tests was failed.
     */
    private static boolean checkParenthesis(List<String> expression) {
        if (expression.lastIndexOf(OPEN_PARENTHESIS) == expression.size() - 1) return true;
        if (expression.indexOf(OPEN_PARENTHESIS) > expression.indexOf(CLOSE_PARENTHESIS))
            return true;
        int numberOfParenthesis = 0;
        int lastCloseParenthesisIndex = 0;
        for (int i = 0; i < expression.size(); i++) {
            if (expression.get(i).equals(OPEN_PARENTHESIS)) {
                if (expression.get(i + 1).equals(CLOSE_PARENTHESIS)) return true;
                numberOfParenthesis++;
                List<String> subExpression = expression.subList(lastCloseParenthesisIndex, expression.size());
                if (subExpression.contains(CLOSE_PARENTHESIS)) {
                    lastCloseParenthesisIndex = subExpression.indexOf(CLOSE_PARENTHESIS);
                } else return true;
                if (lastCloseParenthesisIndex == -1) return true;
            }
            if (expression.get(i).equals(CLOSE_PARENTHESIS)) {
                numberOfParenthesis--;
            }
        }

        return numberOfParenthesis != 0;
    }

    /**
     * Checks that the next character after the function name is '('
     *
     * @param index      of value.
     * @param value      that lies in the expression array in the cell № index.
     * @param expression LinkedList format mathematical expression.
     * @return false - formula is valid; true - one of the tests was failed.
     */
    private static boolean checkIfFunction(int index, String value, LinkedList<String> expression) {
        if (SUPPORTED_FUNCTIONS.contains(value)) {
            if (index != expression.size() - 1) {
                return !expression.get(index + 1).equals(OPEN_PARENTHESIS);
            } else return true;
        }
        return false;
    }

    /**
     * Checks:
     * - if math. operator(except '-') in the start or in the end of the formula.
     * - if math. operator before close parenthesis or after open parenthesis(except '-').
     * - if 2+ math operator in succession (except  ternary '-').
     *
     * @param index      of value.
     * @param value      that lies in the expression array in the cell № index.
     * @param expression LinkedList format mathematical expression.
     * @return false - formula is valid; true - one of the tests was failed.
     */
    private static boolean checkIfOperator(int index, String value, LinkedList<String> expression) {
        if (SUPPORTED_OPERATORS.contains(value)) {
            if (expression.get(0).equals(PLUS)) expression.remove(0);
            if ((index == 0 && !value.equals(MINUS)) || index == expression.size() - 1) return true;

            String next = expression.get(index + 1);
            String previous = index != 0 ? expression.get(index - 1) : PLUS;

            if ((OPEN_PARENTHESIS.equals(previous) && !value.equals(MINUS))
                    || CLOSE_PARENTHESIS.equals(next))
                return true;
            return SUPPORTED_OPERATORS.contains(expression.get(index + 1))
                    && !expression.get(index + 1).equals(MINUS);

        }
        return false;
    }

    /**
     * @param formula string format mathematical expression
     * @param pointer pointer for not valid place
     * @return String which will be displayed in console
     */
    private static String exceptionToString(String formula, String pointer) {
        return "\n" + formula + "\n" + pointer;
    }

    /**
     * pointer under the invalid location in the line
     *
     * @param index invalid place in the formula
     * @return Sting filled with spaces by the number of indexes, at the end of the line pointer.
     */
    private static String pointer(int index) {
        return SPACE.repeat(Math.max(0, index)) + "^";
    }

    /**
     * Checks if the string can be cast to a number
     *
     * @param str eny line.
     * @return Boolean answer to the question: can a string be cast to a number?
     */
    private static boolean isDigit(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
