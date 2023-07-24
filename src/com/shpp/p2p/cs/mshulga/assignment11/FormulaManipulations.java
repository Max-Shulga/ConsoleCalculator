package com.shpp.p2p.cs.mshulga.assignment11;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Contain methods for pre-calculated operations.
 */
public class FormulaManipulations implements Constants {

    /**
     * find numbers with the '-' sign (if it is not a matrix operator), and change their value
     *
     * @param expression formula divided by mathematical operators.
     */
    static public void transformNegativeNumbers(List<String> expression) {
        //for 0 index ternary minus.
        if (expression.get(0).equals(MINUS)) {
            String newValue = MINUS + expression.get(1);
            newValue = Validation.plusMinusBalancing(newValue);
            expression.set(1, newValue);
            expression.remove(0);
        }

        for (int i = 1; i < expression.size(); i++) {
            if (expression.get(i).equals(MINUS)) {

                if (SUPPORTED_OPERATORS.contains(expression.get(i - 1))) {
                    String newValue = MINUS + expression.get(i + 1);
                    newValue = Validation.plusMinusBalancing(newValue);
                    expression.set(i + 1, newValue);
                    expression.remove(i);
                }
            }
        }
    }

    /**
     * replaces all variables in the formula with values from the HashMap.
     *
     * @param expression     LinkedList format mathematical expression
     * @param variableValues HashMap<Variable name, variable value>
     */
    public static void replaceParameters(LinkedList<String> expression, HashMap<String, String> variableValues) {
        try {
            for (int i = 0; i < expression.size(); i++) {
                String currentValue = expression.get(i);
                if (Pattern.matches(LETTERS_PATTERN, currentValue)) {
                    String value = variableValues.get(currentValue);
                    expression.set(i, value);
                    //for ternary minus.
                } else if (Pattern.matches("(-)" + LETTERS_PATTERN, currentValue)) {
                    currentValue = currentValue.replace(MINUS, EMPTY);
                    String value = MINUS + variableValues.get(currentValue);
                    expression.set(i, value);
                }
            }
        }catch (NumberFormatException e){
            System.out.println("Something went wrong, check parameters");
        }
    }

}
