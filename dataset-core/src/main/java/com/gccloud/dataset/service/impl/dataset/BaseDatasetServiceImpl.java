package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.entity.SuperEntity;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.DatasetSearchDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.permission.DatasetPermissionClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.service.ICategoryService;
import com.gccloud.dataset.vo.DataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据集基础服务，仅提供数据集的基础操作（增、删、改、查），未实现具体的数据集执行逻辑
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 13:37
 */
@Slf4j
@Primary
@Service("baseDataSetService")
public class BaseDatasetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Resource
    private ICategoryService categoryService;

    @Resource
    private DatasetPermissionClient datasetPermissionClient;

    @Override
    public List<DatasetEntity> getList(DatasetSearchDTO searchDTO) {
        if (!datasetPermissionClient.hasPermissionService()) {
            return IBaseDataSetService.super.getList(searchDTO);
        }
        // 查询数据集id列表
        LambdaQueryWrapper<DatasetEntity> queryWrapper = getQueryWrapper(searchDTO);
        queryWrapper.select(SuperEntity::getId);
        List<DatasetEntity> datasetList = this.list(queryWrapper);
        List<String> datasetIdList = datasetList.stream().map(DatasetEntity::getId).collect(Collectors.toList());
        // 调用权限服务过滤数据集id列表
        List<String> filterIdList = datasetPermissionClient.filterByPermission(datasetIdList, searchDTO.getDatasetType());
        // 查询数据集列表
        LambdaQueryWrapper<DatasetEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SuperEntity::getId, filterIdList);
        return this.list(wrapper);
    }

    @Override
    public PageVO<DatasetEntity> getPage(DatasetSearchDTO searchDTO) {
        if (!datasetPermissionClient.hasPermissionService()) {
            return IBaseDataSetService.super.getPage(searchDTO);
        }
        // 查询数据集id列表
        LambdaQueryWrapper<DatasetEntity> queryWrapper = getQueryWrapper(searchDTO);
        queryWrapper.select(SuperEntity::getId);
        List<DatasetEntity> datasetList = this.list(queryWrapper);
        List<String> datasetIdList = datasetList.stream().map(DatasetEntity::getId).collect(Collectors.toList());
        // 检查分页参数
        int current = searchDTO.getCurrent();
        int size = searchDTO.getSize();
        int start = (current - 1) * size;
        int end = current * size;
        if (start > datasetIdList.size()) {
            return new PageVO<>();
        }
        List<String> filterIds = datasetPermissionClient.filterByPermission(datasetIdList, searchDTO.getDatasetType());
        if (filterIds == null || filterIds.isEmpty()) {
            return new PageVO<>();
        }
        if (start > filterIds.size()) {
            return new PageVO<>();
        }
        if (end > filterIds.size()) {
            end = filterIds.size();
        }
        List<String> pageIds = filterIds.subList(start, end);
        // 查询数据集列表
        LambdaQueryWrapper<DatasetEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SuperEntity::getId, pageIds);
        List<DatasetEntity> list = this.list(wrapper);
        PageVO<DatasetEntity> pageVO = new PageVO<>();
        pageVO.setCurrent(current);
        pageVO.setSize(size);
        pageVO.setTotalCount(filterIds.size());
        pageVO.setList(list);
        int totalPage = filterIds.size() / size;
        if (filterIds.size() % size != 0) {
            totalPage++;
        }
        pageVO.setTotalPage(totalPage);
        return pageVO;
    }

    @Override
    public String add(DatasetEntity entity) {
        String id = IBaseDataSetService.super.add(entity);
        if (datasetPermissionClient.hasPermissionService()) {
            // 添加数据集权限
            datasetPermissionClient.addPermission(id);
        }
        return id;
    }

    @Override
    public void delete(String id) {
        IBaseDataSetService.super.delete(id);
        if (datasetPermissionClient.hasPermissionService()) {
            // 删除数据集权限
            datasetPermissionClient.deletePermission(id);
        }
    }

    @Override
    public Object execute(String id, List<DatasetParamDTO> params) {
        log.error("请通过DataSetServiceFactory获取对应的数据集服务实现类来调用该方法");
        return null;
    }

    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        log.error("请通过DataSetServiceFactory获取对应的数据集服务实现类来调用该方法");
        return null;
    }



    /**
     * 组装查询条件
     * @param searchDTO
     * @return
     */
    @Override
    public LambdaQueryWrapper<DatasetEntity> getQueryWrapper(DatasetSearchDTO searchDTO) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getName()), DatasetEntity::getName, searchDTO.getName());
        if (StringUtils.isNotBlank(searchDTO.getTypeId())) {
            List<String> allChildrenId = categoryService.getAllChildrenId(searchDTO.getTypeId());
            allChildrenId.add(searchDTO.getTypeId());
            queryWrapper.in(DatasetEntity::getTypeId, allChildrenId);
        }
        queryWrapper.in(CollectionUtils.isNotEmpty(searchDTO.getDatasetType()), DatasetEntity::getDatasetType, searchDTO.getDatasetType());
        if (searchDTO.getDatasetIds() != null && searchDTO.getDatasetIds().size() > 0) {
            queryWrapper.in(DatasetEntity::getId, searchDTO.getDatasetIds());
        }
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getModuleCode()), DatasetEntity::getModuleCode, searchDTO.getModuleCode());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getSourceId()), DatasetEntity::getSourceId, searchDTO.getSourceId());
        queryWrapper.orderByDesc(DatasetEntity::getUpdateDate);
        return queryWrapper;
    }
}
