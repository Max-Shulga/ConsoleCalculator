package com.shpp.p2p.cs.mshulga.assignment11;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Class for formula parsing.
 */
public class Parser implements Constants{

    /**
     * Splits a string by mathematics operators with operator preservation
     * @param formula string format mathematical expression;
     * @return parsed LinkedList.
     */
    public static LinkedList<String> parseFormula(String formula) {
        return new LinkedList<>(Arrays.asList(
                formula.split("(?=[" + (SUPPORTED_OPERATORS + SUPPORTED_SYMBOLS) + "])|(?<=[" + (SUPPORTED_OPERATORS + SUPPORTED_SYMBOLS) + "])")));
    }

}
