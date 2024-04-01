/*
 * Copyright 2023 http://gcpaas.gccloud.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gccloud.dataset.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gccloud.dataset.entity.DatasetEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhang.zeJun
 * @date 2022-11-14-11:41
 */
@Mapper
public interface DatasetDao extends BaseMapper<DatasetEntity> {
    

}