package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.utils.JSON;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.config.JsonDataSetConfig;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.vo.DataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * json数据集服务实现类
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.JSON)
public class JsonDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Override
    public Object execute(String id, List<DatasetParamDTO> params) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity datasetEntity = this.getById(id);
        if (datasetEntity == null) {
            throw new GlobalException("数据集不存在");
        }
        JsonDataSetConfig config = (JsonDataSetConfig) datasetEntity.getConfig();
        String json = config.getJson();
        Object object = null;
        // json可能为数组，也可能为对象
        if (json.startsWith("[")) {
            object = JSON.parseArray(json);
        } else {
            object = JSON.parseObject(json);
        }
        if (object == null) {
            throw new GlobalException("json格式错误");
        }
        return object;
    }


    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        String json = executeDTO.getScript();
        if (StringUtils.isBlank(json)) {
            throw new GlobalException("json不能为空");
        }
        Object object = null;
        // json可能为数组，也可能为对象
        if (json.startsWith("[")) {
            object = JSON.parseArray(json);
        } else {
            object = JSON.parseObject(json);
        }
        if (object == null) {
            throw new GlobalException("json格式错误");
        }
        DataVO dataVO = new DataVO();
        dataVO.setData(object);
        return dataVO;
    }
}
