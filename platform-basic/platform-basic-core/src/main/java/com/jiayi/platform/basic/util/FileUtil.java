package com.jiayi.platform.basic.util;

import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.ServiceException;
import com.opencsv.CSVReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

public class FileUtil {

    public static File upload(MultipartFile file, String uploadPath) throws IOException {
        String fileName = file.getOriginalFilename();
        File targetFile = new File(uploadPath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        File dest = new File(uploadPath + File.separator + fileName);
        file.transferTo(dest);
        return dest;
    }

    public static List<String[]> parseCsvFile(File file){
        BufferedInputStream bis = null;
        InputStreamReader isr = null;
        CSVReader csvReader = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            bis = new BufferedInputStream(inputStream);
            bis.mark(0);
            String charset = "GBK";
            byte[] head = new byte[3];
            bis.read(head);//这里会读取3个字节导致第一个文件内容丢失，后面需要reset
            if (head[0] == (byte) 0xEF && head[1] == (byte) 0xBB && head[2] == (byte) 0xBF) {
                charset = "UTF-8";
            }//解决中文乱码问题
            bis.reset();
            isr = new InputStreamReader(bis, charset);
            csvReader = new CSVReader(isr);
            List<String[]> contents = csvReader.readAll();
            if (contents.size() <= 1) {
                throw new ArgumentException("导入文件不能为空");
            }
            return contents;
        } catch (ArgumentException ae) {
            throw ae;
        } catch (Exception e) {
            throw new ServiceException("error reading csv file content", e);
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
