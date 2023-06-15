package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.entity.config.StoredProcedureDataSetConfig;
import com.gccloud.dataset.params.ParamsClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.service.factory.DatasourceServiceFactory;
import com.gccloud.dataset.service.impl.datasource.BaseDatasourceServiceImpl;
import com.gccloud.dataset.utils.DBUtils;
import com.gccloud.dataset.vo.DataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.STORED_PROCEDURE)
public class StoredProcedureDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Resource
    private ParamsClient paramsClient;

    @Resource
    private DatasourceServiceFactory datasourceServiceFactory;

    @Resource
    private BaseDatasourceServiceImpl datasourceService;


    @Override
    public PageVO execute(String id, List<DatasetParamDTO> params, int current, int size) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity dataset = this.getById(id);
        StoredProcedureDataSetConfig config = (StoredProcedureDataSetConfig) dataset.getConfig();
        // 存储过程
        String sqlProcess = config.getSqlProcess();
        // 参数预处理
        params = paramsClient.handleParams(params);
        // 参数替换
        sqlProcess = DBUtils.updateParamsConfig(sqlProcess, params);
        DatasourceEntity datasource = datasourceService.getInfoById(config.getSourceId());
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        // 执行存储过程
        DataVO dataVO = buildService.executeProcedure(datasource, sqlProcess, current, size);
        return (PageVO) dataVO.getData();
    }

    @Override
    public Object execute(String id, List<DatasetParamDTO> params) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity dataset = this.getById(id);
        StoredProcedureDataSetConfig config = (StoredProcedureDataSetConfig) dataset.getConfig();
        // 存储过程
        String sqlProcess = config.getSqlProcess();
        // 参数预处理
        params = paramsClient.handleParams(params);
        // 参数替换
        sqlProcess = DBUtils.updateParamsConfig(sqlProcess, params);
        DatasourceEntity datasource = datasourceService.getInfoById(config.getSourceId());
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        // 执行存储过程
        DataVO dataVO = buildService.executeProcedure(datasource, sqlProcess, null, null);
        return dataVO.getData();
    }

    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        String sqlProcess = executeDTO.getScript();
        if (StringUtils.isBlank(sqlProcess)) {
            throw new GlobalException("存储过程执行语句不能为空");
        }
        List<DatasetParamDTO> params = executeDTO.getParams();
        // 参数预处理
        params = paramsClient.handleParams(params);
        // 参数替换
        sqlProcess = DBUtils.updateParamsConfig(sqlProcess, params);
        DatasourceEntity datasource = datasourceService.getInfoById(executeDTO.getDataSourceId());
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO;
        Integer current = executeDTO.getCurrent();
        Integer size = executeDTO.getSize();
        if (current != null && size != null) {
            // 执行存储过程
            dataVO = buildService.executeProcedure(datasource, sqlProcess, current, size);
        } else {
            // 执行存储过程
            dataVO = buildService.executeProcedure(datasource, sqlProcess, null, null);
        }
        return dataVO;
    }
}
