/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.options;

import edu.zju.common.CExecutor;
import edu.zju.gips.GIPS;
import edu.zju.gips.ResultOutputer;
import edu.zju.parameter.GIPSJob;
import edu.zju.parameter.GlobalParameter;
import edu.zju.parameter.ParameterLoader;
import java.io.IOException;

/**
 *
 ** @author Zhongxu Zhu
 */
public class Main {

        private static String gipsHeader=""
                                 +"==============================================================================\n"
                                 +"        Gene Identification via Phenotype Sequencing  (Version 1.2.5)\n"                            
                                 +"     Copyright(c) 2014-2015, Zhongxu Zhu, Xin Chen. All Rights Reserved.\n"   
                                 +"==============================================================================\n";
        
        public static void main(String[] args) throws IOException, Exception {
                edu.zju.common.CExecutor.getRunningTime();
                boolean toolType = false;
                GIPSJob job=new GIPSJob();
                ParameterLoader ploader=new ParameterLoader();
                for (int i = 0; i < args.length; i++) {
                        switch (args[i++]) {
                                case "-T" :{
                                        GlobalParameter.setToolType(args[i]);
                                        job.setJobType(args[i]);break;
                                }
                                case "-init":{
                                        Init init=new Init(args[i]);
                                        init.createProjectDirectory();
                                        job.setJobType("init");
                                        break;
                                }
                                case "-p" :{
                                        Init init=new Init(args[i]);break;
                                } 
                                case "-update" :{
                                       job.setJobNeedUpdate();i=i-1;break;
                                }
                                case "-Test" :{
                                        TESTProject testp=new TESTProject();
                                        testp.initiateTestProject(edu.zju.common.CExecutor.getTerminalDirectoy());
                                        job.setJobType("Test");
                                        break;
                                }
                                case "-h" :{
                                        new HelpOption().showHelp();
                                        edu.zju.common.CExecutor.stopProgram("");
                                }
                                case "-H":{
                                        new HelpOption().showHelp();
                                        edu.zju.common.CExecutor.stopProgram("");
                                }
                                default:{
                                        edu.zju.common.CExecutor.stopProgram("Do not find option "+args[i-1]);
                                }
                        }
                }
                System.out.println(gipsHeader);
                job.setGlobalParameter(ploader.loadGlobalParameter(edu.zju.options.Init.getParameterFilePath()));
                job.setSampleParameterBag(ploader.loadSampleSpecificParameter(edu.zju.options.Init.getParameterFilePath()));
                job.jobCheck();
                GIPS gips=new GIPS(job);
                job=gips.gipsRun();
                ResultOutputer ro=new ResultOutputer(job);
                ro.outputResult();
                CExecutor.println("******** ******** ******** ********");
        }

}
