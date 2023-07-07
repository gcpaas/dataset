package com.gccloud.dataset.controller;

import com.gccloud.common.vo.PageVO;
import com.gccloud.common.vo.R;
import com.gccloud.dataset.dto.DatasetDTO;
import com.gccloud.dataset.dto.DatasetSearchDTO;
import com.gccloud.dataset.dto.ExecuteDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.entity.LabelEntity;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.service.IDatasetLabelService;
import com.gccloud.dataset.service.factory.DataSetServiceFactory;
import com.gccloud.dataset.service.impl.dataset.BaseDatasetServiceImpl;
import com.gccloud.dataset.service.impl.datasource.BaseDatasourceServiceImpl;
import com.gccloud.dataset.utils.DBUtils;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DatasetInfoVO;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:47
 */
@Api(tags = "数据集")
@RestController
@RequestMapping("/dataset")
public class DatasetController {

    @Resource
    private BaseDatasetServiceImpl baseDatasetService;

    @Resource
    private DataSetServiceFactory dataSetServiceFactory;

    @Resource
    private BaseDatasourceServiceImpl datasourceService;

    @Resource
    private IDatasetLabelService datasetLabelService;


    @ApiOperation("分页列表")
    @GetMapping("/page")
    public R<PageVO<DatasetEntity>> getPage(DatasetSearchDTO searchDTO) {
        List<String> labelIds = searchDTO.getLabelIds();
        List<String> datasetIdList = this.filterDatasetByIdList(labelIds);
        searchDTO.setDatasetIds(datasetIdList);
        PageVO<DatasetEntity> page = baseDatasetService.getPage(searchDTO);
        return R.success(page);
    }

    @ApiOperation("列表")
    @GetMapping("/list")
    public R<List<DatasetEntity>> getList(DatasetSearchDTO searchDTO) {
        List<String> labelIds = searchDTO.getLabelIds();
        List<String> datasetIdList = this.filterDatasetByIdList(labelIds);
        searchDTO.setDatasetIds(datasetIdList);
        List<DatasetEntity> list = baseDatasetService.getList(searchDTO);
        return R.success(list);
    }

    /**
     * 根据标签id列表过滤数据集
     * @param labelIds
     * @return
     */
    private List<String> filterDatasetByIdList(List<String> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return null;
        }
        return datasetLabelService.getDatasetIdsByLabelIds(labelIds);
    }


    @ApiOperation("新增")
    @PostMapping("/add")
    public R<String> add(@RequestBody DatasetDTO datasetDTO) {
        IBaseDataSetService dataSetService = dataSetServiceFactory.build(datasetDTO.getDatasetType());
        String id = dataSetService.add(datasetDTO);
        // 保存与标签的关联关系
        List<String> labelIds = datasetDTO.getLabelIds();
        if (labelIds == null || labelIds.isEmpty()) {
            return R.success(id);
        }
        datasetLabelService.addByDatasetId(id, labelIds);
        return R.success(id);
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public R<Void> update(@RequestBody DatasetDTO datasetDTO) {
        IBaseDataSetService dataSetService = dataSetServiceFactory.build(datasetDTO.getDatasetType());
        dataSetService.update(datasetDTO);
        // 更新与标签的关联关系
        List<String> labelIds = datasetDTO.getLabelIds();
        datasetLabelService.deleteByDatasetId(datasetDTO.getId());
        if (labelIds == null || labelIds.isEmpty()) {
            return R.success();
        }
        datasetLabelService.addByDatasetId(datasetDTO.getId(), labelIds);
        return R.success();
    }

    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    public R<Void> delete(@PathVariable("id") String id) {
        IBaseDataSetService dataSetService = dataSetServiceFactory.buildById(id);
        dataSetService.delete(id);
        datasetLabelService.deleteByDatasetId(id);
        return R.success();
    }

    @ApiOperation("详情")
    @GetMapping("/info/{id}")
    public R<DatasetEntity> info(@PathVariable("id") String id) {
        IBaseDataSetService dataSetService = dataSetServiceFactory.buildById(id);
        DatasetEntity datasetEntity = dataSetService.getById(id);
        return R.success(datasetEntity);
    }

    @ApiOperation("数据集详情")
    @GetMapping("/datasetInfo/{id}")
    public R<DatasetInfoVO> getDatasetInfo(@PathVariable("id") String id) {
        IBaseDataSetService dataSetService = dataSetServiceFactory.buildById(id);
        DatasetInfoVO infoVO = dataSetService.getInfoById(id);
        List<LabelEntity> labels = datasetLabelService.getLabelByDatasetId(id);
        infoVO.setLabelList(labels);
        return R.success(infoVO);
    }

    @ApiOperation("数据集名称重复判断")
    @PostMapping("/checkRepeat")
    public R<Boolean> checkRepeat(@RequestBody DatasetEntity datasetEntity) {
        boolean nameRepeat = baseDatasetService.checkNameRepeat(datasetEntity.getId(), datasetEntity.getName(), datasetEntity.getModuleCode());
        return R.success(nameRepeat);
    }

    @ApiOperation("数据集执行测试")
    @PostMapping("/execute/test")
    public R<Object> execute(@RequestBody TestExecuteDTO executeDTO) {
        if (StringUtils.isBlank(executeDTO.getDataSetType())) {
            return R.error("数据集类型不能为空");
        }
        // 获取对应的数据集服务
        IBaseDataSetService dataSetService = dataSetServiceFactory.build(executeDTO.getDataSetType());
        DataVO execute = dataSetService.execute(executeDTO);
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", execute.getData());
        result.put("structure", execute.getStructure());
        if (StringUtils.isNotBlank(executeDTO.getDataSourceId())) {
            DatasourceEntity datasource = datasourceService.getInfoById(executeDTO.getDataSourceId());
            List<String> tableNameList = DBUtils.getTableNames(DBUtils.updateParamsConfig(executeDTO.getScript(), executeDTO.getParams()), datasource.getSourceType());
            result.put("tableNameList", tableNameList);
        }
        return R.success(result);
    }


    @ApiOperation("数据集执行")
    @PostMapping("/execute")
    public R<Object> execute(@RequestBody ExecuteDTO executeDTO) {
        if (StringUtils.isBlank(executeDTO.getDataSetType()) && StringUtils.isBlank(executeDTO.getDataSetId())) {
            return R.error("数据集id不能为空");
        }
        // 获取对应的数据集服务
        IBaseDataSetService dataSetService;
        if (StringUtils.isBlank(executeDTO.getDataSetType())) {
            dataSetService = dataSetServiceFactory.buildById(executeDTO.getDataSetId());
        } else {
            dataSetService = dataSetServiceFactory.build(executeDTO.getDataSetType());
        }
        boolean executionNeeded = dataSetService.checkBackendExecutionNeeded(executeDTO.getDataSetId());
        Object data;
        if (executeDTO.getCurrent() != null && executeDTO.getSize() != null) {
            data = dataSetService.execute(executeDTO.getDataSetId(), executeDTO.getParams(), executeDTO.getCurrent(), executeDTO.getSize());
        } else {
            data = dataSetService.execute(executeDTO.getDataSetId(), executeDTO.getParams());
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", data);
        // 是否需要由前端执行
        result.put("executionByFrontend", !executionNeeded);
        return R.success(result);
    }








}
