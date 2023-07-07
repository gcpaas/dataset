package com.gccloud.dataset.controller;

import com.gccloud.common.vo.PageVO;
import com.gccloud.common.vo.R;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dto.DatasetSearchDTO;
import com.gccloud.dataset.dto.DatasourceSearchDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.entity.config.OriginalDataSetConfig;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.service.factory.DatasourceServiceFactory;
import com.gccloud.dataset.service.impl.dataset.BaseDatasetServiceImpl;
import com.gccloud.dataset.service.impl.datasource.BaseDatasourceServiceImpl;
import com.gccloud.dataset.vo.FieldInfoVO;
import com.gccloud.dataset.vo.TableInfoVO;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 11:32
 */
@Api(tags = "数据源")
@RestController
@RequestMapping("/datasource")
public class DatasourceController {

    @Resource
    private BaseDatasourceServiceImpl baseDatasourceService;

    @Resource
    private DatasourceServiceFactory datasourceServiceFactory;

    @Resource
    private BaseDatasetServiceImpl datasetService;


    @ApiOperation("分页列表")
    @GetMapping("/page")
    public R<PageVO<DatasourceEntity>> getPage(DatasourceSearchDTO searchDTO) {
        PageVO<DatasourceEntity> page = baseDatasourceService.getPage(searchDTO);
        return R.success(page);
    }


    @ApiOperation("列表")
    @GetMapping("/list")
    public R<List<DatasourceEntity>> getList(DatasourceSearchDTO searchDTO) {
        List<DatasourceEntity> list = baseDatasourceService.getList(searchDTO);
        return R.success(list);
    }

    @ApiOperation("新增")
    @PostMapping("/add")
    public R<String> add(@RequestBody DatasourceEntity datasourceEntity) {
        String id = baseDatasourceService.add(datasourceEntity);
        return R.success(id);
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public R<Void> update(@RequestBody DatasourceEntity datasourceEntity) {
        baseDatasourceService.update(datasourceEntity);
        return R.success();
    }

    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    public R<Void> delete(@PathVariable String id) {
        baseDatasourceService.delete(id);
        return R.success();
    }

    @ApiOperation("测试连接")
    @PostMapping("/testConnect")
    public R<String> testConnect(@RequestBody DatasourceEntity datasourceEntity) {
        if (StringUtils.isBlank(datasourceEntity.getSourceType())) {
            return R.error("数据源类型不能为空");
        }
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasourceEntity.getSourceType());
        String info = datasourceService.sourceLinkTest(datasourceEntity);
        return R.success(info);
    }

    @ApiOperation("数据源名称重复判断")
    @PostMapping("/checkRepeat")
    public R<Boolean> checkRepeat(@RequestBody DatasourceEntity datasource) {
        Boolean flag = baseDatasourceService.checkNameRepeat(datasource.getId(), datasource.getSourceName(), datasource.getModuleCode());
        return R.success(flag);
    }

    @ApiOperation("查询数据源下的表")
    @GetMapping("/getTableList/{sourceId}")
    public R<List<TableInfoVO>> getTableList(@PathVariable String sourceId) {
        DatasourceEntity datasourceEntity = baseDatasourceService.getById(sourceId);
        if (datasourceEntity == null) {
            return R.error("数据源不存在");
        }
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasourceEntity.getSourceType());
        List<TableInfoVO> tableList = datasourceService.getTableList(datasourceEntity);
        DatasetSearchDTO searchDTO = new DatasetSearchDTO();
        searchDTO.setDatasetType(Lists.newArrayList(DatasetConstant.DataSetType.ORIGINAL));
        searchDTO.setSourceId(sourceId);
        List<DatasetEntity> originalList = datasetService.getList(searchDTO);
        List<String> tableNameList = originalList.stream().map((dataset) -> {
            OriginalDataSetConfig config = (OriginalDataSetConfig) dataset.getConfig();
            return config.getTableName();
        }).collect(Collectors.toList());
        tableList.forEach((table) -> {
            if (tableNameList.contains(table.getName())) {
                table.setStatus(1);
            }
        });
        return R.success(tableList);
    }


    @ApiOperation("查询数据源下的视图")
    @GetMapping("/getViewList/{sourceId}")
    public R<List<TableInfoVO>> getViewList(@PathVariable String sourceId) {
        DatasourceEntity datasourceEntity = baseDatasourceService.getById(sourceId);
        if (datasourceEntity == null) {
            return R.error("数据源不存在");
        }
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasourceEntity.getSourceType());
        List<TableInfoVO> viewList = datasourceService.getViewList(datasourceEntity);
        DatasetSearchDTO searchDTO = new DatasetSearchDTO();
        searchDTO.setDatasetType(Lists.newArrayList(DatasetConstant.DataSetType.ORIGINAL));
        searchDTO.setSourceId(sourceId);
        List<DatasetEntity> originalList = datasetService.getList(searchDTO);
        List<String> tableNameList = originalList.stream().map((dataset) -> {
            OriginalDataSetConfig config = (OriginalDataSetConfig) dataset.getConfig();
            return config.getTableName();
        }).collect(Collectors.toList());
        viewList.forEach((view) -> {
            if (tableNameList.contains(view.getName())) {
                view.setStatus(1);
            }
        });
        return R.success(viewList);
    }


    @ApiOperation("查询数据源下表的字段信息")
    @GetMapping("/getFieldList/table/{sourceId}/{tableName}")
    public R<List<FieldInfoVO>> getTableFieldList(@PathVariable String sourceId, @PathVariable String tableName) {
        DatasourceEntity datasourceEntity = baseDatasourceService.getById(sourceId);
        if (datasourceEntity == null) {
            return R.error("数据源不存在");
        }
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasourceEntity.getSourceType());
        List<FieldInfoVO> fieldList = datasourceService.getTableColumnList(datasourceEntity, tableName);
        return R.success(fieldList);
    }

    @ApiOperation("查询数据源下表的字段信息")
    @GetMapping("/getFieldList/view/{sourceId}/{tableName}")
    public R<List<FieldInfoVO>> getViewFieldList(@PathVariable String sourceId, @PathVariable String tableName) {
        DatasourceEntity datasourceEntity = baseDatasourceService.getById(sourceId);
        if (datasourceEntity == null) {
            return R.error("数据源不存在");
        }
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasourceEntity.getSourceType());
        List<FieldInfoVO> fieldList = datasourceService.getViewColumnList(datasourceEntity, tableName);
        return R.success(fieldList);
    }

}
