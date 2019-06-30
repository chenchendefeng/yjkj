package com.jiayi.platform.security.core.util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

public class FileUtil {
    public static void writeFileToPath(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + File.separator + fileName);
        out.write(file);
        out.flush();
        out.close();
    }
    
    public static void copyFile(String oldPath, String newPath) throws Exception {
    	File oldFile = new File(oldPath);
    	String path = newPath.substring(0, newPath.lastIndexOf(File.separator));
    	File file = new File(path);
    	if (!file.exists()) {
    		file.mkdirs();
        }
    	FileInputStream in = new FileInputStream(oldFile);
    	FileOutputStream out = new FileOutputStream(newPath);
    	
    	byte[] buffer = new byte[2048];
    	int len = 0;
    	while((len = in.read(buffer)) != -1) {
    		out.write(buffer, 0, len);
    	}
    	out.flush();
    	out.close();
    	in.close();
    }

    public static String generateNormalFilePath() {
        Calendar calendar = new GregorianCalendar();
        String fileName = "" + calendar.get(Calendar.YEAR);
        DateFormat df = new SimpleDateFormat("MM-dd");
        fileName += File.separator + df.format(calendar.getTime());
        return fileName;
    }

    public static String generateNormalFileName(String orignalName) {
        String fileName;
        fileName = "" + UUID.randomUUID();
        fileName += "." + FilenameUtils.getExtension(orignalName);
        return fileName;
    }
    
    public static boolean deleteFile(String fileName) {
    	File file = new File(fileName);
    	if (file.exists() && file.isFile()) {
            file.delete();
            return true;
    	}
        return false;
    }

    public static void writeCsvFileToPath(List<String> contents, String columnName, String fileName, String filePath) throws Exception {
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
//        String fn = fileName + ".csv";
        FileOutputStream out = new FileOutputStream(filePath + File.separator + fileName);
        OutputStreamWriter ow = new OutputStreamWriter(out,"GBK");
        ow.write(columnName);
        ow.write("\r\n");
        if (null != contents) {
            for (String content : contents) {
                ow.write(content);
            }
        }
        ow.flush();
        out.flush();
        ow.close();
        out.close();
    }
}

