package edu.zju.genome.effectiveRegion;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 *
 * @author zzx
 */
public class PositionContainer {
    private  TreeSet<Integer> pointPositionContainer = new TreeSet<>();
    
    public void add2Container(LinkedList<int[]> regions){
           for (Iterator<int[]> it = regions.iterator(); it.hasNext();) {
                  int[] region = it.next();
                  this.add2Container(region);
           }
    }
    public void add2Container(int[] region){
           for(int i=region[0];i<=region[1];i++){
                  this.add2Container(i);
           }
    }
    public void add2Container(int position){
           this.pointPositionContainer.add(position);
    }
    
    /**
     * return effective region in gene or chromosome
     * change the point position to segments
     * @return  
     */
    public LinkedList<int[]> converterPointPosition2Region(){
           int former=0;int middle=0; int latter=0;//former记录最前面的点，middle记录latter前1个的点，latter记录最后一个点
           int segment[]=new int[2];
           LinkedList<int [] > segments=new LinkedList<>();
           if(this.pointPositionContainer.size()==0) return segments;
           for(Iterator<Integer> iterator=this.pointPositionContainer.iterator();iterator.hasNext();){
                   latter=iterator.next();
                   if(former==0){//初始化former，middle
                           former=latter;
                           middle=latter;
                   }
                   else{
                           if(latter==middle+1){
                                    middle=latter;
                            }
                            else if(latter!=middle+1){
                                     segment[0]=former;
                                     segment[1]=middle;
                                     former=latter;
                                     middle=latter;
                                     segments.add(segment);
                                     segment=new int[2];
                            }
                    }
            } 
            segment[0]=former;
            segment[1]=middle;
            segments.add(segment);
            return segments;
    }
    /**
     * set the instance null 
     */
    public void cleanContainer(){
           this.pointPositionContainer.clear();
    }
    
    
    public LinkedList<int[]> getUTR(LinkedList<int[]> exon,LinkedList<int[]> cds,String featureType,String strand){
            if(exon.size()==0||cds.size()==0) return this.converterPointPosition2Region();
            if((strand.equals("+")&&featureType.equals("five_prime_UTR"))||(strand.equals("-")&&featureType.equals("three_prime_UTR"))){
                    this.add2Container(exon);
                    int smallestCDSPosition=this.pointPositionContainer.last();
                    for(int[] pos:cds){
                            for(int i=pos[0];i<=pos[1];i++){
                                this.removePosition(i);  
                                if(smallestCDSPosition>i) smallestCDSPosition=i;
                            }
                    } 
                    this.removePosition(">", smallestCDSPosition);  
            }else{
                    this.add2Container(exon);
                    int bigestCDSPosition=this.pointPositionContainer.first();
                    for(int[] pos:cds){
                            for(int i=pos[0];i<=pos[1];i++){
                                this.removePosition(i);  
                                if(bigestCDSPosition<i) bigestCDSPosition=i;
                            }
                    } 
                    this.removePosition("<", bigestCDSPosition);                    
            }
            return this.converterPointPosition2Region();
    }
    private void removePosition(LinkedList<int[]> position){
            for(int[] pos:position){
                    for(int i=pos[0];i<=pos[1];i++){
                        this.removePosition(i);                            
                    }
            }
    }
    private void removePosition(int i){
            this.pointPositionContainer.remove(i);
    }
    private void removePosition(String flag,int marker){
            int pos;
            if(flag.equals(">")){
                    for(Iterator<Integer> iterator=this.pointPositionContainer.iterator();iterator.hasNext();){
                            pos=iterator.next();
                            if(pos>=marker) iterator.remove();
                    } 
            }
            if(flag.equals("<")){
                    for(Iterator<Integer> iterator=this.pointPositionContainer.iterator();iterator.hasNext();){
                            pos=iterator.next();
                            if(pos<=marker) {
                                    iterator.remove();
                            }
                    }                     
            }
    }
    public int getNoRepeatPositionLength(){
            return this.pointPositionContainer.size();
    }

    
    
}
