package edu.zju.options;

/**
 *
 ** @author Zhongxu Zhu
 */
public class HelpOption {
        
        
        void showHelp(){
                String helpInfo=""
                        + "\n Program: GIPS (Gene Identification via Phenotype Sequencing)"
                        + "\n"
                        + "\n Usage        : java -jar GIPS.jar [options] "
                        + "\n Common usage: java -jar GIPS.jar -T <tool> [options] -p <project>"
                        + "\n"
                        + "\n Options:   "
                        + "\n -H (-h)           show GIPS usage help"
                        + "\n -Test             initiate a simple example to run \'vcs\' tool"
                        + "\n -init <project>   initiate a project"
                        + "\n -p    <project>   specify  a project"
                        + "\n -T <gips|vcs|filter>   gips tools"
                        + "\n -update           update protocol for optimization"
                        + "\n"
                        + "\n Examples:"
                        + "\n run a simple example"
                        + "\n java -jar GIPS.jar -Test"
                        + "\n java -jar GIPS.jar -T vcs -p Test"
                        + "\n"
                        + "\n initiate a project"
                        + "\n java -jar -init example"
                        + "\n"
                        + "\n run full GIPS"
                        + "\n java -jar -T gips -p example"
                        + "\n"
                        + "\n update protocol for optimization"
                        + "\n java -jar -T gips -update -p example"
                        + "\n"
                        + "\n******NOTES******"
                        + "\n* When a project initiated, please configure the PROJECT.ini file"
                        + "\n* SNPEFF variable in ini file is SNPEFF folder directory not snpeff.jar's path"
                        + "\n* Caler script should have two system variables: \"$1\" is SAM format input file, \"$2\" VCF format output file"
                        + "\n* GIPS will create a \'temporary\' directory in /path/to/project/working folder, so intermediate files while running script are recommended to dump into \'temporary\'"
                        + "\n* User can also read README file in GIPS folder."
                        + "";
                edu.zju.common.CExecutor.println(helpInfo);
                
        }
        
        
        
        
}
