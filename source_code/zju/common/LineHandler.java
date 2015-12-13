package edu.zju.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 ** @author Zhongxu Zhu
 */
public class LineHandler {

        public String linesplit[];

        public void splitByTab(String line) {
                linesplit = line.split("\t");
        }

        public void splitByComma(String line) {
                linesplit = line.split(",");
        }

        public void splitByDoublePoint(String line) {
                linesplit = line.split("\\.\\.");
        }

        public void splitByColon(String line) {
                linesplit = line.split(":");
        }

        public void splitByVerticalLine(String line) {
                linesplit = line.split("\\|");
        }

        public void splitBySemicolon(String line) {
                linesplit = line.split(";");
        }

        /**
         *
         * @param line waiting for match
         * @param regex match pattern
         * @return
         */
        public String regexMatch(String line, String regex) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);
                try {
                        if (!matcher.find()) {
                                return null;
                        }
                        return matcher.group(1);
                } catch (IllegalStateException ex) {
                        //common.CExecutor.print("no match find ---"+line+"\""+regex+ "\"");
                        //Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                }

        }
}
