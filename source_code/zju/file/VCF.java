package edu.zju.file;

import java.io.BufferedReader;

/**
 *
 ** @author Zhongxu Zhu
 */
public class VCF extends CommonInputFile{

        public VCF(String path) {
                super(path);
        }
        public VCF(String path,BufferedReader br){
                super(path,br);
        }
        @Override
        public void check(){
              super.check();
              if(!this.getFilePath().equals(Config.getItem("FAKE_PAHT"))&&!this.getFileName().endsWith(".vcf")){
                      edu.zju.common.CExecutor.stopProgram(this.getFileName()+" should end with .vcf");
              }
        }
}
