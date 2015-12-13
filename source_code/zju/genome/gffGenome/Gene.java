package edu.zju.genome.gffGenome;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author zzx
 */
public class Gene extends edu.zju.genome.abstractGenome.Gene{
    private int[] site=new int[2];//length: 2
    private HashMap<String,Transcript> transcripts;

    
        public Gene(String geneID,String geneName,String chrID,String strand,int start,int end) {
                super(geneID,geneName,chrID,strand);
                this.setPosition(start, end);
                this.transcripts = new HashMap<>();
        }
    

    /**
     * add RNA to a gene
     * @param mRNA 
     */
    protected void addTranscript(Transcript transcript){
           this.transcripts.put(transcript.getID(),transcript);
    }
    /**
     * 
     * @param startPositon 5' position, no matter which strand the gene locate
     * @param endPosition 3' positon, no matter which strand the gene locate
     */
    private void setPosition(int startPositon, int endPosition){
           this.site[0]=startPositon;
           this.site[1]=endPosition;
    }
    /**
     * 
     * @return all mRNA in this gene 
     */
    public LinkedList<Transcript> getTranscripts(){
           return new LinkedList<>(this.transcripts.values()) ;
    }
    /**
     * 
     * @param mRNAID
     * @return return a mRNA according to its id
     */
    public Transcript getTranscript(String transcriptID){
           return this.transcripts.get(transcriptID);
    }
    /**
     * 
     * @return type: array----int[],length: 2
     */
    public int[] getSite(){
           return this.site;
    }
    
    /**
     * moduleType see class Transcript---get1Module(moduleType)
     * moduleType:"CDS","five_prime_UTR","three_prime_UTR","exon" 
     * @param moduleType 
     * @return return one module's region 
     */
    public LinkedList<int[]> get1ModuleRegions(String feature){
           LinkedList<int[]> regions= new LinkedList<>(); 
           for(Transcript transcript : this.getTranscripts()){
                  regions.addAll(transcript.get1TypeModuleRegion(feature));
           }
           if((feature.equals("five_prime_UTR")||feature.equals("three_prime_UTR"))&&regions.size()==0){
                   for(Transcript transcript:this.getTranscripts()){
                           regions.addAll(transcript.getUTR(feature, this.getStrand()));
                   }
           }
           return regions;
    }  
    protected void renewTranscript(Transcript transcript){
            this.transcripts.put(transcript.getID(), transcript);
    }

    protected void addTranscriptModule(TranscriptModule transcriptModule){
            Transcript transcript=this.getTranscript(transcriptModule.getParent());
            transcript.addTranscriptModule(transcriptModule);
            this.renewTranscript(transcript);
    }
    
    
}
