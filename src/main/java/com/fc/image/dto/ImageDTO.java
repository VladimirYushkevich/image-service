package com.fc.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    @NotNull
    private String granuleImages;
    @NotNull
    private ChannelMap channelMap;
}
