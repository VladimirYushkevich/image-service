package com.fc.image.service;

import com.fc.image.dto.ChannelMap;
import com.fc.image.dto.ImageDTO;
import com.fc.image.utils.ImageMagick;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.fc.image.utils.J2KToJpegConverter.convert;

@Component
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private String storeIn;
    private String storeOut;
    private String gcsImageBucket;
    private int bufferSizeInMb;
    private int resizeFactorPercentage;

    private final RestTemplate restTemplate;

    public File getFile(ImageDTO dto) {
        File outFile = null;
        try {
            final String granuleImagesPath = dto.getGranuleImages();
            final String fileName = dto.getChannelMap().getValue();

            final File outDirectory = createDirectoryIfNotExists(Paths.get(storeOut, granuleImagesPath).toString());
            outFile = Paths.get(outDirectory.getPath(), changeFileExtension(fileName, "jpeg")).toFile();

            if (outFile.exists()) {
                log.info("Converted file '{}' exists. Skip", outFile);
                return outFile;
            }

            final List<String> fileNames = resolveFileNames(dto.getChannelMap(),
                    Paths.get(granuleImagesPath).getParent().getFileName().toString());

            final List<CompletableFuture<Optional<InputStream>>> futures = fileNames.stream()
                    .map(fn -> CompletableFuture.supplyAsync(() -> process(granuleImagesPath, fn)))
                    .collect(Collectors.toList());

            final List<InputStream> inputStreams = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                    .thenApply(f -> futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList())
                    ).get().stream()
                    .map(Optional::get)
                    .collect(Collectors.toList());

            return convert(inputStreams, outFile.getPath());
        } catch (Exception e) {
            log.error("Can't process file, reason: ", e);
        }

        return outFile;
    }

    private Optional<InputStream> process(String path, String fileName) {
        final File inDirectory = createDirectoryIfNotExists(Paths.get(storeIn, path).toString());
        File inFile = Paths.get(inDirectory.getPath(), changeFileExtension(fileName, "tmp")).toFile();
        if (!inFile.exists()) {
            log.info("started downloading file '{}'", inFile);
            inFile = downloadFile(path + "/" + fileName);
            log.info("downloaded file '{}'", inFile);
        }

        final String inputFileName = inFile.getPath();
        final String outputFileName = changeFileExtension(inputFileName, "jp2");
        ImageMagick.magic(inputFileName, outputFileName, resizeFactorPercentage);
        inFile.delete();

        try {
            return Optional.of(Files.newInputStream(Paths.get(outputFileName)));
        } catch (Exception e) {
            log.error("can't ope input stream, reason:", e);
            return Optional.empty();
        }
    }

    private List<String> resolveFileNames(ChannelMap channelMap, String granuleImagesParentName) {
        List<String> fileNames = new ArrayList<>();
        switch (channelMap) {
            case WATER_VAPOR:
                fileNames.add(StringUtils.substringBeforeLast(granuleImagesParentName, "_") + "_B09.jp2");
                break;
            case VISIBLE:
                fileNames.add(StringUtils.substringBeforeLast(granuleImagesParentName, "_") + "_B04.jp2");
                fileNames.add(StringUtils.substringBeforeLast(granuleImagesParentName, "_") + "_B03.jp2");
                fileNames.add(StringUtils.substringBeforeLast(granuleImagesParentName, "_") + "_B02.jp2");
                break;
            case VEGETATION:
                fileNames.add(StringUtils.substringBeforeLast(granuleImagesParentName, "_") + "_B05.jp2");
                fileNames.add(StringUtils.substringBeforeLast(granuleImagesParentName, "_") + "_B06.jp2");
                fileNames.add(StringUtils.substringBeforeLast(granuleImagesParentName, "_") + "_B07.jp2");
                break;
        }

        return fileNames;
    }

    private File downloadFile(String fileName) {
        return Optional.ofNullable(
                restTemplate.execute(
                        gcsImageBucket + "/" + fileName,
                        HttpMethod.GET,
                        null,
                        response -> saveFile(changeFileExtension(fileName, "tmp"), response)))
                .orElseThrow(() -> new RuntimeException("Error during file downloading"));
    }

    private String changeFileExtension(String sourceFileName, String newExtension) {
        return String.format("%s.%s", FilenameUtils.removeExtension(sourceFileName), newExtension);
    }

    private File createDirectoryIfNotExists(String directory) {
        final File directoryFile = new File(directory);
        if (!directoryFile.exists()) {
            try {
                Files.createDirectories(directoryFile.toPath());
            } catch (IOException e) {
                log.error("Directory is not created, reason: ", e);
            }
            log.info("Created {}", directoryFile);
        }

        return directoryFile;
    }

    private File saveFile(String fileName, ClientHttpResponse response) {
        final Path path = Paths.get(storeIn, fileName);
        final File file = path.toFile();
        try (FileOutputStream out = new FileOutputStream(file);
             InputStream input = response.getBody()) {
            byte[] buffer = new byte[bufferSizeInMb * 1024 * 1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error("File is not saved, reason: ", e);
        }

        return file;
    }
}
