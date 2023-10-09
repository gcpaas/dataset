package com.gccloud.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * 在JSON反序列化时，将空字符串转换为null
 * 主要用于id字段，由于mybatis-plus在3.4.1版本更新了主键自增逻辑，导致空字符串""也会被当做主键，从而报错
 * @author hongyang
 * @version 1.0
 * @date 2023/10/8 10:53
 */
public class EmptyAsNullDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.readValueAsTree();
        if (node.asText().isEmpty()) {
            return null;
        }
        return node.asText();
    }

}