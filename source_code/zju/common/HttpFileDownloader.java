package edu.zju.common;
import edu.zju.file.Config;
import edu.zju.options.Init; 
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL; 
import java.util.concurrent.TimeoutException;
/**
 *
 ** @author Zhongxu Zhu
 */
public class HttpFileDownloader {
    private String path;
    private int threadCount = Integer.parseInt(Config.getItem("THREADS").trim());  
    private int runningThread = Integer.parseInt(Config.getItem("THREADS").trim());  
    private String localPath;
    private static int reconnectionTime=0;
    
    public void download(String fileUrl,String localFilePath) throws Exception{  
        this.path=fileUrl;
        this.localPath=localFilePath;    
        //1.连接服务器，获取一个文件，获取文件的长度，在本地创建一个跟服务器一样大小的临时文件  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
        conn.setConnectTimeout(5000);  
        conn.setRequestMethod("GET");  
 
        reconnectionTime=reconnectionTime+1;
        int code=0;
        try {//connection time out
                code = conn.getResponseCode(); 
        } catch (Exception e) {
                if(reconnectionTime==5){
                        edu.zju.common.CExecutor.stopProgram("Connect error: Faile to donwload file "+path
                    +"\nIf necessary, you can donwload  by yourself. Then read GIPS usage to set project configuration");
                }else {
        //                System.out.println("超时重新连接");
                        this.download(fileUrl, localFilePath);
                }
        }
        if (code == 200) {  
            //服务器端返回的数据的长度，实际上就是文件的长度  
            int length = conn.getContentLength();  
            //System.out.println("文件总长度："+length);  
            //在客户端本地创建出来一个大小跟服务器端一样大小的临时文件  
            RandomAccessFile raf = new RandomAccessFile(this.localPath, "rwd");  
            //指定创建的这个文件的长度  
            raf.setLength(length);  
            raf.close();  
            //假设是3个线程去下载资源。  
            //平均每一个线程下载的文件大小.  
            int blockSize = length / threadCount; 
            Thread[] threads=new Thread[threadCount];
            for (int threadId = 1; threadId <= threadCount; threadId++) {  
                //第一个线程下载的开始位置  
                int startIndex = (threadId - 1) * blockSize;  
                int endIndex = threadId * blockSize - 1;  
                if (threadId == threadCount) {//最后一个线程下载的长度要稍微长一点  
                    endIndex = length;  
                }  
       //         System.out.println("线程："+threadId+"下载:---"+startIndex+"--->"+endIndex);  
                threads[threadId-1]=new DownLoadThread(path, threadId, startIndex, endIndex, this.localPath);  
                threads[threadId-1].start();
            }  
            for(int threadId=0;threadId<threadCount;threadId++){
                    threads[threadId].join();
            }
        }else{
                edu.zju.common.CExecutor.stopProgram("Connect error: Faile to donwload file "+path
                    +"\nIf necessary, you can donwload  by yourself. Then read GIPS usage to set project configuration");
             //System.out.printf("服务器错误!");  
        }
    }  
      
    /** 
     * 下载文件的子线程  每一个线程下载对应位置的文件 
     * @author  
     * 
     */  
    private class DownLoadThread extends Thread{  
        private int threadId;  
        private int startIndex;  
        private int endIndex;  
        private String localFilePath;
        //record file prefix
        private String recordFilePrefix;
        /** 
         * @param path 下载文件在服务器上的路径 
         * @param threadId 线程Id 
         * @param startIndex 线程下载的开始位置 
         * @param endIndex  线程下载的结束位置 
         */  
        public DownLoadThread(String path, int threadId, int startIndex, int endIndex, String localFilePath) {  
            super();  
            this.threadId = threadId;  
            this.startIndex = startIndex;  
            this.endIndex = endIndex; 
            this.localFilePath=localFilePath;
            this.recordFilePrefix=Init.getProjectDirectory()+edu.zju.common.CExecutor.getFileSeparator()+"."+localFilePath.split(edu.zju.common.CExecutor.getFileSeparator())[localFilePath.split(edu.zju.common.CExecutor.getFileSeparator()).length-1];
        }  
  
        @Override  
        public void run() {  
            try {  
                //检查是否存在记录下载长度的文件，如果存在读取这个文件  
                File tmp_file = new File(this.recordFilePrefix+threadId+".txt");  
                if (tmp_file.exists() && tmp_file.length() > 0) {  
                    FileInputStream fio = new FileInputStream(tmp_file);  
                    byte[] temp = new byte[1024];  
                    int len = fio.read(temp);  
                    String downloadlen = new String(temp, 0, len);  
                    int downloadInt = Integer.parseInt(downloadlen);  
                    startIndex = downloadInt;//修改下载的真实的开始位置  
                    //System.out.println("线程："+threadId+"真实的下载位置："+startIndex+"--->"+endIndex);  
                    fio.close();  
                }  
                  
                  
                URL url = new URL(path);  
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                conn.setConnectTimeout(5000);  
                conn.setRequestMethod("GET");  
                //重要:请求服务器下载部分文件 指定文件的位置  
                conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);  
                //从服务器请求全部资源返回200 ok如果从服务器请求部分资源 返回 206 ok  
                int code = conn.getResponseCode();  
                //System.out.println("code:"+code);  
                InputStream is = conn.getInputStream();//已经设置了请求的位置，返回的是当前位置对应的文件的输入流  
                RandomAccessFile raf = new RandomAccessFile(this.localFilePath, "rwd");  
                //随机写文件的时候从哪个位置开始写  
                raf.seek(startIndex);//定位文件  
                boolean isOver=false;
                int len = 0;  
                byte[] buffer = new byte[1024];  
                int total = startIndex;//已经下载的数据长度 
                long currentTime=System.currentTimeMillis();
                do { 
                    if(is.available()==0){
                            if((System.currentTimeMillis()-currentTime)>10*1000){
                                currentTime=System.currentTimeMillis();
                                if(total>=endIndex){
                        //            System.out.println("线程"+threadId+":over");
                                    isOver=true;
                                }else{
                        //            System.out.println(threadId+":"+"线程错误,超时10s");
                                }
                                break;     
                            }else{
                                    continue;
                            }
                            
                    }else{
                            currentTime=System.currentTimeMillis();
                    }
                    len=is.read(buffer);
                    RandomAccessFile file = new RandomAccessFile(this.recordFilePrefix+threadId+".txt", "rwd");  
                    raf.write(buffer, 0, len);  
                    total += len;  
                    file.write((""+(total)).getBytes());  
                    file.close(); 
                 //   System.out.println(threadId+":"+total);
                }  while(true);
                is.close();  
                raf.close(); 
                if(!isOver){
                        throw  new TimeoutException();
                }else{
                     runningThread--;     
                }
                //System.out.println("线程："+threadId+"下载完毕");  
            } catch (Exception e) {
            //    System.out.println(threadId+":遇到错误，线程重启");
                this.run();
     //           e.printStackTrace();  
            }finally{  
                
                if (runningThread == 0) {//所有的线程执行完毕  
                    for (int i = 1; i <= threadCount; i++) {  
                        File file = new File(this.recordFilePrefix+i+".txt");  
                        file.delete();  
                    }  
                    //System.out.println("文件全部下载完毕!");  
                }  
            }  
        }  
          
    }  
    
}
