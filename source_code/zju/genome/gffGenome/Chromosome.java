/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.genome.gffGenome;

import edu.zju.common.LineHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author zzx
 */
public class Chromosome extends edu.zju.genome.abstractGenome.Chromosome{
    private HashMap<String,Gene> genes;
    private HashMap<String,String> transcriptIDInGene;
    private HashSet<String> parentIdSet;
    private int length=0;

        public Chromosome(String chrID,LinkedList<String> chromosomeInformation) {
                super(chrID);
              genes = new HashMap<>();
              this.transcriptIDInGene = new HashMap<>();
              this.parentIdSet=new HashSet<>();
              this.setChromosome(chromosomeInformation);                 
        }
    
//
//"attributes" description!!!!!
//A list of feature attributes in the format tag=value. Multiple tag=value pairs are separated by semicolons. URL escaping rules are used for tags or values containing the following characters: ",=;". Spaces are allowed in this field, but tabs must be replaced with the %09 URL escape. Attribute values do not need to be and should not be quoted. The quotes should be included as part of the value by parsers and not stripped.
//
//These tags have predefined meanings:
//
//ID
//Indicates the ID of the feature. IDs for each feature must be unique within the scope of the GFF file. In the case of discontinuous features (i.e. a single feature that exists over multiple genomic locations) the same ID may appear on multiple lines. All lines that share an ID collectively represent a single feature.
//Name
//Display name for the feature. This is the name to be displayed to the user. Unlike IDs, there is no requirement that the Name be unique within the file.
//Alias
//A secondary name for the feature. It is suggested that this tag be used whenever a secondary identifier for the feature is needed, such as locus names and accession numbers. Unlike ID, there is no requirement that Alias be unique within the file.
//Parent
//Indicates the parent of the feature. A parent ID can be used to group exons into transcripts, transcripts into genes, an so forth. A feature may have multiple parents. Parent can *only* be used to indicate a partof relationship.
//Target
//Indicates the target of a nucleotide-to-nucleotide or protein-to-nucleotide alignment. The format of the value is "target_id start end [strand]", where strand is optional and may be "+" or "-". If the target_id contains spaces, they must be escaped as hex escape %20.
//Gap
//The alignment of the feature to the target if the two are not collinear (e.g. contain gaps). The alignment format is taken from the CIGAR format described in the Exonerate documentation. (http://cvsweb.sanger.ac.uk/cgi-bin/cvsweb.cgi/exonerate ?cvsroot=Ensembl). See "THE GAP ATTRIBUTE" for a description of this format.
//Derives_from
//Used to disambiguate the relationship between one feature and another when the relationship is a temporal one rather than a purely structural "part of" one. This is needed for polycistronic genes. See "PATHOLOGICAL CASES" for further discussion.
//Note
//A free text note.
//Dbxref
//A database cross reference. See the section "Ontology Associations and Db Cross References" for details on the format.
//Ontology_term
//A cross reference to an ontology term. See the section "Ontology Associations and Db Cross References" for details.
//Is_circular
//A flag to indicate whether a feature is circular. See extended discussion below.
//Multiple attributes of the same type are indicated by separating the values with the comma "," character, as in:
//
//Parent=AF2312,AB2812,abc-3
//In addition to Parent, the Alias, Note, Dbxref and Ontology_term attributes can have multiple values.
//
//Note that attribute names are case sensitive. "Parent" is not the same as "parent".
//
//All attributes that begin with an uppercase letter are reserved for later use. Attributes that begin with a lowercase letter can be used freely by applications.
        
        
        
        
    /**
     * input one chromosome information
     * using this information to build chromosome
     * split chromosome to lots of genes
     * @param chromosomeInformation 
     */
    private void setChromosome(LinkedList<String> chromosomeInformation){
            String feature=null;
            String id=null;
            LineHandler lh = new LineHandler();
            Gene gene;
            String geneIDTemp = null;
            for(String line:chromosomeInformation){
                  if(line.trim().isEmpty())continue;
                  lh.splitByTab(line);
                  HashMap<String,String> tagValuePaire=new HashMap<>();
                  for(String temp:lh.linesplit[8].split(";")){
                          String paire[]=temp.split("=");
                          if(paire.length<2){
                                  tagValuePaire.put(paire[0], "");
                          }else{
                                  tagValuePaire.put(paire[0].trim(), paire[1].trim());
                          }
                  }
                  //id=lh.regexMatch(lh.linesplit[8], "ID=(.*?);");
                  id=tagValuePaire.get("ID");
                  feature=lh.linesplit[2];
                  int startPosition=Integer.parseInt(lh.linesplit[3]);
                  int endPosition=Integer.parseInt(lh.linesplit[4]);                  
                  if(feature.equals("gene")){
                          //String geneName=lh.regexMatch(lh.linesplit[8], "Name=(.*?)");
                          String geneName=tagValuePaire.get("Name");
                          String strand=lh.linesplit[6];
                          gene = new Gene(id,geneName,this.getID(),strand,startPosition,endPosition);
                          this.addGene(gene);
                          Transcript transcript = new Transcript(id, "transcript", id, startPosition, endPosition);
                          this.addTranscript(transcript);
                          geneIDTemp=id;
                          continue;
                  }
                  String parent= lh.regexMatch(lh.linesplit[8], "Parent=(.*?)(;|$)");
                  if(parent==null) parent=geneIDTemp;
                  if(this.parentIdSet.contains(parent)){
                          Transcript transcript= new Transcript(id, feature, parent, startPosition, endPosition);
                          this.addTranscript(transcript);
                  }
                  TranscriptModule transcriptModule = new TranscriptModule(id, feature, parent, startPosition, endPosition);
                  if(!this.addTranscriptModule(transcriptModule)){
                          transcriptModule = new TranscriptModule(id, feature, geneIDTemp, startPosition, endPosition);//Transcript module's parent may be null. For instance, the feature of tRNA.  And also ,there is another condition that the rna's parent is no matter annotated any more, for instance gene29944,gene5104,gene2577
                          this.addTranscriptModule(transcriptModule);
                  } 
            }
            this.transcriptIDInGene=null;
            this.parentIdSet=null;
            chromosomeInformation.clear();
    }
    /**
     * add gene into chromosome
     * @param gene 
     */
    private void addGene(Gene gene){
           this.genes.put(gene.getID(), gene);
    }
    /**
     * return gene according to geneID
     * @param geneID
     * @return return the gene according to gene id
     */
    public Gene getGene(String geneID){
           return this.genes.get(geneID);
    }

    /**
     * return all the genes in this chromosome
     * @return 
     */
    public LinkedList<Gene> getGenes(){
           LinkedList<Gene> genes =new LinkedList<>(this.genes.values());
           return genes;
    }
    /**
     * 
     * @return all the gene ids in this chromosome 
     */
    public Set<String> getGeneIDs(){
           return  new TreeSet<>(this.genes.keySet());
    }
    private void addTranscript(Transcript transcript){
         this.transcriptIDInGene.put(transcript.getID(), transcript.getParent());
         this.parentIdSet.add(transcript.getParent());
         Gene gene=this.getGene(transcript.getParent());
         gene.addTranscript(transcript);
         this.renewGene(gene);
    }
    private void renewGene(Gene gene){
           this.genes.put(gene.getID(), gene);            
    }
    private String getGeneIDAccordingToRNAID(String rnaId){
            return this.transcriptIDInGene.get(rnaId);
    }
    
    private boolean addTranscriptModule(TranscriptModule transcriptModule){
            Gene gene= this.getGene(this.getGeneIDAccordingToRNAID(transcriptModule.getParent()));
            if(gene==null) return false;
            gene.addTranscriptModule(transcriptModule);
            this.renewGene(gene);
            return true;
    }
    
}
