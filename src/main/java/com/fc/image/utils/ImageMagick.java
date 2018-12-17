package com.fc.image.utils;

import jj2000.j2k.fileformat.reader.FileFormatReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.IMOps;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ImageMagick {

    /**
     * For some reasons original .jp2 files is not possible read. {@link com.github.jaiimageio.jpeg2000.impl.J2KReadState}
     * throws java.io.IOException("File too long"). Reason for that {@link FileFormatReader#readFileFormat()} in line 206
     * has -1. Fortunately this issue is not present during reading .jp2 file modified by ImageMagick with 100% resize
     * operation.
     *
     * @param inputFileName  Input file name
     * @param outputFileName Output file name
     * @return result of operation
     */
    public static boolean magic(String inputFileName, String outputFileName, int resizeFactorPercentage) {
        final ConvertCmd cmd = new ConvertCmd();
        final IMOperation op = new IMOperation();
        op.addImage(inputFileName);
        final IMOps resize = op.resize();
        resize.getCmdArgs().add(resizeFactorPercentage + "%");
        op.addImage(outputFileName);
        try {
            log.debug("running: {}", op);
            cmd.run(op);
            log.info("applied magic to file '{}'", outputFileName);
            return true;
        } catch (Exception e) {
            log.error("failed ImageMagick operation, reason: ", e);
        }
        return false;
    }
}
