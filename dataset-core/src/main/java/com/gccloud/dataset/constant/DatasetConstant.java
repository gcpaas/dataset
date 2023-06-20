package com.gccloud.dataset.constant;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:39
 */
public interface DatasetConstant {


    interface DataSetType {
        /**
         * 自助数据集
         */
        String CUSTOM = "custom";
        /**
         * 原始数据集
         */
        String ORIGINAL = "original";
        /**
         * 脚本数据集
         */
        String SCRIPT = "script";
        /**
         * JSON数据集
         */
        String JSON = "json";
        /**
         * 存储过程数据集
         */
        String STORED_PROCEDURE = "storedProcedure";
        /**
         * JS数据集
         */
        String JS = "js";

        /**
         * API数据集
         */
        String API = "api";
    }

    interface DatasourceType {
        /**
         * mysql
         */
        String MYSQL = "mysql";

        /**
         * oracle
         */
        String ORACLE = "oracle";

        /**
         * postgresql
         */
        String POSTGRESQL = "postgresql";

        /**
         * hive
         */
        String HIVE = "hive";

        /**
         * clickhouse
         */
        String CLICKHOUSE = "clickhouse";

    }

    /**
     * 数据集缓存状态
     */
    interface DatasetCache {

        /**
         * 数据集开启缓存
         */
        Integer OPEN = 1;


        /**
         * 数据集关闭缓存
         */
        Integer CLOSE = 0;
    }

    /**
     * 数据是否去重
     */
    interface DataRepeat {
        /**
         * 不去重
         */
        Integer NOT_REPEAT = 1;
        /**
         * 默认 去重
         */
        Integer DEFAULT = 0;
    }

    /**
     * SQL中的参数类型
     */
    interface SqlParamsType {

        /**
         * 字符串参数，在替换sql中参数值时，会固定在值两侧加上单引号
         */
        String STRING = "String";

        /**
         * 日期参数
         */
        String DATE = "Date";

        /**
         * 整型
         */
        String INTEGER = "Integer";

        /**
         * 长整型
         */
        String LONG = "Long";

        /**
         * 浮点型
         */
        String DOUBLE = "Double";

    }

    /**
     * SQL中的参数状态
     */
    interface SqlParamsStatus {
        /**
         * 默认
         */
        Integer DEFAULT = 0;
        /**
         * 变量
         */
        Integer VARIABLE = 1;
    }

    /**
     * 扫描包路径
     */
    interface ScanPackage {

        /**
         * 基础包路径
         */
        String COMPONENT = "com.gccloud.dataset";

        /**
         * dao路径
         */
        String DAO = "com.gccloud.dataset.**.dao";

    }

}
