package edu.zju.file;

/**
 *
 ** @author Zhongxu Zhu
 */
public class VariantControl extends CommonInputFile{
        public VariantControl(String path) {
                super(path.trim());
        }

        @Override
        public void check() {
                super.check();
                String line;
                int i=0;
                while((line=this.readLine())!=null){
                        if(line.startsWith("#")) continue;
                        if(line.trim().length()==0) break;
                        i++;
                        if(line.split("\t").length<5){
                                edu.zju.common.CExecutor.stopProgram("\nVariants control file should provide ref and alt base information, for example\n"
                                        + "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\n"
                                        + "Chr1\t189237\t.\tA\tC\n"
                                        + "Chr1\t189237\t.\tA\tC\t.\t.\tGT\t1/1");
                        }
                        if(i==1000) break;
                }
                this.closeInput();
        }        
}
