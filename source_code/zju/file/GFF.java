/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.file;

/**
 *
 ** @author Zhongxu Zhu
 */
public class GFF extends CommonInputFile{

        public GFF(String path) {
                super(path);
        }
        @Override
        protected void check() {
                super.check();
                if(!this.getFileName().toLowerCase().endsWith(".gff3")&&!this.getFileName().toLowerCase().endsWith("gff")){
                      edu.zju.common.CExecutor.stopProgram(this.getFileName()+" should end with .gff3. Please make sure GFF file version (version3 required).");
                }
                String line;
                boolean isHasIDField=false;
                int checkLines=1000;
                while((line=this.readLine())!=null&&checkLines!=0){
                        if(line.trim().length()==0)continue;
                        if(line.startsWith("#")){
                                //common.CExecutor.println(line);
                                continue;
                        }
                        if(line.split("\t")[8].contains("ID")){
                                isHasIDField=true;
                                checkLines=checkLines-1;
                        }
                }
                this.closeInput();
                if(!isHasIDField){
                        edu.zju.common.CExecutor.stopProgram("GFF file does not contain ID information");
                }   
        }        
}
