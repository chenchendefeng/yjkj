package com.jiayi.platform.common.web.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExportUtil {
    public static <T extends CsvContent> String genContent(List<T> dataList) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            CsvWriter csvWriter = new CsvWriter(stream, ',', Charset.forName("utf-8"));
            if (null != dataList) {
                for (CsvContent content : dataList) {
                    String[] colValues = content.getContent().split(",");
                    csvWriter.writeRecord(colValues, true);
                }
            }
            csvWriter.flush();
            csvWriter.close();
            byte[] buffer = stream.toByteArray();
            stream.close();
            String data = Charset.forName("utf-8").decode(ByteBuffer.wrap(buffer)).toString();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String genContentByStringList(List<String[]> dataList) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            CsvWriter csvWriter = new CsvWriter(stream, ',', Charset.forName("utf-8"));
            if (null != dataList) {
                for (String[] content : dataList) {
                    csvWriter.writeRecord(content, true);
                }
            }
            csvWriter.flush();
            csvWriter.close();
            byte[] buffer = stream.toByteArray();
            stream.close();
            String data = Charset.forName("utf-8").decode(ByteBuffer.wrap(buffer)).toString();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean doExportTip(boolean isZeroResult, HttpServletResponse resp, long maxSize) {
        try {
        resp.setContentType("text/html; charset=UTF-8");
        String desc = "exceed ";
        if(isZeroResult) {
            maxSize=0;
            desc = "equal ";
        }
        
        resp.getWriter().print("records "+desc +maxSize+",do not download!");
        resp.getWriter().close();
        return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean doExport(List<String> contents, String colNames, String fileName, HttpServletResponse resp) {
        try {
            String fn = fileName + ".csv";
            resp.setContentType( "application/x-msdownload");
            resp.setHeader("Content-Disposition", "attachment;filename=" + fn);
            OutputStream out = resp.getOutputStream();
            out.write(new byte[]{(byte)0xef,(byte)0xbb,(byte)0xbf});
            OutputStreamWriter ow = new OutputStreamWriter(out,"UTF-8");
//            ow.write(new String(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF }));
            ow.write(colNames);
            ow.write("\r\n");
            if (null != contents) {
                for (String content : contents) {
                    ow.write(content);
                }
            }
            ow.flush();
            ow.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean doExport(List<Map<String, Object>> dataList, String colNames, String mapKey,
            OutputStream os) {
        try {
            CsvWriter csvWriter = new CsvWriter(os, ',', Charset.forName("utf-8"));
            String[] colNameArr = colNames.split(",");
            String[] mapKeyArr = mapKey.split(",");
            csvWriter.writeRecord(colNameArr, true);
            if (null != dataList) {
                for (int i = 0; i < dataList.size(); i++) {
                    String[] content = new String[colNameArr.length];
                    for (int j = 0; j < colNameArr.length; j++) {
                        String temp = dataList.get(i).get(mapKeyArr[j]).toString();
                        if (getType(dataList.get(i).get(mapKeyArr[j]))) {
                            content[j] = " " + temp;
                        } else {
                            content[j] = temp;
                        }
                    }
                    csvWriter.writeRecord(content, true);
                }
            }
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            csvWriter.flush();
            csvWriter.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getType(Object t) {
        if (t instanceof Date) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean doExport(File file, HttpServletResponse response){
        try {
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
            OutputStream os = response.getOutputStream();
            BufferedInputStream bis = null;
            FileInputStream fis = null;
            try {
                byte[] buff = new byte[1024];
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int length;
                while ((length = bis.read(buff)) != -1) {
                    os.write(buff, 0, length);
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bis.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
