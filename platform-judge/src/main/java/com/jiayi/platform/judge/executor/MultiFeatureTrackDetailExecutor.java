package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.util.LocationUtils;
import com.jiayi.platform.common.web.util.ExportUtil;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.dao.impala.TrackCompareDao;
import com.jiayi.platform.judge.dto.TrackCompareDto;
import com.jiayi.platform.judge.dto.TrackCompareInfo;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.TrackCompareQuery;
import com.jiayi.platform.judge.request.JudgeDetailRequest;
import com.jiayi.platform.judge.request.MultiFeatureTrackDetailRequest;
import com.jiayi.platform.judge.response.MultiFeatureTrackDetailResponse;
import com.jiayi.platform.judge.service.ExportService;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 多特征分析、亲密关系分析轨迹列表共用Executor
 */
@Component
@Slf4j
public class MultiFeatureTrackDetailExecutor implements JudgeDetailExecutor {
    @Autowired
    private TrackCompareDao trackCompareDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;
    @Override
    public <T> List<?> query(T request, PageRequest pageRequest) {
        TrackCompareQuery query = new TrackCompareQuery();
        buildTrackCompareQuery(query, (MultiFeatureTrackDetailRequest) request);
        List<TrackCompareInfo> tracks = trackCompareDao.selectTrackCompareTracks(query);
        List<TrackCompareInfo> srcTracks = tracks.stream()
                .filter(dto -> dto.getObjectValue().equals(query.getSrcObjectValue())).collect(Collectors.toList());
        List<TrackCompareInfo> desTracks = tracks.stream()
                .filter(dto -> dto.getObjectValue().equals(query.getDesObjectValue())).collect(Collectors.toList());
        List<Pair<TrackCompareInfo, TrackCompareInfo>> pairs = TrackCompareExecutor.searchBestMatchByTime(srcTracks, desTracks, query.getTimeDiff());
        Collections.reverse(pairs); // 结果按时间倒序排列
        int startIndex = pageRequest.calOffset().intValue();
        int endIndex = pageRequest.calOffset().intValue() + pageRequest.getPageSize();
        if (endIndex > pairs.size())
            endIndex = pairs.size();
        List<Pair<TrackCompareInfo, TrackCompareInfo>> pagePairs = pairs.subList(startIndex, endIndex);
        return pagePairs.stream().map(TrackCompareDto::new).collect(Collectors.toList()); // 将结果二元组合并
    }

    @Override
    public <T> long count(T request) {
        TrackCompareQuery query = new TrackCompareQuery();
        buildTrackCompareQuery(query, (MultiFeatureTrackDetailRequest) request);
        List<TrackCompareInfo> tracks = trackCompareDao.selectTrackCompareTracks(query);
        List<TrackCompareInfo> srcTracks = tracks.stream()
                .filter(dto -> dto.getObjectValue().equals(query.getSrcObjectValue())).collect(Collectors.toList());
        List<TrackCompareInfo> desTracks = tracks.stream()
                .filter(dto -> dto.getObjectValue().equals(query.getDesObjectValue())).collect(Collectors.toList());
        return TrackCompareExecutor.searchBestMatchByTime(srcTracks, desTracks, query.getTimeDiff()).size();
    }

    @Override
    public List<?> convert2Response(List<?> dots, String objectType) {
        List<MultiFeatureTrackDetailResponse> responseList = new ArrayList<>();
        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dots)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(dots.stream().map(dto -> ((TrackCompareDto) dto).getSrcDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceIds.addAll(dots.stream().map(dto -> ((TrackCompareDto) dto).getDesDeviceId()).filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        dots.forEach(dto -> {
            TrackCompareDto item = (TrackCompareDto) dto;
            MultiFeatureTrackDetailResponse response = new MultiFeatureTrackDetailResponse();
            response.setSrcTime(item.getSrcRecordAt());
            response.setDesTime(item.getDesRecordAt());
            response.setTimeDistance(Math.abs(item.getSrcRecordAt() - item.getDesRecordAt()));
            double srcLng = ImpalaDataUtil.convertLngAndLat2Double(item.getSrcLongitude());
            double srcLat = ImpalaDataUtil.convertLngAndLat2Double(item.getSrcLatitude());
            double desLng = ImpalaDataUtil.convertLngAndLat2Double(item.getDesLongitude());
            double desLat = ImpalaDataUtil.convertLngAndLat2Double(item.getDesLatitude());
            response.setSrcLng(srcLng);
            response.setSrcLat(srcLat);
            response.setDesLng(desLng);
            response.setDesLat(desLat);
            response.setDistance((int) (LocationUtils.distance(srcLng, srcLat, desLng, desLat) * 100) / 100.0);
            String status;
            if (30 / 3.6 * response.getTimeDistance() / 1000 + 300 >= response.getDistance()) { // TODO 确定参数
                status = "匹配";
            } else if (125 / 3.6 * response.getTimeDistance() / 1000 + 300 >= response.getDistance()){
                status = "不确定";
            } else
                status = "偏离";
            response.setStatus(status);
            if (item.getSrcDeviceId() != null && deviceMap.get(item.getSrcDeviceId()) != null) {
                response.setSrcAddress(deviceMap.get(item.getSrcDeviceId()).getAddress());
            }
            if (item.getDesDeviceId() != null && deviceMap.get(item.getDesDeviceId()) != null) {
                response.setDesAddress(deviceMap.get(item.getDesDeviceId()).getAddress());
            }
            responseList.add(response);
        });
        return responseList;
    }

    @Override
    public <T extends JudgeDetailRequest> int exportResult(List<String> contents, T request, long offset) {
        TrackCompareQuery query = new TrackCompareQuery();
        buildTrackCompareQuery(query, (MultiFeatureTrackDetailRequest) request);
        List<TrackCompareInfo> tracks = trackCompareDao.selectTrackCompareTracks(query);
        List<TrackCompareInfo> srcTracks = tracks.stream()
                .filter(dto -> dto.getObjectValue().equals(query.getSrcObjectValue())).collect(Collectors.toList());
        List<TrackCompareInfo> desTracks = tracks.stream()
                .filter(dto -> dto.getObjectValue().equals(query.getDesObjectValue())).collect(Collectors.toList());
        List<Pair<TrackCompareInfo, TrackCompareInfo>> pairs = TrackCompareExecutor.searchBestMatchByTime(srcTracks, desTracks, query.getTimeDiff());
        Collections.reverse(pairs); // 结果按时间倒序排列
        int startIndex = (int) offset;
        int endIndex = (int) (offset + ExportService.LOAD_SIZE);
        if (endIndex > pairs.size())
            endIndex = pairs.size();
        List<TrackCompareDto> data = pairs.subList(startIndex, endIndex).stream().map(TrackCompareDto::new).collect(Collectors.toList());

        Map<Long, Device> deviceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(data)) {
            Set<Long> deviceIds = new HashSet<>();
            deviceIds.addAll(data.stream().map(TrackCompareDto::getSrcDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceIds.addAll(data.stream().map(TrackCompareDto::getDesDeviceId).filter(Objects::nonNull).collect(Collectors.toSet()));
            deviceMap.putAll(deviceService.findByIds(deviceIds));
        }
        int colCount = 7;
        List<String[]> rowData = new ArrayList<>();
        for (TrackCompareDto datum : data) {
            String[] rowValue = new String[colCount];
            int j = 0;
            long timeDiff = Math.abs(datum.getSrcRecordAt() - datum.getDesRecordAt());
            double srcLng = ImpalaDataUtil.convertLngAndLat2Double(datum.getSrcLongitude());
            double srcLat = ImpalaDataUtil.convertLngAndLat2Double(datum.getSrcLatitude());
            double desLng = ImpalaDataUtil.convertLngAndLat2Double(datum.getDesLongitude());
            double desLat = ImpalaDataUtil.convertLngAndLat2Double(datum.getDesLatitude());
            double distance = LocationUtils.distance(srcLng, srcLat, desLng, desLat);
            String status;
            if (30 / 3.6 * timeDiff / 1000 + 300 >= distance) { // TODO 确定参数
                status = "匹配";
            } else if (125 / 3.6 * timeDiff / 1000 + 300 >= distance){
                status = "不确定";
            } else
                status = "偏离";
            rowValue[j++] = status;
            rowValue[j++] = ExportService.SDF.format(datum.getSrcRecordAt());
            rowValue[j++] = ExportService.SDF.format(datum.getDesRecordAt());
            rowValue[j++] = String.valueOf(timeDiff / 1000);
            rowValue[j++] = (datum.getSrcDeviceId() != null && deviceMap.get(datum.getSrcDeviceId()) != null)
                    ? deviceMap.get(datum.getSrcDeviceId()).getAddress()
                    : "";
            rowValue[j++] = (datum.getDesDeviceId() != null && deviceMap.get(datum.getDesDeviceId()) != null)
                    ? deviceMap.get(datum.getDesDeviceId()).getAddress()
                    : "";
            rowValue[j] = String.valueOf((int) (distance * 100) / 100.0);
            rowData.add(rowValue);
        }
        contents.add(ExportUtil.genContentByStringList(rowData));
        return data.size();
    }

    private void buildTrackCompareQuery(TrackCompareQuery query, MultiFeatureTrackDetailRequest request) {
        Map<String, Pair<Long, Long>> tableNamesAndTime =
                impalaTableManager.getValidTableList("query", request.getBeginDate(), request.getEndDate());
        query.setTableNameList(tableNamesAndTime.keySet());
        // 设置临时表和查询表的查询时间及网格
        for (Map.Entry<String, Pair<Long, Long>> namesAndTime : tableNamesAndTime.entrySet()) {
            long beginDate = namesAndTime.getValue().getLeft();
            long endDate = namesAndTime.getValue().getRight();
            if (namesAndTime.getKey().contains("recent")) {
                query.setRecentBeginDate(beginDate);
                query.setRecentEndDate(endDate);
                query.setRecentBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                query.setRecentEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
            } else {
                query.setBeginDate(beginDate);
                query.setEndDate(endDate);
                query.setBeginHours(beginDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
                query.setEndHours(endDate / (ImpalaTableManager.QUERY_SPLIT_HOURS * 60 * 60 * 1000L));
            }
        }
        query.setSrcTrackType(CollectType.valueOf(request.getSrcObjectTypeName().toUpperCase()).code());
        query.setDesTrackType(CollectType.valueOf(request.getDesObjectTypeName().toUpperCase()).code());
        query.setSrcObjectValue(request.getSrcObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
        query.setSrcObjectHash(ImpalaDataUtil.getObjectHash(query.getSrcObjectValue()));
        query.setDesObjectValue(request.getDesObjectValue().toUpperCase().replaceAll("[-:：\\s]", ""));
        query.setDesObjectHash(ImpalaDataUtil.getObjectHash(query.getDesObjectValue()));
        query.setTimeDiff(5 * 60 * 1000L);
        query.setMatching(true);
    }
}
