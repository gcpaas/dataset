package com.gccloud.dataset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gccloud.common.service.ISuperService;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.dto.LabelDTO;
import com.gccloud.dataset.dto.LabelSearchDTO;
import com.gccloud.dataset.entity.LabelEntity;
import com.gccloud.dataset.vo.LabelVO;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 10:31
 */
public interface ILabelService extends ISuperService<LabelEntity> {

    /**
     * 分页查询标签
     * @param searchDTO
     * @return
     */
    PageVO<LabelEntity> getPage(LabelSearchDTO searchDTO);

    /**
     * 新增标签
     * @param labelDTO
     * @return
     */
    String add(LabelDTO labelDTO);


    /**
     * 更新标签
     * @param labelDTO
     */
    void update(LabelDTO labelDTO);

    /**
     * 删除标签
     * @param id
     */
    void delete(String id);

    /**
     * 根据id获取标签信息
     * @param id
     * @return
     */
    LabelVO getInfoById(String id);

    /**
     * 获取标签的类型列表
     * @return
     */
    List<String> getLabelType();

    /**
     * 校验标签名称重复
     * @param labelEntity
     * @return
     */
    boolean checkRepeat(LabelEntity labelEntity);

    /**
     * 根据类型删除标签
     * @param labelType
     */
    void deleteLabelByType(String labelType);

    /**
     * 更新标签类型
     * @param labelType
     * @param oldLabelType
     */
    void updateLabelType(String labelType, String oldLabelType);

}
