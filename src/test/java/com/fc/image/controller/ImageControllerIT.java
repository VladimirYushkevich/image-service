package com.fc.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc.image.dto.ImageDTO;
import com.fc.image.service.FileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Paths;

import static com.fc.image.dto.ChannelMap.*;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ImageControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FileService fileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        final String tempFolderPath = folder.getRoot().getPath();
        fileService.setStoreIn(tempFolderPath + "/" + "in");
        fileService.setStoreOut(tempFolderPath + "/" + "out");

        mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    @Test
    public void getWaterVaporImage() throws Exception {
        this.mockServer.expect(ExpectedCount.times(1),
                requestTo("http://mock.endpoint.com/path/some_long_name_waterVapor_N02.02/IMG_DATA/some_long_name_waterVapor_B09.jp2"))
                .andRespond(withSuccess(IOUtils.toByteArray(getClass().getResourceAsStream("/data/waterVapor.jp2")),
                        MediaType.APPLICATION_OCTET_STREAM));

        final MvcResult mvcResult = mockMvc.perform(post("/images")
                .content(
                        objectMapper.writeValueAsString(
                                ImageDTO.builder()
                                        .granuleImages("/path/some_long_name_waterVapor_N02.02/IMG_DATA")
                                        .channelMap(WATER_VAPOR)
                                        .build()
                        ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        final File createdFile = Paths.get(fileService.getStoreOut(), "waterVapor.jpeg").toFile();
        FileUtils.writeByteArrayToFile(createdFile, mvcResult.getResponse().getContentAsByteArray());

        final File expected = Paths.get(getClass().getResource("/data").getFile(), "waterVapor_100.jpeg").toFile();
        assertTrue(FileUtils.contentEquals(createdFile, expected));
    }

    @Test
    public void getVisibleImage() throws Exception {
        this.mockServer.expect(ExpectedCount.times(1),
                requestTo("http://mock.endpoint.com/path/some_long_name_visible_N02.02/IMG_DATA/some_long_name_visible_B04.jp2"))
                .andRespond(withSuccess(IOUtils.toByteArray(getClass().getResourceAsStream("/data/visible_r_10.jp2")),
                        MediaType.APPLICATION_OCTET_STREAM));
        this.mockServer.expect(ExpectedCount.times(1),
                requestTo("http://mock.endpoint.com/path/some_long_name_visible_N02.02/IMG_DATA/some_long_name_visible_B03.jp2"))
                .andRespond(withSuccess(IOUtils.toByteArray(getClass().getResourceAsStream("/data/visible_g_10.jp2")),
                        MediaType.APPLICATION_OCTET_STREAM));
        this.mockServer.expect(ExpectedCount.times(1),
                requestTo("http://mock.endpoint.com/path/some_long_name_visible_N02.02/IMG_DATA/some_long_name_visible_B02.jp2"))
                .andRespond(withSuccess(IOUtils.toByteArray(getClass().getResourceAsStream("/data/visible_b_10.jp2")),
                        MediaType.APPLICATION_OCTET_STREAM));

        final MvcResult mvcResult = mockMvc.perform(post("/images")
                .content(
                        objectMapper.writeValueAsString(
                                ImageDTO.builder()
                                        .granuleImages("/path/some_long_name_visible_N02.02/IMG_DATA")
                                        .channelMap(VISIBLE)
                                        .build()
                        ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        final File createdFile = Paths.get(fileService.getStoreOut(), "visible.jpeg").toFile();
        FileUtils.writeByteArrayToFile(createdFile, mvcResult.getResponse().getContentAsByteArray());

        final File expected = Paths.get(getClass().getResource("/data").getFile(), "visible.jpeg").toFile();
        assertTrue(FileUtils.contentEquals(createdFile, expected));
    }

    @Test
    public void getVegetationImage() throws Exception {
        this.mockServer.expect(ExpectedCount.times(1),
                requestTo("http://mock.endpoint.com/path/some_long_name_vegetation_N02.02/IMG_DATA/some_long_name_vegetation_B05.jp2"))
                .andRespond(withSuccess(IOUtils.toByteArray(getClass().getResourceAsStream("/data/vegetation_r_20.jp2")),
                        MediaType.APPLICATION_OCTET_STREAM));
        this.mockServer.expect(ExpectedCount.times(1),
                requestTo("http://mock.endpoint.com/path/some_long_name_vegetation_N02.02/IMG_DATA/some_long_name_vegetation_B06.jp2"))
                .andRespond(withSuccess(IOUtils.toByteArray(getClass().getResourceAsStream("/data/vegetation_g_20.jp2")),
                        MediaType.APPLICATION_OCTET_STREAM));
        this.mockServer.expect(ExpectedCount.times(1),
                requestTo("http://mock.endpoint.com/path/some_long_name_vegetation_N02.02/IMG_DATA/some_long_name_vegetation_B07.jp2"))
                .andRespond(withSuccess(IOUtils.toByteArray(getClass().getResourceAsStream("/data/vegetation_b_20.jp2")),
                        MediaType.APPLICATION_OCTET_STREAM));

        final MvcResult mvcResult = mockMvc.perform(post("/images")
                .content(
                        objectMapper.writeValueAsString(
                                ImageDTO.builder()
                                        .granuleImages("/path/some_long_name_vegetation_N02.02/IMG_DATA")
                                        .channelMap(VEGETATION)
                                        .build()
                        ))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        final File createdFile = Paths.get(fileService.getStoreOut(), "vegetation.jpeg").toFile();
        FileUtils.writeByteArrayToFile(createdFile, mvcResult.getResponse().getContentAsByteArray());

        final File expected = Paths.get(getClass().getResource("/data").getFile(), "vegetation.jpeg").toFile();
        assertTrue(FileUtils.contentEquals(createdFile, expected));
    }
}
