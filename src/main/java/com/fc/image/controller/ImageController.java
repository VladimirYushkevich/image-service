package com.fc.image.controller;

import com.fc.image.dto.ImageDTO;
import com.fc.image.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping(value = "/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final FileService fileService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public byte[] image(@RequestBody @Valid ImageDTO dto) throws IOException {
        return IOUtils.toByteArray(Files.newInputStream(fileService.getFile(dto).toPath()));
    }

}
