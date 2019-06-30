package com.jiayi.platform.repo.monrepo.service;

import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.repo.monrepo.dao.MonObjectDao;
import com.jiayi.platform.repo.monrepo.dao.MonPersonDao;
import com.jiayi.platform.repo.monrepo.dto.MonObjectDto;
import com.jiayi.platform.repo.monrepo.dto.MonPersonDto;
import com.jiayi.platform.repo.monrepo.enums.MonitorTypeEnum;
import com.jiayi.platform.repo.monrepo.vo.MonObjectSearchVo;
import com.jiayi.platform.repo.monrepo.vo.MonPersonSearchVo;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonExportService {
    @Autowired
    private MonPersonDao monPersonDao;
    @Autowired
    private MonObjectDao monObjectDao;

    private static Integer LOAD_SIZE = 5000;

    public void exportMonObjectAndPerson(long id, Integer status, HttpServletResponse response) {//status：0都有，1人员，2物品
        HSSFWorkbook workbook = new HSSFWorkbook();
        if (0 == status) {
            getPersonSheet(workbook, id);
            getObjectSheet(workbook, id);
        } else if (1 == status) {
            getPersonSheet(workbook, id);
        } else if (2 == status) {
            getObjectSheet(workbook, id);
        } else {
            throw new ArgumentException("request parameter error");
        }
        String fileName = "monExport" + System.currentTimeMillis() + ".xls";
        try {
            this.setResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPersonSheet(HSSFWorkbook workbook, Long id) {
        HSSFSheet personSheet = workbook.createSheet("人员");
        MonPersonSearchVo monPersonSearchVo = new MonPersonSearchVo();
        monPersonSearchVo.setRepoId(id);
        List<MonPersonDto> personData = new ArrayList<>();
        try {
            monPersonSearchVo.setPage(0);
            monPersonSearchVo.setSize(50000);
//            List<MonPersonDto> pageList = monPersonDao.selectMonPersonList(monPersonSearchVo);
            personData.addAll(monPersonDao.selectMonPersonList(monPersonSearchVo));// 获取人员列表
            HSSFRow personTopRow = personSheet.createRow(0);
            String[] pTitle = new String[]{"姓名", "性别", "年龄", "籍贯", "住址", "布控要素", "布控类型", "证件号码", "手机号码", "备注"};
            for (int i = 0; i < pTitle.length; i++) {
                HSSFCell cell = personTopRow.createCell(i);
                cell.setCellValue(pTitle[i]);
            }
            //添加数据
            for (int j = 0; j < personData.size(); j++) {
                HSSFRow row2 = personSheet.createRow(j + 1);
                row2.createCell(0).setCellValue(StringUtils.isBlank(personData.get(j).getName()) ? "" : personData.get(j).getName());
                row2.createCell(1).setCellValue(StringUtils.isBlank(personData.get(j).getSex()) ? "" : personData.get(j).getSex());
                row2.createCell(2).setCellValue(null == personData.get(j).getAge() ? "" : personData.get(j).getAge().toString());
                row2.createCell(3).setCellValue(StringUtils.isBlank(personData.get(j).getBirthplace()) ? "" : personData.get(j).getBirthplace());
                row2.createCell(4).setCellValue(StringUtils.isBlank(personData.get(j).getAddress()) ? "" : personData.get(j).getAddress());
                StringBuffer sb = new StringBuffer();
                personData.get(j).getMonObjList().forEach(item ->
                        sb.append(CollectType.getByCode(item.getObjType()).desc() + " " + item.getObjValue().toUpperCase() + " ")
                );
                row2.createCell(5).setCellValue(sb.toString());
                if (null == personData.get(j).getMonitorType()) {
                    row2.createCell(6).setCellValue("");
                } else {
                    row2.createCell(6).setCellValue(MonitorTypeEnum.getDescByType(personData.get(j).getMonitorType()));
                }
                row2.createCell(7).setCellValue(StringUtils.isBlank(personData.get(j).getCertCode())?"":personData.get(j).getCertCode());
                row2.createCell(8).setCellValue(StringUtils.isBlank(personData.get(j).getPhone())?"":personData.get(j).getPhone());
                row2.createCell(9).setCellValue(StringUtils.isBlank(personData.get(j).getDescription())?"":personData.get(j).getDescription());
            }
        } catch (Exception e) {
            throw new ArgumentException("convert monitor data error", e);
        }
    }

    private void getObjectSheet(HSSFWorkbook workbook, Long id) {
        HSSFSheet objectSheet = workbook.createSheet("物品");
        MonObjectSearchVo monObjectSearchVo = new MonObjectSearchVo();
        monObjectSearchVo.setRepoId(id);
        List<MonObjectDto> objectData = new ArrayList<>();
        try {
            monObjectSearchVo.setPage(0);
            monObjectSearchVo.setSize(50000);// TODO 在配置文件中限制最大导出条数？
            List<MonObjectDto> pageList = monObjectDao.selectMonObjectList(monObjectSearchVo);
            objectData.addAll(monObjectDao.selectMonObjectList(monObjectSearchVo));// 获取物品列表
            HSSFRow objectTopRow = objectSheet.createRow(0);
            String[] oTitle = new String[]{"物品名称", "数据类型", "数据值", "用户姓名", "证件号码", "手机号码", "家庭住址", "备注"};
            for (int i = 0; i < oTitle.length; i++) {
                HSSFCell cell = objectTopRow.createCell(i);
                cell.setCellValue(oTitle[i]);
            }
            for (int j = 0; j < objectData.size(); j++) {
                HSSFRow row2 = objectSheet.createRow(j + 1);
                row2.createCell(0).setCellValue(StringUtils.isBlank(objectData.get(j).getObjectName()) ? CollectType.getByCode(objectData.get(j).getObjectType()).desc() : objectData.get(j).getObjectName());
                row2.createCell(1).setCellValue(CollectType.getByCode(objectData.get(j).getObjectType()).desc());
                row2.createCell(2).setCellValue(objectData.get(j).getObjectValue().toUpperCase());
                row2.createCell(3).setCellValue(StringUtils.isBlank(objectData.get(j).getName())?"":objectData.get(j).getName());
                row2.createCell(4).setCellValue(StringUtils.isBlank(objectData.get(j).getCertCode())?"":objectData.get(j).getCertCode());
                row2.createCell(5).setCellValue(StringUtils.isBlank(objectData.get(j).getPhone())?"":objectData.get(j).getPhone());
                row2.createCell(6).setCellValue(StringUtils.isBlank(objectData.get(j).getAddress())?"":objectData.get(j).getAddress());
                row2.createCell(7).setCellValue(StringUtils.isBlank(objectData.get(j).getDescription())?"":objectData.get(j).getDescription());
            }
        } catch (Exception e) {
            throw new ArgumentException("convert monitor data error");
        }
    }

    //发送响应流
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "UTF-8");//ISO8859-1
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
