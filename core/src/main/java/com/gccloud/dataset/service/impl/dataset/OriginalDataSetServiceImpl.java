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
import com.gccloud.dataset.entity.config.OriginalDataSetConfig;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.service.factory.DatasourceServiceFactory;
import com.gccloud.dataset.service.impl.datasource.BaseDatasourceServiceImpl;
import com.gccloud.dataset.vo.DataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    @Override
    public PageVO execute(String id, List<DatasetParamDTO> params, int current, int size) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity entity = this.getById(id);
        OriginalDataSetConfig config = (OriginalDataSetConfig) entity.getConfig();
        String fieldInfo = config.getFieldInfo();
        if (StringUtils.isBlank(fieldInfo)) {
            fieldInfo = "*";
        }
        if (DatasetConstant.DataRepeat.NOT_REPEAT.equals(config.getRepeatStatus())) {
            fieldInfo = "DISTINCT " + fieldInfo;
        }
        String sql = "SELECT " + fieldInfo + " FROM " + config.getTableName();
        String dataSourceId = config.getSourceId();
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
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
        OriginalDataSetConfig config = (OriginalDataSetConfig) entity.getConfig();
        String fieldInfo = config.getFieldInfo();
        if (StringUtils.isBlank(fieldInfo)) {
            fieldInfo = "*";
        }
        if (DatasetConstant.DataRepeat.NOT_REPEAT.equals(config.getRepeatStatus())) {
            fieldInfo = "DISTINCT " + fieldInfo;
        }
        String sql = "SELECT " + fieldInfo + " FROM " + config.getTableName();
        String dataSourceId = config.getSourceId();
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO = buildService.executeSql(datasource, sql);
        return dataVO.getData();
    }

    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        String sql = executeDTO.getScript();
        if (StringUtils.isBlank(sql)) {
            throw new GlobalException("sql不能为空");
        }
        DatasourceEntity datasource = datasourceService.getInfoById(executeDTO.getDataSourceId());
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO;
        Integer current = executeDTO.getCurrent();
        Integer size = executeDTO.getSize();
        if (size != null && current != null) {
            dataVO = buildService.executeSqlPage(datasource, sql, current, size);
        } else {
            dataVO = buildService.executeSql(datasource, sql);
        }
        return dataVO;
    }
}
