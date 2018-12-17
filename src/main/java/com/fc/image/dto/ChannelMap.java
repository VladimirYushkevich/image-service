package com.fc.image.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ChannelMap {
    VISIBLE("visible"), VEGETATION("vegetation"), WATER_VAPOR("waterVapor");

    @Getter
    @JsonValue
    private String value;
}
