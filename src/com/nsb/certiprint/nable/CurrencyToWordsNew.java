package com.nsb.certiprint.nable;

import java.text.DecimalFormat;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class CurrencyToWordsNew {
    
    // Array for numbers less than 20
    private static final String[] belowTwenty = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", 
                                                "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", 
                                                "Eighteen", "Nineteen"};

    // Array for tens multiples
    private static final String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

    // Convert numbers below 1000 to words
    private static String convertLessThanThousand(int number) {
        if (number < 20) {
            return belowTwenty[number];
        } else if (number < 100) {
            return tens[number / 10] + (number % 10 != 0 ? " " + belowTwenty[number % 10] : "");
        } else {
            return belowTwenty[number / 100] + " Hundred" + (number % 100 != 0 ? " " + convertLessThanThousand(number % 100) : "");
        }
    }

    // Convert the entire amount to words
    public static String convertToWords(double amount) {
        if (amount == 0) {
            return "Zero Dollars";
        }

        // Split the number into the dollar and cents parts
        long dollars = (long) amount;
        int cents = (int) Math.round((amount - dollars) * 100);

        String dollarPart = (dollars == 0) ? "" : convertLessThanThousand((int)(dollars % 1000));
        String centsPart = (cents == 0) ? "" : convertLessThanThousand(cents) + " Cents";

        // Handle larger values (thousands, millions, billions)
        String word = "";
        int thousandCounter = 0;
        String[] thousandNames = {"", " Thousand", " Million", " Billion"};

        while (dollars > 0) {
            int part = (int) (dollars % 1000);
            if (part != 0) {
                String partWord = convertLessThanThousand(part) + thousandNames[thousandCounter];
                word = partWord + (word.isEmpty() ? "" : " " + word);
            }
            dollars /= 1000;
            thousandCounter++;
        }

        // Combine the dollar and cents parts
        return (word.isEmpty() ? "Zero Rupees" : word) + (cents == 0 ? "" : " AND " + centsPart);
    }

}
