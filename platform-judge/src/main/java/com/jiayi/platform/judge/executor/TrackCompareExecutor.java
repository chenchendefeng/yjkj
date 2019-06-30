package com.jiayi.platform.judge.executor;

import com.jiayi.platform.basic.entity.Device;
import com.jiayi.platform.basic.service.DeviceService;
import com.jiayi.platform.common.enums.CollectType;
import com.jiayi.platform.common.util.LocationUtils;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.judge.common.bean.PageInfo;
import com.jiayi.platform.judge.common.bean.PageRequest;
import com.jiayi.platform.judge.common.bean.PageResult;
import com.jiayi.platform.judge.dao.impala.TrackCompareDao;
import com.jiayi.platform.judge.dto.TrackCompareAllInfo;
import com.jiayi.platform.judge.dto.TrackCompareDto;
import com.jiayi.platform.judge.dto.TrackCompareInfo;
import com.jiayi.platform.judge.manage.ImpalaTableManager;
import com.jiayi.platform.judge.query.TrackCompareQuery;
import com.jiayi.platform.judge.query.TrackCompareSingleQuery;
import com.jiayi.platform.judge.request.JudgeRequest;
import com.jiayi.platform.judge.request.TrackCompareRequest;
import com.jiayi.platform.judge.response.TrackCompareResponse;
import com.jiayi.platform.judge.util.ImpalaDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TrackCompareExecutor implements JudgeExecutor {
    @Autowired
    private TrackCompareDao trackCompareDao;

    @Autowired
    private ImpalaTableManager impalaTableManager;
    @Autowired
    private DeviceService deviceService;
    @Override
    public <T extends JudgeRequest> List<?> query(T request, PageRequest pageRequest) {
        TrackCompareQuery query = new TrackCompareQuery();
        buildTrackCompareQuery(query, (TrackCompareRequest) request);
        if (query.getMatching()) { // 只获取匹配结果
            long selectStart = System.currentTimeMillis();
            List<TrackCompareInfo> tracks = trackCompareDao.selectTrackCompareTracks(query);
            List<TrackCompareInfo> srcTracks = tracks.stream()
                    .filter(dto -> dto.getObjectValue().equals(query.getSrcObjectValue())).collect(Collectors.toList());
            List<TrackCompareInfo> desTracks = tracks.stream()
                    .filter(dto -> dto.getObjectValue().equals(query.getDesObjectValue())).collect(Collectors.toList());
            long matchStart = System.currentTimeMillis();
            List<Pair<TrackCompareInfo, TrackCompareInfo>> pairs = searchBestMatchByTime(srcTracks, desTracks, query.getTimeDiff());
            long matchEnd = System.currentTimeMillis();
            Collections.reverse(pairs); // 结果按时间倒序排列
            int startIndex = pageRequest.calOffset().intValue();
            int endIndex = pageRequest.calOffset().intValue() + pageRequest.getPageSize();
            if (endIndex > pairs.size())
                endIndex = pairs.size();
            List<Pair<TrackCompareInfo, TrackCompareInfo>> pagePairs = pairs.subList(startIndex, endIndex);
            log.info("get track time: " + (matchStart - selectStart) / 100 / 10.0 + "s, " +
                    "matching time: " + (matchEnd - matchStart) / 100 / 10.0 + "s");
            return pagePairs.stream().map(TrackCompareDto::new).collect(Collectors.toList()); // 将结果二元组合并
        } else { // 获取全部结果
            query.setLimit(pageRequest.getPageSize());
            query.setOffset(pageRequest.calOffset());
            TrackCompareSingleQuery srcQuery = new TrackCompareSingleQuery();
            TrackCompareSingleQuery desQuery = new TrackCompareSingleQuery();
            buildTrackCompareSingleQuery(srcQuery, desQuery, query);
//            long countStart = System.currentTimeMillis();
//            Long srcCount = trackCompareDao.countTrackCompareAll(srcQuery);
//            Long desCount = trackCompareDao.countTrackCompareAll(desQuery);
//            Long srcCount = 0L, desCount = 0L;
//            Future<Long> srcCountFuture = ThreadPoolUtil.getInstance().submit(() -> trackCompareDao.countTrackCompareAll(srcQuery));
//            Future<Long> desCountFuture = ThreadPoolUtil.getInstance().submit(() -> trackCompareDao.countTrackCompareAll(desQuery));
//            try {
//                srcCount = srcCountFuture.get();
//                desCount = desCountFuture.get();
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
            long countEnd = System.currentTimeMillis();
            TrackCompareAllInfo timeInfo;
//            if (srcCount >= desCount)
            if (!((TrackCompareRequest) request).getSrcObjectTypeName().equalsIgnoreCase("mac")
                    && ((TrackCompareRequest) request).getDesObjectTypeName().equalsIgnoreCase("mac"))
                timeInfo = trackCompareDao.selectTrackCompareTime(desQuery);
            else
                timeInfo = trackCompareDao.selectTrackCompareTime(srcQuery);
            long findEnd = System.currentTimeMillis();
            ((TrackCompareRequest) request).setBeginDate(timeInfo.getMinHappenAt());
            ((TrackCompareRequest) request).setEndDate(timeInfo.getMaxHappenAt());
            buildTrackCompareQuery(query, (TrackCompareRequest) request);
            long selectStart = System.currentTimeMillis();
            List<TrackCompareInfo> tracks = trackCompareDao.selectTrackCompareTracks(query);
            List<TrackCompareInfo> srcTracks = tracks.stream()
                    .filter(dto -> dto.getObjectValue().equals(query.getSrcObjectValue())).collect(Collectors.toList());
            List<TrackCompareInfo> desTracks = tracks.stream()
                    .filter(dto -> dto.getObjectValue().equals(query.getDesObjectValue())).collect(Collectors.toList());
            long matchStart = System.currentTimeMillis();
            List<Pair<TrackCompareInfo, TrackCompareInfo>> pairs = searchBestMatchByTime(srcTracks, desTracks, query.getTimeDiff());

            List<TrackCompareDto> resultList = new ArrayList<>();
            Map<TrackCompareInfo, TrackCompareInfo> valueMap = pairs.stream()
                    .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            for (int i = tracks.size() - 1; i >= 0; i--) {
                if (tracks.get(i).getObjectValue().equals(query.getSrcObjectValue())) {
                    resultList.add(new TrackCompareDto(tracks.get(i), valueMap.getOrDefault(tracks.get(i), null)));
                } else if (!valueMap.containsValue(tracks.get(i))) {
                    resultList.add(new TrackCompareDto(null, tracks.get(i)));
                }
            }
            long matchEnd = System.currentTimeMillis();
            log.info("get time info time: " + (findEnd - countEnd) / 100 / 10.0 + "s, " +
                    "get track time: " + (matchStart - selectStart) / 100 / 10.0 + "s, " +
                    "matching time: " + (matchEnd - matchStart) / 1000.0 + "s. ");

            return resultList;
        }
    }

    @Override
    public <T extends JudgeRequest> long count(T request) {
        TrackCompareQuery query = new TrackCompareQuery();
        buildTrackCompareQuery(query, (TrackCompareRequest) request);
        if (query.getMatching()) {
            List<TrackCompareInfo> tracks = trackCompareDao.selectTrackCompareTracks(query);
            List<TrackCompareInfo> srcTracks = tracks.stream()
                    .filter(dto -> dto.getObjectValue().equals(query.getSrcObjectValue())).collect(Collectors.toList());
            List<TrackCompareInfo> desTracks = tracks.stream()
                    .filter(dto -> dto.getObjectValue().equals(query.getDesObjectValue())).collect(Collectors.toList());
            return searchBestMatchByTime(srcTracks, desTracks, query.getTimeDiff()).size();
        } else {
            TrackCompareSingleQuery srcQuery = new TrackCompareSingleQuery();
            TrackCompareSingleQuery desQuery = new TrackCompareSingleQuery();
            buildTrackCompareSingleQuery(srcQuery, desQuery, query);
            if (!((TrackCompareRequest) request).getSrcObjectTypeName().equalsIgnoreCase("mac")
                    && ((TrackCompareRequest) request).getDesObjectTypeName().equalsIgnoreCase("mac"))
                return trackCompareDao.countTrackCompareAll(desQuery);
            else
                return trackCompareDao.countTrackCompareAll(srcQuery);
//            if (srcCount >= desCount)
//            return srcCount;
//            return desCount;
        }
    }

    @Override
    public List<?> queryCache(Long queryHistoryId, PageRequest pageRequest) {
        return trackCompareDao.selectTrackCompareResult(queryHistoryId, pageRequest.getPageSize(), pageRequest.calOffset());
    }

    @Override
    public long countCache(Long queryHistoryId) {
        return trackCompareDao.countTrackCompareResult(queryHistoryId);
    }

    @Override
    public <T extends JudgeRequest> long cache(T request, long queryHistoryId) {
        TrackCompareQuery query = new TrackCompareQuery();
        buildTrackCompareQuery(query, (TrackCompareRequest) request);
        if (query.getMatching()) {
            List<TrackCompareInfo> tracks = trackCompareDao.selectTrackCompareTracks(query);
            List<TrackCompareInfo> srcTracks = tracks.stream()
                    .filter(dto -> dto.getObjectValue().equals(query.getSrcObjectValue())).collect(Collectors.toList());
            List<TrackCompareInfo> desTracks = tracks.stream()
                    .filter(dto -> dto.getObjectValue().equals(query.getDesObjectValue())).collect(Collectors.toList());
            List<Pair<TrackCompareInfo, TrackCompareInfo>> pairs = searchBestMatchByTime(srcTracks, desTracks, query.getTimeDiff());
            Collections.reverse(pairs);
            trackCompareDao.insertTrackCompareResult(queryHistoryId, pairs.stream().map(TrackCompareDto::new).collect(Collectors.toList()));
            return countCache(queryHistoryId);
        } else {
            return count(request);
        }
    }

    @Override
    public <T extends JudgeRequest> PageResult convert2Response(List<?> dots, long count, T request, PageRequest pageRequest) {
        List<TrackCompareResponse> responseList = new ArrayList<>();
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
            TrackCompareResponse response = new TrackCompareResponse();
            response.setSrcTime(item.getSrcRecordAt());
            response.setDesTime(item.getDesRecordAt());
            if (item.getSrcRecordAt() != null && item.getDesRecordAt() != null)
                response.setTimeDistance(Math.abs(item.getSrcRecordAt() - item.getDesRecordAt()));
            Double srcLng = null;
            Double srcLat = null;
            Double desLng = null;
            Double desLat = null;
            if (item.getSrcLongitude() != null && item.getSrcLatitude() != null) {
                srcLng = ImpalaDataUtil.convertLngAndLat2Double(item.getSrcLongitude());
                srcLat = ImpalaDataUtil.convertLngAndLat2Double(item.getSrcLatitude());
            }
            if (item.getDesLongitude() != null && item.getDesLatitude() != null) {
                desLng = ImpalaDataUtil.convertLngAndLat2Double(item.getDesLongitude());
                desLat = ImpalaDataUtil.convertLngAndLat2Double(item.getDesLatitude());
            }
            response.setSrcLng(srcLng);
            response.setSrcLat(srcLat);
            response.setDesLng(desLng);
            response.setDesLat(desLat);
            if (srcLng != null && desLng != null) {
                response.setDistance(LocationUtils.distance(srcLng, srcLat, desLng, desLat));
            }
            if (item.getSrcDeviceId() != null && deviceMap.get(item.getSrcDeviceId()) != null) {
                response.setSrcAddress(deviceMap.get(item.getSrcDeviceId()).getAddress());
            }
            if (item.getDesDeviceId() != null && deviceMap.get(item.getDesDeviceId()) != null) {
                response.setDesAddress(deviceMap.get(item.getDesDeviceId()).getAddress());
            }
            responseList.add(response);
        });
        PageInfo pageInfo = new PageInfo(dots.size(), count, pageRequest);
        return new PageResult<>(responseList, pageInfo);
    }

    @Override
    public <T extends JudgeRequest> int exportResult(List<String> contents, T request, long offset, long queryId) {
        // 轨迹比对暂无导出
        return 0;
    }

    private void buildTrackCompareQuery(TrackCompareQuery query, TrackCompareRequest request) {
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
        query.setTimeDiff(request.getDuration() * 60 * 1000L);
        query.setMatching(request.getMatch() == 1);
    }

    private void buildTrackCompareSingleQuery(TrackCompareSingleQuery srcQuery, TrackCompareSingleQuery desQuery, TrackCompareQuery query) {
        srcQuery.setTableNameList(query.getTableNameList());
        srcQuery.setObjectValue(query.getSrcObjectValue());
        srcQuery.setObjectHash(query.getSrcObjectHash());
        srcQuery.setTrackType(query.getSrcTrackType());
        srcQuery.setRecentBeginDate(query.getRecentBeginDate());
        srcQuery.setRecentEndDate(query.getRecentEndDate());
        srcQuery.setRecentBeginHours(query.getRecentBeginHours());
        srcQuery.setRecentEndHours(query.getRecentEndHours());
        srcQuery.setBeginDate(query.getBeginDate());
        srcQuery.setEndDate(query.getEndDate());
        srcQuery.setBeginHours(query.getBeginHours());
        srcQuery.setEndHours(query.getEndHours());
        srcQuery.setLimit(query.getLimit());
        srcQuery.setOffset(query.getOffset());

        desQuery.setTableNameList(query.getTableNameList());
        desQuery.setObjectValue(query.getDesObjectValue());
        desQuery.setObjectHash(query.getDesObjectHash());
        desQuery.setTrackType(query.getDesTrackType());
        desQuery.setRecentBeginDate(query.getRecentBeginDate());
        desQuery.setRecentEndDate(query.getRecentEndDate());
        desQuery.setRecentBeginHours(query.getRecentBeginHours());
        desQuery.setRecentEndHours(query.getRecentEndHours());
        desQuery.setBeginDate(query.getBeginDate());
        desQuery.setEndDate(query.getEndDate());
        desQuery.setBeginHours(query.getBeginHours());
        desQuery.setEndHours(query.getEndHours());
        desQuery.setLimit(query.getLimit());
        desQuery.setOffset(query.getOffset());

    }

    static List<Pair<TrackCompareInfo, TrackCompareInfo>> searchBestMatchByTime(List<TrackCompareInfo> track1, List<TrackCompareInfo> track2,
                                                                                long maxTimeDiff) {
        return searchBestMatchByTime(track1, 0, track1.size(), track2, 0, track2.size(), maxTimeDiff);
    }

    private static List<Pair<TrackCompareInfo, TrackCompareInfo>> searchBestMatchByTime(List<TrackCompareInfo> track1, int startIdx1, int endIndex1,
                                                                                 List<TrackCompareInfo> track2, int startIdx2, int endIndex2,
                                                                                 long maxTimeDiff) {
        List<Pair<TrackCompareInfo, TrackCompareInfo>> result = new ArrayList<>();
        Pair<Integer, Integer> matchIndices = findGlobalBestMatchByTime(track1, startIdx1, endIndex1, track2, startIdx2, endIndex2, maxTimeDiff);
        int bestI = matchIndices.getLeft();
        int bestJ = matchIndices.getRight();
        if (bestI < 0) {
            return result;
        }
        result.addAll(searchBestMatchByTime(track1, startIdx1, bestI, track2, startIdx2, bestJ, maxTimeDiff));
        result.add(Pair.of(track1.get(bestI), track2.get(bestJ)));
        result.addAll(searchBestMatchByTime(track1, bestI + 1, endIndex1, track2, bestJ + 1, endIndex2, maxTimeDiff));
        return result;
    }

    /**
     * 从2条轨迹中找到最佳匹配对（时间差最小）
     *
     * @param track1      轨迹1
     * @param startIdx1   开始下标
     * @param endIndex1   结束下标（不含）
     * @param track2      轨迹2
     * @param startIdx2   开始下标
     * @param endIndex2   结束下标（不含）
     * @param maxTimeDiff 匹配成功的最大时间差
     * @return 最佳匹配对， （-1，-1）表示没有成功的匹配
     */
    private static Pair<Integer, Integer> findGlobalBestMatchByTime(List<TrackCompareInfo> track1, int startIdx1, int endIndex1,
                                                             List<TrackCompareInfo> track2, int startIdx2, int endIndex2,
                                                             long maxTimeDiff) {
        if (startIdx1 >= endIndex1 || startIdx2 >= endIndex2)
            return Pair.of(-1, -1);

        int bestIdx1 = -1;
        int bestIdx2 = -1;
        long minDiff = Long.MAX_VALUE;
        int startJ = startIdx2;
        for (int i = startIdx1; i < endIndex1; i++) {
            Triple<Integer, Long, Integer> matchInfo = findBestMatchByTime(track1.get(i).getRecordAt(), track2, startJ, endIndex2);
            int j = matchInfo.getLeft();
            long diff = matchInfo.getMiddle();
            startJ = matchInfo.getRight();
            if (diff < minDiff) {
                minDiff = diff;
                bestIdx1 = i;
                bestIdx2 = j;
            }
        }
        if (minDiff <= maxTimeDiff)
            return Pair.of(bestIdx1, bestIdx2);
        else
            return Pair.of(-1, -1);
    }

    /**
     * 从轨迹track中找到最接近 compTime 的记录
     *
     * @return 下标，时间差，轨迹中比compTime小的最后一条记录的下标
     */
    private static Triple<Integer, Long, Integer> findBestMatchByTime(Long compTime, List<TrackCompareInfo> track, int startIdx, int endIndex) {
        long minDiff = Long.MAX_VALUE;
        int nextJ = startIdx;
        int bestJ = -1;
        for (int j = startIdx; j < endIndex; j++) {
            long diff = Math.abs(compTime - track.get(j).getRecordAt());
            if (diff < minDiff) {
                minDiff = diff;
                bestJ = j;
            }
            if (track.get(j).getRecordAt() < compTime)
                nextJ = j;
            if (track.get(j).getRecordAt() > compTime)
                return Triple.of(bestJ, minDiff, nextJ);
        }
        return Triple.of(bestJ, minDiff, nextJ);
    }
}
