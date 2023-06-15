package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.utils.GroovyUtils;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.config.GroovyDataSetConfig;
import com.gccloud.dataset.params.ParamsClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.vo.DataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.SCRIPT)
public class GroovyDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Resource
    private ParamsClient paramsClient;


    @Override
    public Object execute(String id, List<DatasetParamDTO> params) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        final List<DatasetParamDTO> finalParams = params;
        return DATASET_CACHE.get(id, key -> {
            DatasetEntity datasetEntity = this.getById(id);
            if (datasetEntity == null) {
                throw new GlobalException("数据集不存在");
            }
            GroovyDataSetConfig config = (GroovyDataSetConfig) datasetEntity.getConfig();
            String script = config.getScript();
            // 参数预处理
            List<DatasetParamDTO> paramList = paramsClient.handleParams(finalParams);
            Map<String, Object> paramMap = this.buildParams(paramList, script);
            return GroovyUtils.run(script, paramMap);
        });

    }


    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        String script = executeDTO.getScript();
        if (StringUtils.isBlank(script)) {
            throw new GlobalException("脚本不能为空");
        }
        List<DatasetParamDTO> params = executeDTO.getParams();
        // 参数预处理
        params = paramsClient.handleParams(params);
        Map<String, Object> paramMap = this.buildParams(params, script);
        DataVO dataVO = new DataVO();
        dataVO.setData(GroovyUtils.run(script, paramMap));
        return dataVO;
    }


    /**
     * 构建参数，并且编译脚本
     * @param params
     * @param script
     * @return
     */
    private Map<String, Object> buildParams(List<DatasetParamDTO> params, String script) {
        Map<String, Object> paramMap = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach(p -> paramMap.put(p.getName(), p.getValue()));
        }
        Class clazz = GroovyUtils.buildClass(script);
        if (clazz == null) {
            throw new GlobalException("脚本编译异常");
        }
        return paramMap;
    }
}
