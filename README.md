# Overview #

Next generation sequencing (NGS) has become a fast and cost-effective approach for identifying genes that are associated with Mendelian phenotypes. Such investigations are frequently referred to as phenotype-sequencing research. The success of a phenotype-sequencing research depends on many factors. It is still difficult for an investigator to optimally design a phenotype-sequencing protocol with integral consideration of all factors that affect its chance of success. In particular, after the sequencing results have been obtained, how to optimally design an analysis protocol that fits this specific set of data, and maximizes the chance of reporting true phenotype-associated genes.

Gene identification via phenotype sequencing (GIPS) computes four measurements of study effectiveness to help iterative optimization of a study protocol. The four measurements are:

  1. The chance of reporting true phenotype-associated genes.
  2. The number of random genes that are expected to meet reporting criterion.
  3. The significance of each reported gene to associate with phenotype.
  4. The significance of violating Mendelian assumption, if no gene passes reporting criterion or all reported genes are confirmed false positives.

# Donwload #

  * [GIPS software package](https://raw.githubusercontent.com/RLIBS-ZJU/gips/wiki/GIPS.jar)           Require JAVA 1.7 on Linux.
  * [GIPS manual](https://raw.githubusercontent.com/RLIBS-ZJU/gips/wiki/GIPS_User_Manual.pdf)
  * [Pho2 supressor identification scripts](https://github.com/RLIBS-ZJU/gips/tree/wiki/Scripts)

# Command Options #

Usage:

    java -jar GIPS.jar [options]    

Example: 

    java –Xms5g -jar GIPS.jar -T <tool>  -p /path/to/project_folder

| **Options** |   |Description|
|:------------|:------------|:------------|
| -h (-H) |  | Show help |
| -Test |  | Initiate a new project with test setup|
| -init | /path/to/project_folder | Initiate a new project |
|-p|/path/to/project_folder|Work with an existing project|
| -T | \<gips\|vcs\|filter\> | Select GIPS function. gips: full work flow; vcs: only estimate variant calling sensitivity for each sample; filter: only filter sample variants. Defaults to gips. |
| -update|  | Run GIPS in update mode. GIPS will try to re-use intermediate results produced in the previous run. |

GLOBAL section:  

    PROJECT, REF_GENOME_ANNOTATION.GFF, SNPEFF_GENOME_VERSION, SNPEFF, CANDIDATE_CRITERIA,
    VAR_CALL_SCRIPT, EFF_REGION, VAR_FILTERS, SCORE_MATRIX, MAX_AA_SCORE, NUM_SIM_SNPS, 
    MAX_VAR_DENSITY, LIB_PHENOTYPE_VAR, LIB_VAR_SNPEFF_GENOME_VERSION, 
    LIB_GENOME_ANNOTATION.GFF,  CONTROL

Sample specific section: 

    SAMPLE_NAME, SAMPLE.VCF, READS_ALIGNMENT.SAM, VAR_CALL_SCRIPT, SCORE_MATRIX,
    MAX_AA_SCORE, CONTROL, NUM_SIM_SNPS, MAX_VAR_DENSITY, SPECIFY_HOMO_VDS, 
    SPECIFY_HETERO_VDS, SPECIFY_BVF

# Author #

Zhongxu Zhu , Han Hu, Chuanzao Mao, Xin Chen (xinchen#zju.edu.cn) and Weitao Wang(wwtwin#zju.edu.cn)

If you have any question or suggestion, feel free to contact us.
