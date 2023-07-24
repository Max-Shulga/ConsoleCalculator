import java.util.ArrayList;
import java.util.List;


public interface Constants {

    /**
     * operators that the program supports.
     */
    String SUPPORTED_OPERATORS = "-\\+\\*/\\^%";

    /**
     * symbols that the program supports.
     */
    String SUPPORTED_SYMBOLS = "()";

    /**
     * functions that the program supports.
     */
    ArrayList<String> SUPPORTED_FUNCTIONS = new ArrayList<>(List.of(
            "sin","cos","tan", "atan", "log10", "log2", "sqrt","-sin","-cos","-tan", "-atan", "-log10", "-log2", "-sqrt"));

    //functions names:
    String SIN = "sin";
    String COS = "cos";
    String TAN = "tan";
    String ATAN = "atan";
    String LOG_10 = "log10";
    String LOG_2 = "log2";
    String SQRT = "sqrt";

    /**
     * letters pattern
     */
    String LETTERS_PATTERN = "[a-zA-Z]";

    // sting designations of operators:
    String PLUS = "+";
    String MINUS = "-";
    String MULTIPLICATION = "*";
    String DIVISION = "/";
    String EXPONENTIATION = "^";
    String MODULO = "%";

    //else symbols
    String SPACE = " ";
    String EMPTY = "";
    String OPEN_PARENTHESIS = "(";
    String CLOSE_PARENTHESIS = ")";
}
