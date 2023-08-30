package com.gccloud.dataset.vo;

import lombok.Data;

/**
 * 库表信息扩展类，用于接收sqlserver库表信息
 * @author hongyang
 * @version 1.0
 * @date 2023/8/29 16:42
 */
@Data
public class TableInfoVOExtend extends TableInfoVO {

    /**
     * 表所属数据库名
     */
    private String tableCatalog;

    /**
     * 表所属模式名
     */
    private String tableSchema;

}
