package com.jiayi.platform.common.util;


import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    /*在这个方法里面进行解压*/

    static public class UnZipData{
        private byte unZipData[];
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public byte[] getUnZipData() {
            return unZipData;
        }

        public void setUnZipData(byte[] unZipData) {
            this.unZipData = unZipData;
        }
    }

    /**
     * 数据解压
     * @param bytesArray
     * @return
     * @throws IOException
     */
    public static UnZipData unCompression(byte[] bytesArray) throws Exception {

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(bytesArray)));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ZipEntry ze  = null;

        UnZipData unZipData = new UnZipData();

        try {
            while((ze = zis.getNextEntry()) != null){

                int fileSize = (int) ze.getSize();
                if (fileSize < 0){
                    fileSize = 64*1024;
                }
                unZipData.setName(ze.getName());

                byte[] b = new byte[fileSize];
                int rb = 0, chunk = 0;

                //通过循环一次把数据全部都读到内存中去
                while(zis.available() > 0)
                {
                    chunk = zis.read(b, 0, fileSize);
                    if (chunk <= 0)
                    {
                        break;
                    }
                    bos.write(b,0,chunk);
                    rb += chunk;
                }
                break;
            }
            unZipData.setUnZipData(bos.toByteArray());
        }catch (Exception e){
            unZipData.setUnZipData(bos.toByteArray());
        }finally {
            if (zis != null){
                zis.close();
            }
            if (bos != null){
                bos.close();
            }
        }

        return unZipData;
    }

    public static void bytesToZipFile(byte data[],String path,String fileEntry) throws IOException{
        File zipFile = new File(path);
        ZipOutputStream zipOut = null ; // 声明压缩流对象
        FileOutputStream outputStream = new FileOutputStream(zipFile);
        zipOut = new ZipOutputStream(outputStream) ;
        zipOut.putNextEntry(new ZipEntry(fileEntry)) ; // 设置ZipEntry对象
        zipOut.setComment("jykj");  // 设置注释
        zipOut.write(data);
        zipOut.finish();
        zipOut.close() ;
        outputStream.close();
    }

    public static byte[] bytesToZipBytes(byte data[],String fileEntry) throws IOException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOut = null ; // 声明压缩流对象
        zipOut = new ZipOutputStream(outputStream) ;
        zipOut.putNextEntry(new ZipEntry(fileEntry)) ; // 设置ZipEntry对象
        zipOut.setComment("jykj");  // 设置注释
        zipOut.write(data);
        zipOut.finish();
        zipOut.close() ;
        byte zipData[] = outputStream.toByteArray();
        outputStream.close();
        return zipData;
    }



}
