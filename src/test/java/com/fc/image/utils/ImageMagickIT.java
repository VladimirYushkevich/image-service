package com.fc.image.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ImageMagickIT {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldMakeAMagicForOriginalImage() throws Exception {
        final String inputFileName = getClass().getResource("/data/waterVapor.jp2").toURI().getPath();
        final String outputFileName = folder.getRoot().getPath() + "/" + "processable.jp2";

        assertTrue(ImageMagick.magic(inputFileName, outputFileName, 100));
        assertTrue("converted file size is less or equal as expected (depends on Imagemagick version)",
                new File(outputFileName).length() <= Paths.get(getClass().getResource("/data").getFile(),
                        "waterVapor_100.jp2").toFile().length());
    }
}
