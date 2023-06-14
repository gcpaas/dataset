package com.gccloud.dataset.controller;

import com.gccloud.common.vo.R;
import com.gccloud.dataset.dto.CategorySearchDTO;
import com.gccloud.dataset.entity.CategoryEntity;
import com.gccloud.dataset.service.ICategoryService;
import com.gccloud.dataset.vo.CategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/7 15:17
 */
@Api(tags = "数据集")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private ICategoryService categoryService;

    @ApiOperation("依据类型查询对应的种类树")
    @GetMapping("/queryTreeList")
    public R<List<CategoryVO>> queryTreeList(CategorySearchDTO searchDTO) {
        List<CategoryVO> tree = categoryService.getTree(searchDTO);
        return R.success(tree);
    }

    @ApiOperation("新增")
    @PostMapping("/add")
    public R<String> add(@RequestBody CategoryEntity categoryEntity) {
        String id = categoryService.add(categoryEntity);
        return R.success(id);
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    public R<Void> update(@RequestBody CategoryEntity categoryEntity) {
        categoryService.update(categoryEntity);
        return R.success();
    }


    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    public R<Void> delete(@PathVariable String id) {
        categoryService.delete(id);
        return R.success();
    }

}
