/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.common;

/**
 *
 ** @author Zhongxu Zhu
 */
public class NumeralHandler {

        public static double setScale(int scale, double number) {
                String numString = String.valueOf(number);
                if (numString.contains(".") && numString.contains("E")) {
                        String a = numString.substring(0, numString.indexOf("."));
                        String b = numString.substring(numString.indexOf("E") + 1, numString.length());
                        String c = numString.substring(numString.indexOf(".") + 1, numString.indexOf("E"));
                        if (c.length() < scale) {
                                return number;
                        } else {
                                return Double.parseDouble(a + "." + c.substring(0, scale) + "E" + b);
                        }
                }
                if (numString.contains(".") && !numString.contains("E")) {
                        String a = numString.substring(0, numString.indexOf("."));
                        String b = numString.substring(numString.indexOf(".") + 1, numString.length());
                        if (Integer.parseInt(a) != 0) {
                                if (a.length() == 1) {
                                        if (b.length() < scale) {
                                                return number;
                                        } else {
                                                return Double.parseDouble(a + "." + b.substring(0, scale));
                                        }
                                } else {
                                        if ((a + b).length() < scale + 1) {
                                                return Double.parseDouble(a.substring(0, 1) + "." + a.substring(1, a.length()) + b + "E" + (a.length() - 1));
                                        } else {
                                                return Double.parseDouble(a.substring(0, 1) + "." + a.substring(1, a.length()) + b.substring(0, scale - a.length() + 1) + "E" + (a.length() - 1));
                                        }
                                }
                        } else {
                                int marker = 0;
                                for (int i = 0; i < b.toCharArray().length; i++) {
                                        if (b.toCharArray()[i] != '0') {
                                                marker = i;
                                                break;
                                        }
                                }
                                if ((b.length() - (marker + 1)) >= scale) {
                                        return Double.parseDouble(b.substring(marker, marker + 1) + "." + b.substring(marker + 1, marker + scale) + "E-" + (marker + 1));
                                } else {
                                        return Double.parseDouble(b.substring(marker, marker + 1) + "." + b.substring(marker + 1, b.length()) + "E-" + (marker + 1));
                                }
                        }
                }
                return number;
        }

        public static double setScale(double number) {
                return NumeralHandler.setScale(3, number);
        }
}
