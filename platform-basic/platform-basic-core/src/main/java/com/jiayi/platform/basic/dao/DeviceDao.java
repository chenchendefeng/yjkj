package com.jiayi.platform.basic.dao;

import com.jiayi.platform.basic.dto.DeviceLocationDetailDto;
import com.jiayi.platform.basic.entity.Device;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author : weichengke
 * @date : 2019-03-01 10:20
 */
@Repository
public interface DeviceDao extends PagingAndSortingRepository<Device, Long> {

    @Query("select device from Device device " + "where device.latitude is not null and device.longitude is not null")
    List<Device> findAllValidDevices();

    @Query("select count(device.pkId) from Device device "
            + "where device.latitude is not null and device.longitude is not null")
    int countAllValidDevices();

    @Query("select device from Device device\n" +
            "left join Place place on device.placeId=place.id\n" +
            "left join Src src on device.src = src.code\n" +
            "left join City city on place.district = city.id\n" +
            "left join DeviceSubType type on type.id = device.type\n" +
            "where device.id is not null and src.id is not null and city.id is not null\n" +
            "and type.dataType like CONCAT('%',:dataType,'%') and place.city=:city")
    List<Device> findDevicesByTypeAndCity(@Param("dataType") String dataType, @Param("city") String city); // TODO 这里数据库表city设置成了字符串!

    // JPA有自带的根据主键获取，但是数据库里既有pkid，又有id，为了代码可读性，这里自己分别封装了两个id的获取方法，否则数据库的pkid和jpa的id容易弄错
    Device findOneByPkId(Long pkId);

    Device findOneById(Long id);

    List<Device> findByPkIdIn(Collection<Long> pkIds);

    List<Device> findByIdIn(Collection<Long> ids);

    @Query("select d.code from Device d where d.placeId=:placeId")
    List<String> findCodeByPlaceId(@Param("placeId") Long placeId);

    @Query("select d from Device d where d.src=:srcCode and d.code in :codes")
    List<Device> isCodeUsedInSrc(@Param("srcCode") String srcCode, @Param("codes") List<String> codes);

    @Query("select d from Device d where d.placeCode=:placeCode and d.code in :codes")
    List<Device> isCodeUsedInPlace(@Param("placeCode") String placeCode, @Param("codes") List<String> codes);

    @Query("select d from Device d where d.code in :codes")
    List<Device> isCodeUsed(@Param("codes") List<String> codes);

    @Modifying
    @Query("update Device d set d.src=:srcCode where d.placeId=:placeId")
    void updateSrcByPlaceId(@Param("srcCode") String srcCode, @Param("placeId") Long placeId);

    @Query("select device.placeId,count(1) from Device device group by device.placeId")
    List<Object[]> countDeviceByPlace();

    //数据源，设备编码，主类型，在线状态，子类型信息
    @Query(value = "select d.src as src,d.code as code,dt.name as typeName, \n" +
            "case when abs(unix_timestamp(now()) * 1000 - \n" +
            "case when dtss.heartbeat_time is null then 0 else dtss.heartbeat_time end) <=:onlineTime then 1 else 0 end as isOnline,\n" +
            "dst.id as subTypeId, d.id as deviceId\n" +
            "from t_device d \n" +
            "left join t_device_sub_type dst on dst.id = d.type \n" +
            "left join t_device_type dt on dt.type = dst.device_type \n" +
            "left join device_time_stream_statistic dtss on dtss.src=d.src and dtss.code=d.code \n" +
            "left join t_place p on p.id=d.place_id \n" +
            "left join t_src src on d.src = src.code\n" +
            "left join code_city city on p.district = city.id\n" +
            "where src.id is not null and city.id is not null " +
            "and p.city=:cityId", nativeQuery = true)//"where dt.name is not null and d.latitude is not null and d.longitude is not null  and src.id is not null and city.id is not null " +
    List<Object[]> findCollectTypeDevices(@Param("onlineTime") Long onlineTime, @Param("cityId") Integer cityId);

    @Query("select count(1) from Device device where device.placeId=:placeId")
    Long countDeviceByPlace(@Param("placeId") Long placeId);

    @Query("select device from Device device where device.latitude is not null and device.longitude is not null and device.type in(:types)")
    List<Device> findByTypeIn(@Param("types") List<Integer> types);

    @Query("select device from Device device where device.latitude is not null and device.longitude is not null and device.type=:type")
    List<Device> findByType(@Param("type") Integer type);

    @Query("select device from Device device left join DeviceSubType dst on device.type=dst.id\n" +
            "where device.latitude is not null and device.longitude is not null and dst.dataType like CONCAT('%',:collectType,'%')")
    List<Device> findByCollectType(@Param("collectType") String collectType);

    Device findOneBySrcAndCode(String src, String code);

    @Query("select d from Device d where concat(d.src, '|', d.code) in (:srcAndCodes)")
    List<Device> findBySrcAndCode(@Param("srcAndCodes") Set<String> srcAndCodes);

    @Query("select device from Device device " +
            "left join DeviceSubType subtype on device.type=subtype.id " +
            "left join DeviceType type on type.type=subtype.deviceType " +
            "where type.type=:type")
    List<Device> findByMainType(@Param("type") Integer type);


    @Query("select new com.jiayi.platform.basic.dto.DeviceLocationDetailDto(device.latitude, device.longitude, cast(device.type as long), \n"
            + "device.pkId, device.placeId, device.id, device.name) from Device device \n"
            + "where device.latitude is not null and device.longitude is not null")
    Set<DeviceLocationDetailDto> selectDeviceLocation();

    @Modifying
    @Query("update Device set longitude = :longitude, latitude = :latitude, updateAt = :updateAt where id = :id")
    void adjustDevice(@Param("id") Long id, @Param("longitude") Long longitude, @Param("latitude") Long latitude, @Param("updateAt") Date updateAt);

    @Modifying
    @Query("update Device set status = :status where pkId = :pkId")
    void updateDeviceStatusByPkId(@Param("pkId")Long pkId, @Param("status")Integer status);
}
