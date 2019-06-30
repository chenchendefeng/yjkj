package com.jiayi.platform.common.util;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.ArrayUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

/**
 * @author : weichengke
 * @date : 2019-04-13 10:37
 */
public class BeanUtils extends BeanUtilsBean2 {

    private static ThreadLocal<BeanUtils> threadLocal = new ThreadLocal<>();

    /**
     * Gets the instance which provides the functionality for {@link org.apache.commons.beanutils.BeanUtils}.
     * This is a pseudo-singleton - an single instance is provided per (thread) context classloader.
     * This mechanism provides isolation for web apps deployed in the same container.
     *
     * @return The (pseudo-singleton) BeanUtils bean instance
     */
    public static BeanUtils getInstance() {
        BeanUtils beanUtils = threadLocal.get();
        if(beanUtils == null){
            beanUtils = new BeanUtils();
            DateConverter convert = new DateConverter();
            // 支持时间和
            convert.setPatterns(ArrayUtils.toArray(
                    MyDateUtil.yyyy_MM_dd,
                    MyDateUtil.yyyy_MM_dd_HH_mm_ss,
                    MyDateUtil.HH_mm_ss,
                    MyDateUtil.yyyyMMdd,
                    MyDateUtil.yyyyMMddHHmmss,
                    MyDateUtil.MM_dd_yyyy,
                    MyDateUtil.MM_dd_yyyy_HH_mm_ss));
            beanUtils.getConvertUtils().register(convert, Date.class);
        }
        return beanUtils;
    }

    public void copyPropertiesIgnoreNull(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
        if (dest == null) {
            throw new IllegalArgumentException
                    ("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }

        if (orig instanceof DynaBean) {
            final DynaProperty[] origDescriptors =
                    ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (DynaProperty origDescriptor : origDescriptors) {
                final String name = origDescriptor.getName();
                if (getPropertyUtils().isReadable(orig, name) &&
                        getPropertyUtils().isWriteable(dest, name)) {
                    final Object value = ((DynaBean) orig).get(name);
                    if (value != null)
                        copyProperty(dest, name, value);
                }
            }
        } else if (orig instanceof Map) {
            @SuppressWarnings("unchecked") final Map<String, Object> propMap = (Map<String, Object>) orig;
            for (final Map.Entry<String, Object> entry : propMap.entrySet()) {
                final String name = entry.getKey();
                if (getPropertyUtils().isWriteable(dest, name)) {
                    if (entry.getValue() != null)
                        copyProperty(dest, name, entry.getValue());
                }
            }
        } else /* if (orig is a standard JavaBean) */ {
            final PropertyDescriptor[] origDescriptors =
                    getPropertyUtils().getPropertyDescriptors(orig);
            for (PropertyDescriptor origDescriptor : origDescriptors) {
                final String name = origDescriptor.getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (getPropertyUtils().isReadable(orig, name) &&
                        getPropertyUtils().isWriteable(dest, name)) {
                    try {
                        final Object value = getPropertyUtils().getSimpleProperty(orig, name);
                        if (value != null)
                            copyProperty(dest, name, value);
                    } catch (final NoSuchMethodException e) {
                        // Should not happen
                    }
                }
            }
        }
    }
}
