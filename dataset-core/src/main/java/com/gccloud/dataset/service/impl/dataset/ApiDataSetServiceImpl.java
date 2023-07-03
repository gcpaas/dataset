package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.utils.GroovyUtils;
import com.gccloud.common.utils.HttpUtils;
import com.gccloud.common.utils.JSON;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.config.ApiDataSetConfig;
import com.gccloud.dataset.params.ParamsClient;
import com.gccloud.dataset.permission.PermissionClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.vo.DataVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.API)
public class ApiDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Resource
    private ParamsClient paramsClient;

    @Resource
    private PermissionClient permissionClient;


    @Override
    public String add(DatasetEntity entity) {
        String id = IBaseDataSetService.super.add(entity);
        if (permissionClient.hasPermissionService()) {
            // 添加数据集权限
            permissionClient.addPermission(id);
        }
        return id;
    }

    @Override
    public void delete(String id) {
        IBaseDataSetService.super.delete(id);
        if (permissionClient.hasPermissionService()) {
            // 删除数据集权限
            permissionClient.deletePermission(id);
        }
    }

    @Override
    public Object execute(String id, List<DatasetParamDTO> paramList) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity entity = this.getById(id);
        if (entity == null) {
            throw new GlobalException("数据集不存在");
        }
        final List<DatasetParamDTO> finalParamList = Lists.newArrayList(paramList);
        if (DatasetConstant.DatasetCache.OPEN.equals(entity.getCache())) {
            return DATASET_CACHE.get(id, key -> getData(entity, finalParamList));
        }
        return getData(entity, finalParamList);
    }

    /**
     * 获取数据
     * @param entity
     * @param finalParamList
     * @return
     */
    private Object getData(DatasetEntity entity, List<DatasetParamDTO> finalParamList) {
        ApiDataSetConfig config = (ApiDataSetConfig) entity.getConfig();
        List<DatasetParamDTO> params = paramsClient.handleParams(finalParamList);
        config = this.handleParams(config, params);
        if (config.getRequestType().equals("frontend")) {
            return config;
        }
        return this.getBackendData(config);
    }


    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        String apiInfoJson = executeDTO.getScript();
        if (StringUtils.isBlank(apiInfoJson)) {
            throw new GlobalException("数据集测试数据不能为空");
        }
        ApiDataSetConfig config = JSON.parseObject(apiInfoJson, ApiDataSetConfig.class);
        List<DatasetParamDTO> paramList = executeDTO.getParams();
        paramList = paramsClient.handleParams(paramList);
        config = this.handleParams(config, paramList);
        DataVO dataVO = new DataVO();
        if (config.getRequestType().equals("frontend")) {
            dataVO.setData(config);
        }
        Object data = this.getBackendData(config);
        dataVO.setData(data);
        return dataVO;
    }

    /**
     * 由前端执行请求，这里只需替换参数信息
     * @param config
     * @param paramList
     * @return
     */
    private ApiDataSetConfig handleParams(ApiDataSetConfig config, List<DatasetParamDTO> paramList) {
        if (paramList == null || paramList.size() == 0) {
            return config;
        }
        // 处理header中的参数
        List<Map<String, Object>> headers = config.getHeaders();
        if (headers != null && headers.size() > 0) {
            for (Map<String, Object> header : headers) {
                String value = (String) header.get("value");
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                // 检查是否包含${}格式的参数
                if (!value.contains("${")) {
                    continue;
                }
                for (DatasetParamDTO param : paramList) {
                    if (value.contains(param.getName())) {
                        this.parameterReplace(param, value);
                        header.put("value", value);
                    }
                }
            }
        }
        // 处理params中的参数
        List<Map<String, Object>> params = config.getParams();
        if (params != null && params.size() > 0) {
            for (Map<String, Object> param : params) {
                String value = (String) param.get("value");
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                // 检查是否包含${}格式的参数
                if (!value.contains("${")) {
                    continue;
                }
                for (DatasetParamDTO paramDTO : paramList) {
                    if (value.contains(paramDTO.getName())) {
                        this.parameterReplace(paramDTO, value);
                        param.put("value", value);
                    }
                }
            }
        }
        String body = config.getBody();
        if (StringUtils.isNotBlank(body)) {
            for (DatasetParamDTO param : paramList) {
                if (body.contains(param.getName())) {
                    body = this.parameterReplace(param, body);
                }
            }
            config.setBody(body);
        }
        // 处理url中的参数
        String url = config.getUrl();
        if (StringUtils.isNotBlank(url)) {
            for (DatasetParamDTO param : paramList) {
                if (url.contains(param.getName())) {
                    url = this.parameterReplace(param, url);
                }
            }
            config.setUrl(url);
        }
        // 处理请求脚本中的参数
        String requestScript = config.getRequestScript();
        if (StringUtils.isNotBlank(requestScript)) {
            for (DatasetParamDTO param : paramList) {
                if (requestScript.contains(param.getName())) {
                    requestScript = this.parameterReplace(param, requestScript);
                }
            }
            config.setRequestScript(requestScript);
        }
        // 处理响应脚本中的参数
        String responseScript = config.getResponseScript();
        if (StringUtils.isNotBlank(responseScript)) {
            for (DatasetParamDTO param : paramList) {
                if (responseScript.contains(param.getName())) {
                    responseScript = this.parameterReplace(param, responseScript);
                }
            }
            config.setResponseScript(responseScript);
        }
        return config;

    }

    private Object getBackendData(ApiDataSetConfig config) {
        Map<String, String> headers = config.getHeaders() == null ? Maps.newHashMap() : config.getHeaders().stream().collect(Collectors.toMap(item -> (String) item.get("name"), item -> (String) item.get("value")));
        Map<String, Object> params = config.getParams() == null ? Maps.newHashMap() : config.getParams().stream().collect(Collectors.toMap(item -> (String) item.get("name"), item -> item.get("value")));
        String body = config.getBody();
        // 如果有请求前脚本，则执行请求前脚本
        if (StringUtils.isNotBlank(config.getRequestScript())) {
            Map<String, Object> requestScriptMap = Maps.newHashMap();
            // 取name和value
            requestScriptMap.put("headers", headers);
            requestScriptMap.put("params", params);
            requestScriptMap.put("body", config.getBody());
            Object run = GroovyUtils.run(config.getRequestScript(), requestScriptMap);
            body = run == null ? null : run.toString();
        }
        // 拼接url和参数
        StringBuilder url = new StringBuilder(config.getUrl());
        if (params.size() > 0) {
            if (url.indexOf("?") == -1) {
                url.append("?");
            } else {
                url.append("&");
            }
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            url = new StringBuilder(url.substring(0, url.length() - 1));
        }
        // 发送请求
        Response response = null;
        switch (config.getMethod().toUpperCase()) {
            case "GET":
                response = HttpUtils.get(url.toString(), headers);
                break;
            case "POST":
                response = HttpUtils.post(url.toString(),"", headers, body);
                break;
            default:
                throw new GlobalException("不支持的请求方式");
        }
        String responseBody = null;
        try {
            responseBody = response.body().string();
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("获取请求响应失败");
        }
        // 如果有响应后脚本，则执行响应后脚本
        if (StringUtils.isNotBlank(config.getResponseScript())) {
            Map<String, Object> responseScriptMap = Maps.newHashMap();
            // 取name和value
            responseScriptMap.put("responseString", responseBody);
            Object run = GroovyUtils.run(config.getResponseScript(), responseScriptMap);
            return run;
        }
        if (responseBody.startsWith("{")) {
            return JSON.parseObject(responseBody);
        }
        if (responseBody.startsWith("[")) {
            return JSON.parseArray(responseBody);
        }
        return responseBody;





    }


    private String parameterReplace(DatasetParamDTO param, String str) {
        str = str.replaceAll("\\$\\{" + param.getName() + "\\}", param.getValue());
        return str;
    }



}
