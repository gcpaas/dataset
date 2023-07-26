package com.gccloud.dataset.service;

import com.gccloud.common.service.ISuperService;
import com.gccloud.dataset.dto.CategorySearchDTO;
import com.gccloud.dataset.entity.CategoryEntity;
import com.gccloud.dataset.vo.CategoryVO;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 13:43
 */
public interface ICategoryService extends ISuperService<CategoryEntity> {


    /**
     * 获取分类树
     * @param searchDTO
     * @return
     */
    List<CategoryVO> getTree(CategorySearchDTO searchDTO);


    /**
     * 获取节点的所有子节点id，包括子节点的子节点的...
     * @param id
     * @return
     */
    List<String> getAllChildrenId(String id);

    /**
     * 新增
     * @param entity
     * @return
     */
    String add(CategoryEntity entity);

    /**
     * 修改
     * @param entity
     */
    void update(CategoryEntity entity);

    /**
     * 删除
     * @param id
     */
    void delete(String id);


    /**
     * 校验名称重复
     * @param entity
     * @return
     */
    boolean checkNameRepeat(CategoryEntity entity);


}
