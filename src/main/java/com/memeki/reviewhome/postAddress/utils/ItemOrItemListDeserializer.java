package com.memeki.reviewhome.postAddress.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.memeki.reviewhome.postAddress.dto.GetBrTitleInfoResponseDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemOrItemListDeserializer extends JsonDeserializer<List<GetBrTitleInfoResponseDto.ItemDto>> {

    @Override
    public List<GetBrTitleInfoResponseDto.ItemDto> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        List<GetBrTitleInfoResponseDto.ItemDto> items = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode itemNode : node) {
                items.add(ctxt.readTreeAsValue(itemNode, GetBrTitleInfoResponseDto.ItemDto.class));
            }
        } else {
            items.add(ctxt.readTreeAsValue(node, GetBrTitleInfoResponseDto.ItemDto.class));
        }

        return items;
    }
}
