package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.SrcDao;
import com.jiayi.platform.basic.dao.VendorDao;
import com.jiayi.platform.basic.entity.Src;
import com.jiayi.platform.basic.entity.Vendor;
import com.jiayi.platform.basic.enums.DataTypeEnum;
import com.jiayi.platform.basic.request.SrcRequest;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.web.dto.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SrcService {

    @Autowired
    private SrcDao srcDao;
    @Autowired
    private VendorDao vendorDao;

    public PageResult<Src> findAllSrc(Integer page, Integer size) {
        if (page == null)
            page = 0;
        if (size == null)
            size = 10;
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            Pageable pageable = new PageRequest(page, size, sort);
            Page<Src> pageResult = srcDao.findAllResult(pageable);
            return new PageResult<Src>(pageResult.getContent(), pageResult.getTotalElements(),
                    page, pageResult.getContent().size());
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public List<Src> findSrcByType(Integer dataType) {
        List<Src> list = srcDao.findByDataType(dataType);
        return list;
    }

    public Src addSrc(SrcRequest srcRequest) {
        int count = srcDao.isNameUsed(srcRequest.getName());
        if (count > 0)
            throw new ValidException("名称重复");
        count = srcDao.isCodeUsed(srcRequest.getCode());
        if (count > 0)
            throw new ValidException("数据源编码重复");
        try {
            Src src = new Src();
            src.setName(srcRequest.getName());
            src.setCode(srcRequest.getCode());
            if (srcRequest.getVendorId() != null) {
                Vendor vendor = vendorDao.findById(srcRequest.getVendorId()).get();
                src.setVendor(vendor);
            }
            src.setDataType(srcRequest.getDataType());
            src.setDescription(srcRequest.getDescription());
            src.setCreateDate(new Date());
            src.setUpdateDate(new Date());
            return srcDao.save(src);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public void deleteSrc(Long id) {
        checkDatasource(id);
        try {
            srcDao.deleteById(id);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public Src updateSrc(Long id, SrcRequest srcRequest) {
        Src src0 = srcDao.findById(id).get();
        if (StringUtils.isNotBlank(srcRequest.getName()) && !srcRequest.getName().equals(src0.getName())) {
            int count = srcDao.isNameUsed(srcRequest.getName());
            if (count > 0)
                throw new ValidException("名称重复");
        }
        if (StringUtils.isNotBlank(srcRequest.getCode()) && !srcRequest.getCode().equals(src0.getCode())) {
            DataTypeEnum message = DataTypeEnum.DATA_ACCESS;

            checkDatasource(id);
            int count0 = srcDao.isCodeUsed(srcRequest.getCode());
            if (count0 > 0)
                throw new ValidException("数据源编码重复");
        }
        try {
            if (srcRequest != null) {
                if (StringUtils.isNotBlank(srcRequest.getName()))
                    src0.setName(srcRequest.getName());
                if (srcRequest.getDescription() != null)
                    src0.setDescription(srcRequest.getDescription());
                if (StringUtils.isNotBlank(srcRequest.getCode()))
                    src0.setCode(srcRequest.getCode());
                if (srcRequest.getVendorId() != null) {
                    Vendor vendor = new Vendor();
                    vendor.setId(srcRequest.getVendorId());
                    src0.setVendor(vendor);
                }
                if (srcRequest.getDataType() != null)
                    src0.setDataType(srcRequest.getDataType());
            }
            src0.setUpdateDate(new Date());
            return srcDao.save(src0);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public Src findOneSrc(Long id) {
        try {
            return srcDao.findById(id).get();
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    private void checkDatasource(long id) {
//        DataTypeEnum message = DataTypeEnum.PLACE;
//        int count = srcDao.isSrcUsedInPlace(id);
//        if (count == 0) {
        Src src = srcDao.findById(id).get();
        int count = srcDao.isSrcUsedInDevice(src.getCode());
        DataTypeEnum message = DataTypeEnum.DEVICE;
//        }
        if (count > 0)
            throw new ValidException("删除失败，数据源已关联" + message.getDescription());
    }

    public List<Src> findAll(){
        List<Src> srcs = (List)srcDao.findAll();
        return srcs;
    }
}
