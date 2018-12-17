package com.fc.image.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class J2KToJpegConverter {

    public static File convert(List<InputStream> inputs, String outputFileName) {
        final File jpeg = Paths.get(outputFileName).toFile();
        try {
            final List<BufferedImage> bufferedImages = inputs.stream()
                    .map(J2KToJpegConverter::readSafe)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            final BufferedImage bufferedImage = bufferedImages.get(0);

            final int width = bufferedImage.getWidth();
            final int height = bufferedImage.getHeight();


            BufferedImage bufferedJpegImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int p;
                    int r = bufferedImage.getRGB(x, y);
                    if (bufferedImages.size() > 1) {
                        int g = bufferedImages.get(1).getRGB(x, y);
                        int b = bufferedImages.get(2).getRGB(x, y);
                        p = (r << 16) | (g << 8) | b;
                    } else {
                        p = (r << 16) | (r << 8) | r;
                    }
                    bufferedJpegImage.setRGB(x, y, p);
                }
            }

            ImageIO.write(bufferedJpegImage, "jpeg", jpeg);

        } catch (Exception e) {
            log.error("Can't Visible file, reason: ", e);
        }

        log.info("Converted file '{}'", jpeg);
        return jpeg;
    }

    private static Optional<BufferedImage> readSafe(InputStream inputStream) {
        try {
            return Optional.of(ImageIO.read(inputStream));
        } catch (Exception e) {
            log.error("Can't read input stream, reason: ", e);
        }
        return Optional.empty();
    }

}
