/*
 * Copyright 2023 http://gcpaas.gccloud.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gccloud.dataset.extend.datasource;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据源扩展客户端，用于获取数据源扩展实现类，执行扩展方法
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/7/19 17:30
 */
@Component
public class DatasourceExtendClient {

    @Autowired(required = false)
    private List<IDatasourceExtendService> extendServiceList;


    /**
     * 数据源删除前校验
     * 该方法会获取所有数据源扩展实现类，按@Order注解的值从小到大排序，然后依次执行校验
     * @param datasetId
     * @return
     */
    public Map<String, String> deleteCheck(String datasetId) {
        if (extendServiceList == null || extendServiceList.isEmpty()) {
            return Maps.newHashMap();
        }
        Map<String, String> reasons = Maps.newHashMap();
        // 获取实现类上的@Order注解的值，按值从小到大排序，即值越小，越先执行
        extendServiceList.sort((o1, o2) -> {
            int order1 = getOrderValue(o1.getClass());
            int order2 = getOrderValue(o2.getClass());
            return order1 - order2;
        });
        // 根据排序后的顺序执行校验
        for (IDatasourceExtendService service : extendServiceList) {
            String checkResult = service.deleteCheck(datasetId);
            if (checkResult == null || "".equals(checkResult)) {
                continue;
            }
            String serviceType = service.getServiceType();
            if (StringUtils.isBlank(serviceType)) {
                serviceType = "业务系统";
            }
            if (reasons.containsKey(serviceType)) {
                checkResult = reasons.get(serviceType) + "\n" + checkResult;
            }
            reasons.put(serviceType, checkResult);
        }
        return reasons;
    }




    private int getOrderValue(Class<?> clazz) {
        int order = Integer.MAX_VALUE;
        if (clazz.isAnnotationPresent(Order.class)) {
            order = clazz.getAnnotation(Order.class).value();
        }
        return order;
    }

}