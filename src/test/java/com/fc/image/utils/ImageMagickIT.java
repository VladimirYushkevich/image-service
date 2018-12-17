package com.fc.image.utils;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

@ActiveProfiles({"test", "it"})
public class ImageMagickIT {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldMakeAMagicForOriginalImage() throws Exception {
        final String inputFileName = getClass().getResource("/data/waterVapor.jp2").toURI().getPath();
        final String outputFileName = folder.getRoot().getPath() + "/" + "processable.jp2";

        assertTrue(ImageMagick.magic(inputFileName, outputFileName, 100));
        assertTrue(FileUtils.contentEquals(new File(outputFileName),
                Paths.get(getClass().getResource("/data").getFile(), "waterVapor_100.jp2").toFile()));
    }
}
