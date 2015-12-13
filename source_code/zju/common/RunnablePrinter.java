/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.common;

/**
 *
 * @author sss
 */
public class RunnablePrinter implements Runnable{
        private int sleepSecond=60;// second unit
        private String content2Print=".";
        private String welcomeWords="";

        
        @Override
        public void run() {
                CExecutor.print(welcomeWords);
                while(true){
                        try {
                                Thread.sleep(sleepSecond*1000);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                        CExecutor.print(content2Print);
                }
        }
        
        public void setSleepSecond(int seconds){
                this.sleepSecond=seconds;
        }
        public void setContent2Print(String content){
                this.content2Print=content;
        }
        public void setWelcomeWords(String welcomeWords) {
                this.welcomeWords = welcomeWords;
        }

        
}
