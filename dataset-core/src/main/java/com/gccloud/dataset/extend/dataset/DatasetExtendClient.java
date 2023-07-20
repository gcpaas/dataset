package com.gccloud.dataset.extend.dataset;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据集扩展客户端，用于获取数据集扩展实现类，执行扩展方法
 * @author hongyang
 * @version 1.0
 * @date 2023/7/19 17:30
 */
@Component
public class DatasetExtendClient {

    @Autowired(required = false)
    private List<IDatasetExtendService> extendServiceList;


    /**
     * 数据集删除前校验
     * 该方法会获取所有数据集扩展实现类，按@Order注解的值从小到大排序，然后依次执行校验，并将校验结果拼接返回
     * @param datasetId
     * @return
     */
    public List<String> deleteCheck(String datasetId){
        if (extendServiceList == null || extendServiceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> reasons = Lists.newArrayList();
        // 获取实现类上的@Order注解的值，按值从小到大排序，即值越小，越先执行
        extendServiceList.sort((o1, o2) -> {
            int order1 = getOrderValue(o1.getClass());
            int order2 = getOrderValue(o2.getClass());
            return order1 - order2;
        });
        // 根据排序后的顺序执行校验
        for (IDatasetExtendService service : extendServiceList) {
            String checkResult = service.deleteCheck(datasetId);
            if (checkResult == null || "".equals(checkResult)) {
                continue;
            }
            reasons.add(checkResult);
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
