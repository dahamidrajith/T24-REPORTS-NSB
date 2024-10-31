package com.nsb.certiprint.nable;

import java.text.DecimalFormat;

/**
 * TODO: Document me!
 *
 * @author dahami
 *
 */
public class CurrencyToWords {
    
    private static final String[] units = {"", "Thousand", "Million", "Billion", "Trillion"};

    private static final String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};

    private static final String[] teens = {"", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};

    private static final String[] tens = {"", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

    public static String convertCurrencyToWords(double amount) {
        if (amount == 0) {
            return "Zero";
        }

        String result = "";
        int i = 0;

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String formattedAmount = decimalFormat.format(amount);

        String[] parts = formattedAmount.split("\\.");

        long integerPart = Long.parseLong(parts[0]);
        int decimalPart = Integer.parseInt(parts[1]);

        // Convert integer part to words
        while (integerPart > 0) {
            if (integerPart % 1000 != 0) {
                result = convertLessThanOneThousand((int) (integerPart % 1000)) + " " + units[i] + " " + result;
            }
            integerPart /= 1000;
            i++;
        }

        // Convert decimal part to words
        if (decimalPart > 0) {
            if(decimalPart==10){
                result += " and " + convertLessThanOneThousand(decimalPart) + "TEN Cents";
            }else{
                result += " and " + convertLessThanOneThousand(decimalPart) + " Cents";
            }
        }

        return result.trim();
    }

    private static String convertLessThanOneThousand(int number) {
        if (number == 0) {
            return "";
        } else if (number < 10) {
            return ones[number];
        } else if (number < 20) {
            return teens[number - 10];
        } else if (number < 100) {
            return tens[number / 10] + " " + convertLessThanOneThousand(number % 10);
        } else {
            return ones[number / 100] + " Hundred " + convertLessThanOneThousand(number % 100);
        }
    }

    public static void main(String[] args) {
        double amount = 1234567.89; // Replace this with your currency value
        System.out.println(convertCurrencyToWords(amount));
    }
}


