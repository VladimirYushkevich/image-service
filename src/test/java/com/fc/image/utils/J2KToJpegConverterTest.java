package com.fc.image.utils;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.fc.image.utils.J2KToJpegConverter.convert;
import static org.junit.Assert.assertTrue;

public class J2KToJpegConverterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldConvertVisibleChannel() throws Exception {
        final String outputFileName = folder.getRoot().getPath() + "/" + "visible.jpeg";

        final File convertedJpegFile = convert(
                Arrays.asList(
                        getClass().getResourceAsStream("/data/visible_r_10.jp2"),
                        getClass().getResourceAsStream("/data/visible_g_10.jp2"),
                        getClass().getResourceAsStream("/data/visible_b_10.jp2")
                ),
                outputFileName
        );

        assertTrue(FileUtils.contentEquals(convertedJpegFile,
                Paths.get(getClass().getResource("/data").getFile(), "visible_10.jpeg").toFile()));
    }

    @Test
    public void shouldConvertWaterVaporChannel() throws Exception {
        final String outputFileName = folder.getRoot().getPath() + "/" + "waterVapor.jpeg";

        final File convertedJpegFile = convert(
                Arrays.asList(
                        getClass().getResourceAsStream("/data/waterVapor_50.jp2")
                ),
                outputFileName
        );

        assertTrue(FileUtils.contentEquals(convertedJpegFile,
                Paths.get(getClass().getResource("/data").getFile(), "waterVapor_50.jpeg").toFile()));
    }
}
