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

package com.gccloud.dataset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据集的返回数据封装，包含数据、分页数据、结构
 * @author hongyang
 * @version 1.0
 * @date 2023/6/5 16:17
 */
@Data
public class DataVO {

    @ApiModelProperty(value = "数据集执行结果数据")
    private Object data;

    @ApiModelProperty(value = "数据集执行结果的数据结构，非必须")
    private List<Map<String, Object>> structure;

    public DataVO() {
    }

    public DataVO(Object data, List<Map<String, Object>> structure) {
        this.data = data;
        this.structure = structure;
    }

}