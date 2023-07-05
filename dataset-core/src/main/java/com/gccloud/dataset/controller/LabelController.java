package com.gccloud.dataset.controller;

import com.gccloud.common.vo.PageVO;
import com.gccloud.common.vo.R;
import com.gccloud.dataset.dto.LabelDTO;
import com.gccloud.dataset.dto.LabelSearchDTO;
import com.gccloud.dataset.entity.LabelEntity;
import com.gccloud.dataset.service.IDatasetLabelService;
import com.gccloud.dataset.service.ILabelService;
import com.gccloud.dataset.vo.LabelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 14:23
 */
@Api(tags = "标签配置")
@RestController
@RequestMapping("/label")
public class LabelController {

    @Resource
    private ILabelService labelService;

    @Resource
    private IDatasetLabelService datasetLabelService;

    @ApiOperation("查询标签")
    @GetMapping("/list")
    public R<PageVO<LabelEntity>> getPage(@ApiParam(name = "查询", value = "传入查询条件", required = true) LabelSearchDTO searchDTO) {
        PageVO<LabelEntity> page = labelService.getPage(searchDTO);
        return R.success(page);
    }

    @ApiOperation("新增或修改标签")
    @PostMapping("/addOrUpdateLabel")
    public R<Void> addOrUpdateLabel(@RequestBody LabelDTO labelDTO) {
        if (StringUtils.isNotBlank(labelDTO.getId())) {
            labelService.update(labelDTO);
            return R.success();
        }
        labelService.add(labelDTO);
        return R.success();
    }

    @PostMapping("/checkRepeat")
    public R<Boolean> checkRepeat(@RequestBody LabelEntity labelEntity) {
        boolean repeat = labelService.checkRepeat(labelEntity);
        return R.success(repeat);
    }

    @ApiOperation("删除标签")
    @GetMapping("/removeLabel/{id}")
    public R<Void> removeLabel(@PathVariable String id) {
        labelService.delete(id);
        return R.success();
    }

    @GetMapping("/getLabelDetail/{id}")
    public R<LabelVO> getLabelDetail(@PathVariable String id) {
        LabelVO labelVO = labelService.getInfoById(id);
        return R.success(labelVO);
    }

    @ApiOperation("获取全部标签信息")
    @GetMapping("/getLabelList")
    public R<List<LabelEntity>> getLabelList() {
        List<LabelEntity> list = labelService.list();
        return R.success(list);
    }

    @ApiOperation("获取标签类型")
    @GetMapping("/getLabelType")
    public R<List<String>> getLabelType() {
        List<String> labelType = labelService.getLabelType();
        return R.success(labelType);
    }

    @ApiOperation("通过标签类型删除标签")
    @PostMapping("/removeLabelByType")
    public R<Void> removeLabelByType(@RequestBody LabelDTO labelVO) {
        labelService.deleteLabelByType(labelVO.getLabelType());
        return R.success();
    }

    @ApiOperation("标签类型的修改")
    @PostMapping("/updateLabelType")
    public R<Void> updateLabelType(@RequestBody LabelDTO labelVO) {
        labelService.updateLabelType(labelVO.getLabelType(), labelVO.getOldLabelType());
        return R.success();
    }

    @ApiOperation("通过数据集id获取标签")
    @GetMapping("/queryDataSetLabelList/{dataSetId}")
    public R<Object> queryDataSetLabelList(@PathVariable String dataSetId) {
        List<LabelEntity> labelList = datasetLabelService.getLabelByDatasetId(dataSetId);
        return R.success(labelList);
    }

    @ApiOperation("通过数据集id和标签id解除标签")
    @GetMapping("/removeDataSetLabel")
    public R<Void> removeDataSetLabel(String dataSetId, String labelId) {
        datasetLabelService.delete(dataSetId, labelId);
        return R.success();
    }

}
