package com.memeki.reviewhome.postAddress.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.memeki.reviewhome.postAddress.dto.GetBrTitleInfoResponseDto;

import java.io.IOException;

// 객체가 만약 문자열이라면 빈 객체로 만들어주는 클래스
public class ItemsOrStringDeserializer extends JsonDeserializer<GetBrTitleInfoResponseDto.Item> {
    @Override
    public GetBrTitleInfoResponseDto.Item deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.isTextual()) {
            return new GetBrTitleInfoResponseDto.Item();
        } else {
            return ctxt.readTreeAsValue(node, GetBrTitleInfoResponseDto.Item.class);
        }
    }
}
