package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.utils.JSON;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.entity.config.CustomDataSetConfig;
import com.gccloud.dataset.extend.dataset.DatasetExtendClient;
import com.gccloud.dataset.params.ParamsClient;
import com.gccloud.dataset.permission.DatasetPermissionClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.service.factory.DatasourceServiceFactory;
import com.gccloud.dataset.service.impl.datasource.BaseDatasourceServiceImpl;
import com.gccloud.dataset.utils.DBUtils;
import com.gccloud.dataset.utils.MybatisParameterUtils;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DatasetInfoVO;
import com.gccloud.dataset.vo.FieldInfoVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.CUSTOM)
public class CustomDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Resource
    private DatasourceServiceFactory datasourceServiceFactory;

    @Resource
    private BaseDatasourceServiceImpl datasourceService;

    @Resource
    private ParamsClient paramsClient;

    @Resource
    private DatasetPermissionClient datasetPermissionClient;

    @Resource
    private DatasetExtendClient datasetExtendClient;

    @Resource
    private MybatisParameterUtils parameterUtils;

    @Override
    public String add(DatasetEntity entity) {
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
        long startTime = System.currentTimeMillis();
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity entity = this.getByIdFromCache(id);
        if (entity == null) {
            throw new GlobalException("数据集不存在");
        }
        CustomDataSetConfig config = (CustomDataSetConfig) entity.getConfig();
        String sql = config.getSqlProcess();
        // 脚本预处理
        sql = paramsClient.handleScript(entity.getDatasetType(), sql);
        // 参数预处理
        params = paramsClient.handleParams(params);
        // 参数替换
        if (DatasetConstant.SyntaxType.MYBATIS.equals(config.getSyntaxType())) {
            // 使用mybatis语法规则进行sql构造
            sql = parameterUtils.updateParamsConfig(sql, params);
        } else {
            sql = DBUtils.updateParamsConfig(sql, params);
        }
        String dataSourceId = entity.getSourceId();
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        log.info("执行【{}】数据集（类型：【自助】，ID:【{}】）， 参数：【{}】， URL：【{}】， 执行SQL：【{}】", entity.getName(), entity.getId(), JSON.toJSONString(params), datasource.getUrl(), sql);
        DataVO dataVO = buildService.executeSqlPage(datasource, sql, current, size);
        PageVO data = (PageVO) dataVO.getData();
        List list = data.getList();
        // 自定义数据处理
        list = datasetExtendClient.handleData(list, entity);
        data.setList(list);
        long endTime = System.currentTimeMillis();
        log.info("执行【{}】数据集（类型：【自助】，ID:【{}】）结束，耗时：【{}】毫秒", entity.getName(), entity.getId(), endTime - startTime);
        return data;
    }


    @Override
    public Object execute(String id, List<DatasetParamDTO> params) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        final List<DatasetParamDTO> finalParams = params;
        DatasetEntity entity = this.getByIdFromCache(id);
        if (entity == null) {
            throw new GlobalException("数据集不存在");
        }
        if (DatasetConstant.DatasetCache.OPEN.equals(entity.getCache())) {
            CompletableFuture<Object> future = DATASET_CACHE.get(id, key -> getData(finalParams, entity));
            try {
                return future.get();
            } catch (Exception e) {
                log.error("数据集缓存异常：{}", e.getMessage());
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return getData(finalParams, entity);
    }

    /**
     * 获取数据
     *
     * @param finalParams
     * @param entity
     * @return
     */
    private Object getData(List<DatasetParamDTO> finalParams, DatasetEntity entity) {
        long startTime = System.currentTimeMillis();
        CustomDataSetConfig config = (CustomDataSetConfig) entity.getConfig();
        String sql = config.getSqlProcess();
        String dataSourceId = entity.getSourceId();
        sql = paramsClient.handleScript(entity.getDatasetType(), sql);
        List<DatasetParamDTO> paramList = paramsClient.handleParams(finalParams);
        // 参数替换
        if (DatasetConstant.SyntaxType.MYBATIS.equals(config.getSyntaxType())) {
            // 使用mybatis语法规则进行sql构造
            sql = parameterUtils.updateParamsConfig(sql, paramList);
        } else {
            sql = DBUtils.updateParamsConfig(sql, paramList);
        }
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        log.info("执行【{}】数据集（类型：【自助】，ID:【{}】）， 参数：【{}】， URL：【{}】， 执行SQL：【{}】", entity.getName(), entity.getId(), JSON.toJSONString(finalParams), datasource.getUrl(), sql);
        DataVO dataVO = buildService.executeSql(datasource, sql);
        List list = (List) dataVO.getData();
        // 自定义数据处理
        list = datasetExtendClient.handleData(list, entity);
        long endTime = System.currentTimeMillis();
        log.info("执行【{}】数据集（类型：【自助】，ID:【{}】）结束，耗时：【{}】毫秒", entity.getName(), entity.getId(), endTime - startTime);
        return list;
    }

    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        long startTime = System.currentTimeMillis();
        List<DatasetParamDTO> params = executeDTO.getParams();
        // 参数预处理
        params = paramsClient.handleParams(params);
        String sql = executeDTO.getScript();
        sql = paramsClient.handleScript(executeDTO.getDataSetType(), sql);
        // 参数替换
        if (DatasetConstant.SyntaxType.MYBATIS.equals(executeDTO.getSyntaxType())) {
            // 使用mybatis语法规则进行sql构造
            sql = parameterUtils.updateParamsConfig(sql, params);
        } else {
            sql = DBUtils.updateParamsConfig(sql, params);
        }
        DatasourceEntity datasource = datasourceService.getInfoById(executeDTO.getDataSourceId());
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        log.info("测试数据集（类型：【自助】）， 参数：【{}】， URL：【{}】， 执行SQL：【{}】", JSON.toJSONString(params), datasource.getUrl(), sql);
        DataVO dataVO;
        Integer current = executeDTO.getCurrent();
        Integer size = executeDTO.getSize();
        if (size != null && current != null) {
            dataVO = buildService.executeSqlPage(datasource, sql, current, size);
        } else {
            dataVO = buildService.executeSql(datasource, sql);
        }
        long endTime = System.currentTimeMillis();
        log.info("测试数据集（类型：【自助】）结束，耗时：【{}】毫秒", endTime - startTime);
        executeDTO.setScript(sql);
        List<String> tableNames = DBUtils.getTableNames(sql, datasource.getSourceType());
        Map<String, String> columnAlias = DBUtils.getColumnAlias(sql, datasource.getSourceType());
        List<FieldInfoVO> tableColumnList = Lists.newArrayList();
        for (String tableName : tableNames) {
            List<FieldInfoVO> list = null;
            try {
                list = buildService.getTableColumnList(datasource, tableName);
            } catch (Exception e) {
                log.error("获取表【{}】字段信息异常", tableName);
                log.error(ExceptionUtils.getStackTrace(e));
            }
            if (CollectionUtils.isNotEmpty(list)) {
                tableColumnList.addAll(list);
            }
        }
        // 原始字段名和字段描述的映射，这里如果有字段名重复，也不做特殊处理，直接覆盖
        Map<String, String> columnDesc = new HashMap<>();
        for (FieldInfoVO fieldInfoVO : tableColumnList) {
            if (columnDesc.containsKey(fieldInfoVO.getColumnName()) && StringUtils.isNotBlank(columnDesc.get(fieldInfoVO.getColumnName()))) {
                continue;
            }
            columnDesc.put(fieldInfoVO.getColumnName(), fieldInfoVO.getColumnComment());
        }
        List<Map<String, Object>> structure = dataVO.getStructure();
        for (Map<String, Object> fieldInfo : structure) {
            String fieldName = fieldInfo.get(DatasetInfoVO.FIELD_NAME).toString();
            if (columnAlias.containsKey(fieldName)) {
                fieldName = columnAlias.get(fieldName);
            }
            if (columnDesc.containsKey(fieldName)) {
                fieldInfo.put(DatasetInfoVO.FIELD_DESC, columnDesc.get(fieldName));
            }
        }
        return dataVO;
    }

}
