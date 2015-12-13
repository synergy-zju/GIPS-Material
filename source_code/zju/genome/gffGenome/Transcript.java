package edu.zju.genome.gffGenome;

import edu.zju.genome.effectiveRegion.PositionContainer;
import java.util.LinkedList;

/**
 *
 * @author zzx
 */
public class Transcript extends Module{
    private LinkedList<TranscriptModule> transcriptModules;

        public Transcript(String ModuleID, String feature, String parent, int startPosition, int endPositon) {
                super(ModuleID, feature, parent, startPosition, endPositon);
                this.transcriptModules= new LinkedList<>();
        }
        
    /**
     * 
     * @param moduleID
     * @return return a mRNA module up to module ID
     */
    public TranscriptModule getModule(String moduleID){
           for(TranscriptModule transcriptModule:this.getTranscriptModules()){
                  if (transcriptModule.getID().equals(moduleID)) return transcriptModule;  
           }
           edu.zju.common.CExecutor.println("Not find "+moduleID+" in "+this.getID());
           return null;
    }   
    /**
     * return all modules , include exon ,5utr ,3utr ,cds
     */
    public LinkedList<TranscriptModule> getTranscriptModules(){
           return this.transcriptModules;
    }
    /**
     * this function will just return one type of mRNA module according to input
     * moduleType:"exon","CDS","five_prime_UTR","three_prime_UTR"(from gff3 format file)
     * But it is different between human and rice.You should to judge which is exon or transcrip.
     * @param moduleType
     * @return 
     */
    public LinkedList<Module> get1Module(String moduleType){
           LinkedList<Module> transcriptModuleList =new LinkedList<>();
           if(moduleType.equals("transcript")){
                   if(this.getFeatureType().equals("transcript"))
                           transcriptModuleList.add(this);
                   return transcriptModuleList;      
           }
           for( TranscriptModule transcriptModule:this.getTranscriptModules()){
                  if(transcriptModule.getFeatureType().equals(moduleType)) transcriptModuleList.add(transcriptModule);
           }
           return transcriptModuleList;
    } 
    protected void addTranscriptModule(TranscriptModule rnaModule){
            this.transcriptModules.add(rnaModule);
    }
    protected LinkedList<int[]> get1TypeModuleRegion(String moduleType){
           LinkedList<int[]> list =new LinkedList<>();
           if(moduleType.equals("transcript")){
                   if(this.getFeatureType().equals("transcript"))
                           list.add(this.getSite());
                   return list;      
           }
           for( TranscriptModule transcriptModule:this.getTranscriptModules()){
                  if(transcriptModule.getFeatureType().equals(moduleType)) list.add(transcriptModule.getSite());
           }
           return list;            
    }
    
    protected LinkedList<int[]> getUTR(String feature,String strand){
            PositionContainer positionContainer = new PositionContainer();
            LinkedList<int[]> exon=this.get1TypeModuleRegion("exon");
            LinkedList<int[]> cds=this.get1TypeModuleRegion("CDS");
            return positionContainer.getUTR(exon, cds, feature, strand);
    }
}
