package com.gccloud.dataset.extend.dataset;

/**
 * 数据集扩展接口，可通过实现该接口来自定义数据集的一些操作
 * @author hongyang
 * @version 1.0
 * @date 2023/7/19 17:26
 */
public interface IDatasetExtendService {

    /**
     * 数据集删除前校验
     * 请返回校验结果，为空（""或null）表示校验通过，否则为校验失败原因
     * 请不要抛出异常，否则可能会导致校验不完整
     * 返回的校验结果会在前端展示给用户
     * @param id 数据集id
     * @return 校验结果，为空（""或null）表示校验通过，否则为校验失败原因
     */
    String deleteCheck(String id);

}
