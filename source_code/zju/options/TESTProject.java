package edu.zju.options;

import edu.zju.common.CExecutor;
import edu.zju.common.ZipUtil;
import edu.zju.file.CommonOutputFile;
import edu.zju.file.Config;
import edu.zju.file.FileFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 ** @author Zhongxu Zhu
 */
public class TESTProject {
        
     public void initiateTestProject(String projectString) throws FileNotFoundException, IOException{
             if(projectString==null||projectString.trim().isEmpty()){
                     edu.zju.common.CExecutor.stopProgram("Please set Test project's path");
             }
             if(new File(projectString+CExecutor.getFileSeparator()+"Test").exists()){
                     CExecutor.stopProgram("Project Test already exist");
             }
             InputStream in=this.getClass().getResourceAsStream(Config.getItem("Test.Project"));
             String zipPath=edu.zju.common.CExecutor.getTerminalDirectoy()+edu.zju.common.CExecutor.getFileSeparator()+"Test.zip";
             OutputStream out = new FileOutputStream(zipPath);
             byte[] buffer =new byte[1024];
             int len;
             while((len=in.read(buffer))>0){
                     out.write(buffer, 0, len);
             }
             in.close();
             out.flush();
             out.close();
             new ZipUtil().unzipFiles(new File(zipPath), projectString);
             new File(zipPath).delete();
             projectString=projectString+edu.zju.common.CExecutor.getFileSeparator()+"Test";
             new Init(projectString);
             this.generateIniFile();
             this.generateCallerScriptFile();
             
     }
     private void generateIniFile(){
             CommonOutputFile iniOutFile=FileFactory.getOutputFile(Init.getParameterFilePath());
             String s=edu.zju.common.CExecutor.getFileSeparator();
             iniOutFile.write("[GLOBAL]"
                     + "\nPROJECT        : Test"
                     + "\nREF_GENOME_ANNOTATION.GFF: "+Init.getRefDirectory()+s+"Test.gff3"
                     + "\nSNPEFF_GENOME_VERSION    : Test"
                     + "\nSNPEFF :                     #/path/to/snpefffolder"
                     + "\nVAR_CALL_SCRIPT:"+Init.getScriptDirectory()+s+"samtools_Q13q30_script"
                     + "\nEFF_REGION   : CDS|SpliceSite=2"
                     + "\nVAR_FILTERS  : BA"
                     + "\nSCORE_MATRIX : DEFAULT"
                     + "\nCANDIDATE_CRITERIA :"
                     + "\nNUM_SIM_SNPS : 800 "
                     + "\n\n[SAMPLE_LIST]\nsample1\nsample2\nsample3"
                     + "\n\n[SAMPLE]\nSAMPLE_NAME :sample1\nSAMPLE.VCF :"+Init.getDataDirectory()+s+"sample1"+s+"sample1.vcf\nREADS_ALIGNMENT.SAM :"+Init.getDataDirectory()+s+"sample1"+s+"sample1.sam"
                     + "\n\n[SAMPLE]\nSAMPLE_NAME :sample2\nSAMPLE.VCF :"+Init.getDataDirectory()+s+"sample2"+s+"sample2.vcf\nREADS_ALIGNMENT.SAM :"+Init.getDataDirectory()+s+"sample2"+s+"sample2.sam"
                     + "\n\n[SAMPLE]\nSAMPLE_NAME :sample3\nSAMPLE.VCF :"+Init.getDataDirectory()+s+"sample3"+s+"sample3.vcf\nREADS_ALIGNMENT.SAM :"+Init.getDataDirectory()+s+"sample3"+s+"sample3.sam"
                     + "\n\n\n");
             
           String note=""
                     + ""
                     + "######################################################"
                     + "\n# Test project has been initiated. "
                     + "\n# Please make sure SAMtools script (samtools, bcftools, vcfutils.pl) is already in your computer environment, or use absolute script path"
                     + "\n# Samtools is avalible in Samtools.sourceforge.net"
                     + "\n# Please make sure path of SNPEFF folder has been set in "+Init.getParameterFilePath()
                     + "\n# SnpEff is avalible in snpeff.sourceforge.net"
                     + "\n# Detail information to configure before GIPS running, see Test/README file."
                     + "\n######################################################";

             edu.zju.common.CExecutor.println(note);
             iniOutFile.write(note);
             iniOutFile.closeOutput();
     }
     private void generateCallerScriptFile(){
             String s=edu.zju.common.CExecutor.getFileSeparator();
             CommonOutputFile scriptOutFile=FileFactory.getOutputFile(Init.getScriptDirectory()+s+"samtools_Q13q30_script");
             String note=""
                     + "\n######################################################"
                     + "\n# Author: Zhongxu Zhu"
                     + "\n# Script Requirement:"
                     + "\n# Two command line variables required while script running: \"$1\" is SAM format input file, \"$2\" VCF format output file"
                     + "\n# GIPS will create a \'temporary\' directory in working folder, so intermediate files while running script are recommended to dump into \'temporary\'"
                     + "\n######################################################";
             //common.CExecutor.println(note);
             scriptOutFile.write(note+"\n\n");
             scriptOutFile.write("# change .sam file to .bam file"
                     + "\n samtools view -bS \"$1\" > "+Init.getTemporaryFolderPath()+s+"sample.bam"
                     + "\n# sort"
                     + "\n samtools sort "+Init.getTemporaryFolderPath()+s+"sample.bam "+Init.getTemporaryFolderPath()+s+"sample.sort"
                     + "\n# index"
                     + "\n samtools index "+Init.getTemporaryFolderPath()+s+"sample.sort.bam"
                     + "\n# call"
                     + "\n samtools mpileup -Q 13 -q 20 -ugSf "+Init.getRefDirectory()+s+"Test.fa "+ Init.getTemporaryFolderPath()+s+"sample.sort.bam"+" | bcftools view -bvcg - > "+Init.getTemporaryFolderPath()+s+"sample.bvf"
                     + "\n bcftools view "+Init.getTemporaryFolderPath()+s+"sample.bvf | vcfutils.pl varFilter -d 0 -w 0 > \"$2\""
                     + "");
             scriptOutFile.closeOutput();
     }
     
     private void generateReadMeFile(){
             
             
             
             
             
             
             
             
     }
     
     
}
