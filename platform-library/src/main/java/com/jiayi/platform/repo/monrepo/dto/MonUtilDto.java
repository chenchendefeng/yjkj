package com.jiayi.platform.repo.monrepo.dto;

import com.csvreader.CsvReader;
import com.jiayi.platform.common.exception.DBException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonUtilDto {
    public static Long db_maxId;
    private static int seq;
    private static long last_microTs;
    static {
        seq = 0;
        last_microTs = 0;
    }
    public synchronized static Long generateUid() {
        Long curTime = System.currentTimeMillis() * 1000; //微秒
        Long nanoTime = System.nanoTime(); //纳秒
        Long microTime = curTime + (nanoTime - nanoTime / 1000000 * 1000000) / 1000;

//        Integer rand = (int) ((Math.random()*9+1)*100); //生成3位随机数
//        String  uid  = String.valueOf(microTime) + String.valueOf(rand);
//        return Long.valueOf(uid);

        if (db_maxId == null) db_maxId = 0L;
        if (microTime * 1000 <= db_maxId) {
            throw new RuntimeException("error occured for uid revert");
        }

        if (microTime == last_microTs) {
            seq = (seq + 1) % 1000;
            if (seq == 0) {
                throw new RuntimeException("error occured for sequence revert");
            }
        }
        else if (microTime > last_microTs) {
            seq = 0;
        }
        else {
            throw new RuntimeException("error occured for system time revert");
        }
        last_microTs = microTime;

        long uid = microTime * 1000 + seq;
        return uid;
    }

    /**
     * 读取Excel数据内容
     * @param InputStream
     * @return List<Map<String, String>>  Map的key是列Id(0代表第一列)，值是具体内容
     */
    public static List<Map<Integer, String>> readExcelContentByList(MultipartFile file) {
        Workbook wb = null; // XSSFWorkbook/HSSFSheet
        try {
            InputStream is = file.getInputStream();
            wb = WorkbookFactory.create(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取第一个sheet页
        Sheet sheet = wb.getSheetAt(0); // XSSFSheet/HSSFSheet
        //得到总行数
        int rowNum = sheet.getLastRowNum();
        Row row = sheet.getRow(0); // XSSFRow/HSSFRow
        int colNum = row.getPhysicalNumberOfCells();

        List<Map<Integer, String>> list = new ArrayList<Map<Integer,String>>();
        //正文内容应该从第二行开始,第一行为表头的标题
        for (int i = 1; i <= rowNum; i++) {
            row = sheet.getRow(i);
            int j = 0;
            Map<Integer, String> map = new HashMap<Integer, String>();
            while (j < colNum) {
                map.put(j, getCellVal(row.getCell(j)).trim().replaceAll("\t\r", ""));
                j++;
            }
            list.add(map);
        }
        return list;
    }

    public static String getCellVal(Cell cell) { // XSSFCell/HSSFCell
        if (cell == null) return "";

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        else return "";
    }

    public static List<Map<Integer, String>> readCsvContentByList(MultipartFile file) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
//            reader.readLine();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                String item[] = line.split(",");
//                for (String s : item)
//                    System.out.println(s);
//            }

        try {
            InputStream in = file.getInputStream();
            String charset = "GBK";
            byte[] head =  new byte[]{0, 0, 0};
            in.read(head);
            in.close();
            if (head[0] == (byte)0xEF && head[1] == (byte)0xBB && head[2] == (byte)0xBF) {
                charset = "UTF-8";
            }

            ArrayList<String[]> csvContents = new ArrayList<>();
            CsvReader reader = new CsvReader(file.getInputStream(), Charset.forName(charset));  //GBK,UTF-8
            reader.readHeaders(); //跳过第一行标题
            while (reader.readRecord()) {
                System.out.println(reader.getRawRecord());
                csvContents.add(reader.getValues());
            }
            reader.close();

//            for (int row = 0; row < csvContents.size(); row++) {
//                String cell = csvContents.get(row)[0];
//                System.out.println("---->"+cell);
//            }

            List<Map<Integer, String>> list = new ArrayList<Map<Integer,String>>();
            for (int i = 0; i < csvContents.size(); i++) {
                String[] row = csvContents.get(i);
                int j = 0;
                Map<Integer, String> map = new HashMap<Integer, String>();
                while (j < row.length) {
                    map.put(j, row[j].trim().replaceAll("\t\r", ""));
                    j++;
                }
                list.add(map);
            }

            return list;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new DBException("read csv file error", e);
        }
    }

}
