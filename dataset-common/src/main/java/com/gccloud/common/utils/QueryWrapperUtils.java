package com.gccloud.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.gccloud.common.dto.SearchDTO;
import com.gccloud.common.dto.SortField;
import com.gccloud.common.utils.TableUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * EntityWrapper的封装，用于查询使用
 */
public class QueryWrapperUtils {

    /**
     * 模糊查询
     *
     * @param queryWrapper
     * @param searchKey    查询的关键字，多关键字之间使用空格隔开
     * @param fieldNames
     * @return
     */
    public static <T> LambdaQueryWrapper<T> wrapperLike(LambdaQueryWrapper<T> queryWrapper, String searchKey, SFunction<T, ?>... fieldNames) {
        if (fieldNames == null || fieldNames.length == 0 || StringUtils.isBlank(searchKey)) {
            return queryWrapper;
        }
        /**
         * 多个条件之间使用空格隔开
         */
        String[] searchKeyArr = searchKey.split(" ");
        for (String key : searchKeyArr) {
            if (StringUtils.isBlank(key)) {
                continue;
            }
            queryWrapper.and(wrapper -> {
                for (int i = 0; i < fieldNames.length; i++) {
                    if (StringUtils.isBlank(key)) {
                        continue;
                    }
                    wrapper.or().like(fieldNames[i], key);
                }
            });
        }
        return queryWrapper;
    }

    /**
     * 模糊查询封装
     *
     * @param ew         必须传入
     * @param searchKey  多条件之间使用空格隔开
     * @param columNames 实体对应的属性
     * @return
     */
    public static <T> QueryWrapper wrapperLike(QueryWrapper<T> ew, String searchKey, String... columNames) {
        if (columNames == null || columNames.length == 0) {
            return ew;
        }
        /**
         * 按照属性模糊查询
         */
        if (StringUtils.isNotBlank(searchKey)) {
            /**
             * 多个条件之间使用空格隔开
             */
            String[] searchKeyArr = searchKey.split(" ");
            for (String key : searchKeyArr) {
                if (StringUtils.isBlank(key)) {
                    continue;
                }
                ew.and(wrapper -> {
                    for (int i = 0; i < columNames.length; i++) {
                        String column = columNames[i];
                        wrapper.or().like(StringUtils.isNotBlank(key), column, key);
                    }
                });
            }
        }
        return ew;
    }
}
