package com.jiayi.platform.basic.serviceImpl;

import com.jiayi.platform.basic.dao.PlaceLabelDao;
import com.jiayi.platform.basic.dto.PlaceLabelDto;
import com.jiayi.platform.basic.entity.PlaceLabel;
import com.jiayi.platform.basic.enums.PlaceLabelType;
import com.jiayi.platform.basic.request.PageSearchRequest;
import com.jiayi.platform.basic.request.PlaceLabelRequest;
import com.jiayi.platform.common.exception.ArgumentException;
import com.jiayi.platform.common.exception.ValidException;
import com.jiayi.platform.common.web.dto.PageResult;
import com.jiayi.platform.common.web.util.ExportUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class PlaceLabelService {

    private static Logger log = LoggerFactory.getLogger(PlaceLabelService.class);

    @Autowired
    private PlaceLabelDao placeLabelDao;

    public PageResult<PlaceLabelDto> findPlaceLabelList(PageSearchRequest searchVo) {
        Sort sort = new Sort(Sort.Direction.ASC, "createAt");
        Pageable pageable = PageRequest.of(searchVo.getPage(), searchVo.getSize(), sort);
        Page<PlaceLabel> page = placeLabelDao.findByType(0, pageable);
        List<PlaceLabel> allResult = new ArrayList<>(page.getContent());
        List<String> codes = allResult.stream().map(PlaceLabel::getCode).collect(Collectors.toList());
        findAllChild(codes, allResult);
        Map<String, List<PlaceLabel>> map = StreamSupport.stream(allResult.spliterator(), false)
                .collect(Collectors.groupingBy(PlaceLabel::getPcode));
        List<PlaceLabelDto> result = labelTree("0", map);
        return new PageResult<PlaceLabelDto>(result, page.getTotalElements(), searchVo.getPage(), result.size());
    }

    private void findAllChild(List<String> codes, List<PlaceLabel> allResult){
        List<PlaceLabel> placeLabels = placeLabelDao.findByPcodeIn(codes);
        if(CollectionUtils.isNotEmpty(placeLabels)){
            allResult.addAll(placeLabels);
            if(placeLabels.get(0).getType() != 2){//二级标签就不用往下找了
                findAllChild(placeLabels.stream().map(PlaceLabel::getCode).collect(Collectors.toList()), allResult);
            }
        }
    }

    public List<PlaceLabelDto> tree() {
        Iterable<PlaceLabel> iter = placeLabelDao.findAll();
        Map<String, List<PlaceLabel>> map = StreamSupport.stream(iter.spliterator(), false)
                .collect(Collectors.groupingBy(PlaceLabel::getPcode));
        return labelTree("0", map);
    }

    /**
     * 递归设置子标签
     * @param code
     * @param map
     * @return
     */
    private List<PlaceLabelDto> labelTree(String code, Map<String, List<PlaceLabel>> map){
        List<PlaceLabelDto> placeLabelDtos = new ArrayList<>();
        List<PlaceLabel> placeLabels = map.get(code);
        if(CollectionUtils.isNotEmpty(placeLabels)){
            placeLabels.forEach(a ->{
                PlaceLabelDto placeLabelDto = new PlaceLabelDto(a.getName(), a.getCode(), a.getRemark());
                List<PlaceLabelDto> tree = labelTree(a.getCode(), map);
                placeLabelDto.setNextLevel(tree);
                placeLabelDtos.add(placeLabelDto);
            });
        }
        return placeLabelDtos;
    }

    public PlaceLabel addPlaceLabel(PlaceLabelRequest request) {
        int count = placeLabelDao.isNameUsed(request.getName());
        if(count > 0){
            throw new ValidException("分类（标签）已经存在！");
        }
        if(request.getType() != 0){
            PlaceLabel value = placeLabelDao.findByCodeAndType(request.getPcode(), request.getType() - 1);
            if(value == null){
                throw new ValidException("父级标签不存在");
            }
        }
        try {
            PlaceLabel placeLabel = new PlaceLabel();
            setPlaceLabel(placeLabel, request);
            placeLabel.setCode(buildPlaceLabelCode(request.getType(), request.getPcode()));
            placeLabel.setCreateAt(new Date());
            return placeLabelDao.save(placeLabel);
        } catch (Exception e) {
            throw new ArgumentException("placelabel add error", e);
        }
    }

    /**
     * 生成场所标签编码code
     * 编码方式：由9位数字表示，1-3位表示标签分类、4-6位表示一级标签，7-9位表示二级标签。
     * 系统固有的标签为000-099，用户自定义添加的标签为101-999。
     * 如100148124，表示用户自定义的第1个标签分类里第48个一级标签下的第24个二级标签。
     * @param type
     * @return
     */
    private String buildPlaceLabelCode(Integer type, String pcode) {
        String code = placeLabelDao.findMaxPlaceLabelCodeByTypeAndPcode(type, pcode);
        int beginIndex = type * 3;
        int endIndex = beginIndex + 1;
        PlaceLabelType placeLabelType = PlaceLabelType.getByType(type);
        if(StringUtils.isBlank(code) || Integer.parseInt(code.substring(beginIndex, endIndex)) == 0){
            //第一次添加自定义标签时，标签编码默认值设置
            switch (placeLabelType){
                case LABEL_TYPE:
                    return "101000000";
                case LEVEL_ONE_LABEL:
                    return pcode.substring(0,3) + "101000";
                case LEVEL_TWO_LABEL:
                    return pcode.substring(0,6) + "101";
                default:return null;
            }
        }else{//非第一次添加自定义标签时，获取上一个最大标签编码，按照标签类型截取对应位数递增，末尾补零
            switch (placeLabelType) {
                case LABEL_TYPE:
                    return Integer.parseInt(code.substring(0,3)) + 1 + "000000";
                case LEVEL_ONE_LABEL:
                    return Integer.parseInt(code.substring(0,6)) + 1 + "000";
                case LEVEL_TWO_LABEL:
                    return Integer.parseInt(code) + 1 + "";
                default:
                    return null;
            }
        }
    }

    private void setPlaceLabel(PlaceLabel placeLabel, PlaceLabelRequest request) {
        placeLabel.setName(request.getName());
        placeLabel.setType(request.getType());
        placeLabel.setPcode(request.getPcode());
        placeLabel.setRemark(request.getRemark());
    }

    public List<PlaceLabel> findByPcode(String pcode) {
        return placeLabelDao.findByPcode(pcode);
    }

    public void download(HttpServletResponse response) {
        long start = System.currentTimeMillis();
        List<PlaceLabelDto> data = tree();
        List<String> contents = new ArrayList<>();
        List<String[]> rowData = new ArrayList<>();
        data.forEach(a -> {
            if(CollectionUtils.isNotEmpty(a.getNextLevel())){
                a.getNextLevel().forEach(b -> {
                    if(CollectionUtils.isNotEmpty(b.getNextLevel())) {
                        b.getNextLevel().forEach(c -> {
                            String[] rowValue = new String[]{a.getName(), b.getName(), c.getName()};
                            rowData.add(rowValue);
                        });
                    }else{
                        String[] rowValue = new String[]{a.getName(), b.getName()};
                        rowData.add(rowValue);
                    }
                });
            }else{
                String[] rowValue = new String[]{a.getName()};
                rowData.add(rowValue);
            }
        });
        contents.add(ExportUtil.genContentByStringList(rowData));
        log.debug("load export data used " + (System.currentTimeMillis() - start) + "ms");
        String fileName = "placeLabel" + System.currentTimeMillis();
        if (!ExportUtil.doExport(contents, "标签分类,一级标签,二级标签", fileName, response)){
            log.error("writing csv file error!");
        }
    }

    public void updateRemarkByCode(PlaceLabelDto placeLabelDto) {
        try {
            placeLabelDao.updateRemarkByCode(placeLabelDto.getCode(), placeLabelDto.getRemark());
        } catch (Exception e) {
            throw new ArgumentException("update remark by code error", e);
        }
    }
}
