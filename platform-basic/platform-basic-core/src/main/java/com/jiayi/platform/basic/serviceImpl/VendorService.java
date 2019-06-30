package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.VendorDao;
import com.jiayi.platform.basic.entity.Vendor;
import com.jiayi.platform.basic.request.VendorRequest;
import com.jiayi.platform.basic.request.VendorSearchRequest;
import com.jiayi.platform.common.enums.ErrorEnum;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.DBException;
import com.jiayi.platform.common.util.BeanUtils;
import com.jiayi.platform.common.web.dto.PageResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VendorService {

    @Autowired
    private VendorDao vendorDao;

    public PageResult<Vendor> findAllVendor(VendorSearchRequest request) {
        try {
            if (request == null || request.getSize() == null || request.getPage() == null) {
                List<Vendor> result = vendorDao.findAll();
                return new PageResult<>(result, (long) result.size(), 0, result.size());
            } else {
                Sort sort = new Sort(Sort.Direction.DESC, "updateDate");
                Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
                Page<Vendor> result = vendorDao.findAll(this.specification(request), pageable);
                return new PageResult<>(result.getContent(), result.getTotalElements(), request.getPage(), result.getSize());
            }
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    private Specification<Vendor> specification(VendorSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            if (StringUtils.isNotBlank(request.getName())) {
                list.add(cb.like(root.get("name"), "%" + request.getName().trim() + "%"));
            }
            if (StringUtils.isNotBlank(request.getCode())) {
                list.add(cb.like(root.get("code"), "%" + request.getCode().trim() + "%"));
            }
            return cb.and(list.toArray(new Predicate[0]));
        };
    }

    public Vendor addVendor(VendorRequest vendorRequest) {
        int nameCount = vendorDao.isNameUsed(vendorRequest.getName());
        int codeCount = vendorDao.isCodeUsed(vendorRequest.getCode());
        if (nameCount > 0) {
            throw new ArgumentException("供应商名字已存在");
        }
        if (codeCount > 0) {
            throw new ArgumentException("供应商编码已存在");
        }
        if(vendorRequest.getCode().length()>9){
            throw new ArgumentException("供应商编码长度过长");
        }
        try {
            Vendor vendor = new Vendor();
            BeanUtils.getInstance().copyPropertiesIgnoreNull(vendor, vendorRequest);
            Date date = new Date();
            vendor.setCreateDate(date);
            vendor.setUpdateDate(date);
            return vendorDao.save(vendor);
        } catch (Exception e) {
            throw new ArgumentException(ErrorEnum.ARGUMENT_ERROR.message(), e);
        }
    }

    public void deleteVendor(Integer id) {
        Vendor vendor = vendorDao.findById(id).orElseThrow(() -> new DBException("find vendor error"));
        Object[] count = vendorDao.isVendorUsed(vendor.getId()).get(0);
        for (int i = 0; i < count.length; ++i) {
            int flag = Integer.valueOf(count[i].toString());
            if (flag > 0) {
                throw new ArgumentException("删除失败，供应商已有关联数据");
            }
        }
        try {
            vendorDao.delete(vendor);
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public void updateVendor(Integer id, VendorRequest vendorRequest) {
        Vendor vendor = vendorDao.findById(id).orElseThrow(() -> new DBException("find vendor error"));
        if (StringUtils.isNotBlank(vendorRequest.getCode()) && !vendorRequest.getCode().equals(vendor.getCode())) {
            int codeCount = vendorDao.isCodeUsed(vendorRequest.getCode());
            if (codeCount > 0) {
                throw new ArgumentException("供应商编码已存在");
            }
        }
        if (StringUtils.isNotBlank(vendorRequest.getName()) && !vendorRequest.getName().equals(vendor.getName())) {
            int nameCount = vendorDao.isNameUsed(vendorRequest.getName());
            if (nameCount > 0) {
                throw new ArgumentException("供应商名字已存在");
            }
        }
        try {
            BeanUtils.getInstance().copyPropertiesIgnoreNull(vendor, vendorRequest);
            vendor.setUpdateDate(new Date());
            vendorDao.save(vendor);
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }

    public List<?> findAll() {
        try {
            return vendorDao.findAll();
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message());
        }
    }

    public void updateVendorExInfo(Integer id, String remark) {
        Vendor vendor = vendorDao.findById(id).orElseThrow(() -> new DBException("find vendor error"));
        try {
            vendor.setExInfo(remark);
            vendor.setUpdateDate(new Date());
            vendorDao.save(vendor);
        } catch (Exception e) {
            throw new DBException(ErrorEnum.DB_ERROR.message(), e);
        }
    }
}
