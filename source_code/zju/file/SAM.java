package edu.zju.file;

import java.io.File;

/**
 *
 ** @author Zhongxu Zhu
 */
public class SAM extends CommonInputFile {

        public SAM(String path) {
                super(path.trim());
        }

        @Override
        public void check() {
                super.check();
                if(!this.getFileName().endsWith(".sam")){
                      edu.zju.common.CExecutor.stopProgram(this.getFileName()+" should end with .sam");
                }
        }
        /**
         * Because SAM format file alway big, it takes time to calculate MD5
         * @return Actually, return the file size rather that MD5
         */
        @Override
        public String getFileMD5(){
                return String.valueOf(new File(this.getFilePath()).length());
        }
}
