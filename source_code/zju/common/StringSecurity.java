/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 ** @author Zhongxu Zhu
 */
public class StringSecurity {
/*** 
     * MD5加码 生成32位md5码 
     */  
    public String string2MD5(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    }  
      
    public String getBASE64(String s){
            String b;
            if(s==null){
                    return null;
            }
            BASE64Encoder encoder=new BASE64Encoder();
            b=encoder.encode(s.getBytes());
            return b;
    }
    public String getFromBASE64(String s){
            if(s==null){
                    return null;
            }
            BASE64Decoder decoder=new BASE64Decoder();
            try {
                    byte[] b=decoder.decodeBuffer(s);
                    return new String(b);
            } catch (Exception e) {
                    return null;
            }
            
            
    }
    public byte [] geFromBASE64ToByte(String s){
            if(s==null){
                    return null;
            }
            BASE64Decoder decoder=new BASE64Decoder();
            try {
                    byte[] b=decoder.decodeBuffer(s);
                    return b;
            } catch (Exception e) {
                    return null;
            }
    }    
      /**

      * 使用gzip进行压缩
      */
      public String gzip(String primStr) {
            if (primStr == null || primStr.length() == 0) {
                  return primStr;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip=null;
            try {
                  gzip = new GZIPOutputStream(out);
                  gzip.write(primStr.getBytes());
            } catch (IOException e) {
                  e.printStackTrace();
            }finally{
                  if(gzip!=null){
                        try {
                              gzip.close();
                        } catch (IOException e) {
                              e.printStackTrace();
                        }
                  }
            }
            return new sun.misc.BASE64Encoder().encode(out.toByteArray());
      }

      /**
      *
      * <p>Description:使用gzip进行解压缩</p>
      * @param compressedStr
      * @return
      */
      public String gunzip(String compressedStr){
            if(compressedStr==null){
                  return null;
            }

            ByteArrayOutputStream out= new ByteArrayOutputStream();
            ByteArrayInputStream in=null;
            GZIPInputStream ginzip=null;
            byte[] compressed=null;
            String decompressed = null;
            try {
                  compressed = new sun.misc.BASE64Decoder().decodeBuffer(compressedStr);
                  in=new ByteArrayInputStream(compressed);
                  ginzip=new GZIPInputStream(in);

                  byte[] buffer = new byte[1024];
                  int offset = -1;
                  while ((offset = ginzip.read(buffer)) != -1) {
                        out.write(buffer, 0, offset);
                  }
                  decompressed=out.toString();
            } catch (IOException e) {
                  e.printStackTrace();
            } finally {
                  if (ginzip != null) {
                  try {
                        ginzip.close();
                        } catch (IOException e) {
                              
                        }
                  }
                  if (in != null) {
                        try {
                        in.close();
                        } catch (IOException e) {

                        }
                  }
                  if (out != null) {
                        try {
                               out.close();
                        } catch (IOException e) {

                        }
                  }
            }
            return decompressed;
      }

      /**
      * 使用zip进行压缩
      * @param str 压缩前的文本
      * @return 返回压缩后的文本
      */
      public String zip(String str) {
            if (str == null)
            return null;
            byte[] compressed;
            ByteArrayOutputStream out = null;
            ZipOutputStream zout = null;
            String compressedStr = null;
            try {
                  out = new ByteArrayOutputStream();
                  zout = new ZipOutputStream(out);
                  zout.putNextEntry(new ZipEntry("0"));
                  zout.write(str.getBytes());
                  zout.closeEntry();
                  compressed = out.toByteArray();
                  compressedStr = new sun.misc.BASE64Encoder().encodeBuffer(compressed);
            } catch (IOException e) {
                  compressed = null;
            } finally {
                  if (zout != null) {
                        try {
                              zout.close();
                        } catch (IOException e) {
                        }
                  }
                  if (out != null) {
                        try {
                              out.close();
                        } catch (IOException e) {

                        }
                  }
            }
            return compressedStr;
      }

      /**
      * 使用zip进行解压缩
      * @param compressed 压缩后的文本
      * @return 解压后的字符串
      */
      public String unzip(String compressedStr) {
            if (compressedStr == null) {
                  return null;
            }

            ByteArrayOutputStream out = null;
            ByteArrayInputStream in = null;
            ZipInputStream zin = null;
            String decompressed = null;
            try {
                  byte[] compressed = new sun.misc.BASE64Decoder().decodeBuffer(compressedStr);
                  out = new ByteArrayOutputStream();
                  in = new ByteArrayInputStream(compressed);
                  zin = new ZipInputStream(in);
                  zin.getNextEntry();
                  byte[] buffer = new byte[1024];
                  int offset = -1;
                  while ((offset = zin.read(buffer)) != -1) {
                        out.write(buffer, 0, offset);
                  }
                  decompressed = out.toString();
            } catch (IOException e) {
                  decompressed = null;
            } finally {
                  if (zin != null) {
                        try {
                              zin.close();
                        } catch (IOException e) {

                        }
                  }
                  if (in != null) {
                        try {
                              in.close();
                        } catch (IOException e) {

                        }
                 }
                 if (out != null) {
                        try {
                              out.close();
                       } catch (IOException e) {

                       }
                  }
            }
            return decompressed;
      } 
                // 计算文件的 MD5 值
          public String getFileMD5(File file) {
              if (!file.isFile()) {
                  return null;
              }
              MessageDigest digest = null;
              FileInputStream in = null;
              byte buffer[] = new byte[8192];
              int len;
              try {
                  digest =MessageDigest.getInstance("MD5");
                  in = new FileInputStream(file);
                  while ((len = in.read(buffer)) != -1) {
                      digest.update(buffer, 0, len);
                  }
                  BigInteger bigInt = new BigInteger(1, digest.digest());
                  return bigInt.toString(16);
              } catch (Exception e) {
                  e.printStackTrace();
                  return null;
              } finally {
                  try {
                      in.close();
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }

          }

        // 计算文件的 SHA-1 值
        public String getFileSha1(File file) {
            if (!file.isFile()) {
                return null;
            }
            MessageDigest digest = null;
            FileInputStream in = null;
            byte buffer[] = new byte[8192];
            int len;
            try {
                digest =MessageDigest.getInstance("SHA-1");
                in = new FileInputStream(file);
                while ((len = in.read(buffer)) != -1) {
                    digest.update(buffer, 0, len);
                }
                BigInteger bigInt = new BigInteger(1, digest.digest());
                return bigInt.toString(16);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    
    
}
