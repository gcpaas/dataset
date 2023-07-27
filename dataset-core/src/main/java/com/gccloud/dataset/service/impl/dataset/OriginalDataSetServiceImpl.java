package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.entity.config.BaseDataSetConfig;
import com.gccloud.dataset.entity.config.OriginalDataSetConfig;
import com.gccloud.dataset.permission.DatasetPermissionClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.service.factory.DatasourceServiceFactory;
import com.gccloud.dataset.service.impl.datasource.BaseDatasourceServiceImpl;
import com.gccloud.dataset.vo.DataVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 原始数据集服务实现类
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.ORIGINAL)
public class OriginalDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Resource
    private DatasourceServiceFactory datasourceServiceFactory;

    @Resource
    private BaseDatasourceServiceImpl datasourceService;

    @Resource
    private DatasetPermissionClient datasetPermissionClient;


    @Override
    public String add(DatasetEntity entity) {
        OriginalDataSetConfig config = (OriginalDataSetConfig) entity.getConfig();
        entity.setCode(config.getTableName());
        String id = IBaseDataSetService.super.add(entity);
        if (datasetPermissionClient.hasPermissionService()) {
            // 添加数据集权限
            datasetPermissionClient.addPermission(id);
        }
        return id;
    }

    @Override
    public void delete(String id) {
        IBaseDataSetService.super.delete(id);
        if (datasetPermissionClient.hasPermissionService()) {
            // 删除数据集权限
            datasetPermissionClient.deletePermission(id);
        }
    }

    @Override
    public PageVO execute(String id, List<DatasetParamDTO> params, int current, int size) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity entity = this.getById(id);
        if (entity == null) {
            throw new GlobalException("数据集不存在");
        }
        OriginalDataSetConfig config = (OriginalDataSetConfig) entity.getConfig();
        String fieldInfo = config.getFieldInfo();
        if (StringUtils.isBlank(fieldInfo)) {
            fieldInfo = "*";
        }
        String dataSourceId = config.getSourceId();
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        String sourceType = datasource.getSourceType();
        fieldInfo = handleSpecialField(fieldInfo, sourceType);
        if (DatasetConstant.DataRepeat.NOT_REPEAT.equals(config.getRepeatStatus())) {
            fieldInfo = "DISTINCT " + fieldInfo;
        }
        String sql = "SELECT " + fieldInfo + " FROM " + config.getTableName();
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO = buildService.executeSqlPage(datasource, sql, current, size);
        return (PageVO) dataVO.getData();
    }

    @Override
    public Object execute(String id, List<DatasetParamDTO> params) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity entity = this.getById(id);
        if (entity == null) {
            throw new GlobalException("数据集不存在");
        }
        if (DatasetConstant.DatasetCache.OPEN.equals(entity.getCache())) {
            return DATASET_CACHE.get(id, key -> getData(entity));
        }
        return getData(entity);
    }

    /**
     * 获取数据
     * @param entity
     * @return
     */
    private Object getData(DatasetEntity entity) {
        OriginalDataSetConfig config = (OriginalDataSetConfig) entity.getConfig();
        String fieldInfo = config.getFieldInfo();
        if (StringUtils.isBlank(fieldInfo)) {
            fieldInfo = "*";
        }
        String dataSourceId = config.getSourceId();
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        String sourceType = datasource.getSourceType();
        fieldInfo = handleSpecialField(fieldInfo, sourceType);
        if (DatasetConstant.DataRepeat.NOT_REPEAT.equals(config.getRepeatStatus())) {
            fieldInfo = "DISTINCT " + fieldInfo;
        }
        String sql = "SELECT " + fieldInfo + " FROM " + config.getTableName();
        IBaseDatasourceService buildService = datasourceServiceFactory.build(sourceType);
        DataVO dataVO = buildService.executeSql(datasource, sql);
        return dataVO.getData();
    }

    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        String fieldInfo = executeDTO.getScript();
        if (StringUtils.isBlank(fieldInfo)) {
            fieldInfo = "*";
        }
        String dataSourceId = executeDTO.getDataSourceId();
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        String sourceType = datasource.getSourceType();
        fieldInfo = handleSpecialField(fieldInfo, sourceType);
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO;
        Integer current = executeDTO.getCurrent();
        Integer size = executeDTO.getSize();
        if (size != null && current != null) {
            dataVO = buildService.executeSqlPage(datasource, fieldInfo, current, size);
        } else {
            dataVO = buildService.executeSql(datasource, fieldInfo);
        }
        return dataVO;
    }

    /**
     * 处理特殊字段
     * @param fieldInfo
     * @param sourceType
     * @return
     */
    private String handleSpecialField(String fieldInfo, String sourceType){
        if ("*".equals(fieldInfo)) {
            return fieldInfo;
        }
        // 分割字段，TODO 这里可能存在一个问题，如果字段中有逗号，会被分割成多个字段
        List<String> fieldList = Lists.newArrayList(fieldInfo.split(","));
        StringBuilder fields = new StringBuilder();
        // 遍历处理字段
        for (String field : fieldList) {
            // 如果字段中包含除数字、字母、下划线以外的字符，需要特殊处理
            if (field.matches("^[a-zA-Z0-9_]+$")) {
                fields.append(field).append(",");
                continue;
            }
            // 如果已经是特殊处理的字段，直接添加到字段列表中
            if (field.startsWith("`") && field.endsWith("`")) {
                fields.append(field).append(",");
                continue;
            }
            if (field.startsWith("\"") && field.endsWith("\"")) {
                fields.append(field).append(",");
                continue;
            }
            switch (sourceType.toLowerCase()) {
                case DatasetConstant.DatasourceType.MYSQL:
                case DatasetConstant.DatasourceType.CLICKHOUSE:
                    field = field.replace(field, "`" + field + "`");
                    break;
                case DatasetConstant.DatasourceType.ORACLE:
                case DatasetConstant.DatasourceType.POSTGRESQL:
                    field = field.replace(field, "\"" + field + "\"");
                    break;
                default:
                    break;
            }
            fields.append(field).append(",");
        }
        return fields.substring(0, fields.length() - 1);
    }

    /**
     * 根据数据源id和表名获取数据集列表
     * @param tableName
     * @param sourceId
     * @return
     */
    public List<DatasetEntity> getListByTableName(String tableName, String sourceId) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatasetEntity::getDatasetType, DatasetConstant.DataSetType.ORIGINAL);
        queryWrapper.eq(DatasetEntity::getSourceId, sourceId);
        // 原始数据集的code就是表名
        queryWrapper.like(DatasetEntity::getCode, tableName);
        List<DatasetEntity> list = this.list(queryWrapper);
        // 遍历二次过滤
        list.removeIf(datasetEntity -> {
            BaseDataSetConfig config = datasetEntity.getConfig();
            if (!(config instanceof OriginalDataSetConfig)) {
                return true;
            }
            OriginalDataSetConfig originalConfig = (OriginalDataSetConfig) config;
            if (originalConfig.getSourceId().equals(sourceId) && originalConfig.getTableName().equals(tableName)) {
                return false;
            }
            return true;
        });
        return list;
    }
}
