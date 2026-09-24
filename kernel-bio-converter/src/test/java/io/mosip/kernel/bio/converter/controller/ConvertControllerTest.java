package io.mosip.kernel.bio.converter.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.bio.converter.TestBootApplication;
import io.mosip.kernel.bio.converter.constant.SourceFormatCode;
import io.mosip.kernel.bio.converter.constant.TargetFormatCode;
import io.mosip.kernel.bio.converter.dto.ConvertRequestDto;
import io.mosip.kernel.bio.converter.exception.ConversionException;
import io.mosip.kernel.bio.converter.util.ConverterDataUtil;
import io.mosip.kernel.core.http.RequestWrapper;

/**
 * Convert Handler Controller Test
 * <p>
 * This class contains integration tests for the Convert Controller
 * functionality. It covers various scenarios related to converting biometric
 * data from one format to another.
 * </p>
 * <p>
 * Each test method focuses on different aspects of the conversion process,
 * including validation of input data, handling of different biometric formats,
 * and ensuring correct transformation to target formats.
 * </p>
 *
 * @author Janardhan B S
 * @since 1.0.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestBootApplication.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConvertControllerTest {
	@Autowired
	public MockMvc mockMvc;

	private RequestWrapper<ConvertRequestDto> convertRequestDto = new RequestWrapper<ConvertRequestDto>();
	private ObjectMapper mapper;

	/**
	 * Set up method to initialize ObjectMapper and default values for test cases.
	 */
	@Before
	public void setUp() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		convertRequestDto.setId("sample-converter");
		convertRequestDto.setVersion("1.0");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto is null.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t001ConvertTest() throws Exception {
		String req = "{" + "\"values\": null," + "\"sourceFormat\":null," + "\"targetFormat\":null,"
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-001");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto values are
	 * null or empty.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t002ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "}," + "\"sourceFormat\":\"ISO19794_4_2011\","
				+ "\"targetFormat\":\"IMAGE/JPEG\"," + "\"sourceParameters\":{" + "\"key\":\"value\"" + "},"
				+ "\"targetParameters\":{" + "\"key\":\"value\"" + "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-002");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto source format
	 * is null or empty.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t0030ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left Thumb\": \"values\"" + "}," + "\"sourceFormat\":\" \","
				+ "\"targetFormat\":\"string\"," + "\"sourceParameters\":{" + "\"key\":\"value\"" + "},"
				+ "\"targetParameters\":{" + "\"key\":\"value\"" + "}" + "}";

		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-003");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto source format
	 * is incorrect. wrong values other than (ISO19794_4_2011, ISO19794_5_2011,
	 * ISO19794_6_2011)
	 * 
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t0031ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left Thumb\": \"values\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_3_2011\"," + "\"targetFormat\":\"string\"," + "\"sourceParameters\":{"
				+ "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\"" + "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-003");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto target format
	 * is null or empty.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t0040ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left Thumb\": \"values\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_4_2011\"," + "\"targetFormat\":\"\"," + "\"sourceParameters\":{"
				+ "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\"" + "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-004");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto target format
	 * is incorrect other than (IMAGE/JPEG, IMAGE/PNG) Current Working Future
	 * Implementation (ISO19794_4_2011_JPEG, ISO19794_5_2011_JPEG,
	 * ISO19794_6_2011_JPEG, ISO19794_4_2011_PNG, ISO19794_5_2011_PNG,
	 * ISO19794_6_2011_PNG)
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t0041ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left Thumb\": \"values\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_4_2011\"," + "\"targetFormat\":\"IMAGE/JPEGLL\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-004");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto Request Value
	 * not null or empty.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t005ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left Thumb\": \"\"" + "}," + "\"sourceFormat\":\"ISO19794_4_2011\","
				+ "\"targetFormat\":\"IMAGE/JPEG\"," + "\"sourceParameters\":{" + "\"key\":\"value\"" + "},"
				+ "\"targetParameters\":{" + "\"key\":\"value\"" + "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-005");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto Request Value
	 * not base64UrlEncoded.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t006ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left Thumb\": \"12SGVsbGxyz8gd29ybGQ=\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_4_2011\"," + "\"targetFormat\":\"IMAGE/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-006");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto Source not
	 * valid ISO ISO19794_4_2011.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t008ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left Thumb\": \"aGVsbG8gaG93IGFyZSB5b3U\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_4_2011\"," + "\"targetFormat\":\"IMAGE/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-008");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto Source not
	 * valid ISO ISO19794_5_2011.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t009ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left Thumb\": \"aGVsbG8gaG93IGFyZSB5b3U\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_5_2011\"," + "\"targetFormat\":\"IMAGE/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-009");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto Source not
	 * valid ISO ISO19794_5_2011.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t0091ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Face\": \"aGVsbG8gaG93IGFyZSB5b3U\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_5_2011\"," + "\"targetFormat\":\"IMAGE/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-009");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto Source is
	 * valid ISO ISO19794_5_2011 and Target code also valid , base64 data is wrong
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t0092ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Face\": \"12SGVsbGxyz8gd29ybGQ=\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_5_2011\"," + "\"targetFormat\":\"IMAGE/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-006");
	}
	
	/**
	 * Integration test for handling scenarios where ConvertRequestDto Source not
	 * valid ISO ISO19794_6_2011.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t010ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left Thumb\": \"aGVsbG8gaG93IGFyZSB5b3U\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_6_2011\"," + "\"targetFormat\":\"IMAGE/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-010");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto Target
	 * Format(ISO19794_6_2011_JPEG) Not Supported For the Given Source
	 * Format(ISO19794_6_2011).
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t011ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left Thumb\": \"aGVsbG8gaG93IGFyZSB5b3U\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_6_2011\"," + "\"targetFormat\":\"ISO19794_6_2011/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-004");
	}

	/**
	 * Integration test for handling scenarios where ConvertRequestDto Target
	 * Format(IMAGE/JPEG) and Source Format(ISO19794_6_2011) and ISO base64 wrong.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t0111ConvertTest() throws Exception {
		String req = "{" + "\"values\":{" + "\"Left\": \"12SGVsbGxyz8gd29ybGQ=\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_6_2011\"," + "\"targetFormat\":\"IMAGE/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil
				.checkResponse(
						mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
						500, null, "MOS-CNV-006");
	}

	/**
	 * Integration test for converting biometric data from ISO19794_4_2011 format to
	 * JPEG image format.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t012ConvertTest() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/resources/finger.txt");
		String bioData = IOUtils.toString(fis, StandardCharsets.UTF_8);
		String req = "{" + "\"values\":{" + "\"Left IndexFinger\": \"" + bioData + "\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_4_2011\"," + "\"targetFormat\":\"IMAGE/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil.checkResponse(
				mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
				200, SourceFormatCode.ISO19794_4_2011, TargetFormatCode.IMAGE_JPEG.getCode());
	}

	/**
	 * Integration test for converting biometric data from ISO19794_4_2011 with WSQ image format to
	 * JPEG image format.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t012ConvertTest_WSQ() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/resources/finger_wsq.txt");
		String bioData = IOUtils.toString(fis, StandardCharsets.UTF_8);
		String req = "{" + "\"values\":{" + "\"Left IndexFinger\": \"" + bioData + "\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_4_2011\"," + "\"targetFormat\":\"IMAGE/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil.checkResponse(
				mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
				200, SourceFormatCode.ISO19794_4_2011, TargetFormatCode.IMAGE_JPEG.getCode());
	}

	/**
	 * Integration test for converting biometric data from ISO19794_4_2011 format to
	 * PNG image format.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t0121ConvertTest() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/resources/finger.txt");
		String bioData = IOUtils.toString(fis, StandardCharsets.UTF_8);
		String req = "{" + "\"values\":{" + "\"Left IndexFinger\": \"" + bioData + "\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_4_2011\"," + "\"targetFormat\":\"IMAGE/PNG\"," + "\"sourceParameters\":{"
				+ "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\"" + "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil.checkResponse(
				mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
				200, SourceFormatCode.ISO19794_4_2011, TargetFormatCode.IMAGE_PNG.getCode());
	}

	/**
	 * Integration test for converting biometric data from ISO19794_5_2011 format to
	 * JPEG image format.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t013ConvertTest() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/resources/face.txt");
		String bioData = IOUtils.toString(fis, StandardCharsets.UTF_8);
		String req = "{" + "\"values\":{" + "\"Face\": \"" + bioData + "\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_5_2011\"," + "\"targetFormat\":\"IMAGE/PNG\"," + "\"sourceParameters\":{"
				+ "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\"" + "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil.checkResponse(
				mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
				200, SourceFormatCode.ISO19794_5_2011, TargetFormatCode.IMAGE_PNG.getCode());
	}

	/**
	 * Integration test for converting biometric data from ISO19794_5_2011 format to
	 * PNG image format.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	@SuppressWarnings({ "java:S4144" })
	public void t0131ConvertTest() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/resources/face.txt");
		String bioData = IOUtils.toString(fis, StandardCharsets.UTF_8);
		String req = "{" + "\"values\":{" + "\"Face\": \"" + bioData + "\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_5_2011\"," + "\"targetFormat\":\"IMAGE/PNG\"," + "\"sourceParameters\":{"
				+ "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\"" + "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil.checkResponse(
				mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
				200, SourceFormatCode.ISO19794_5_2011, TargetFormatCode.IMAGE_PNG.getCode());
	}

	/**
	 * Integration test for converting biometric data from ISO19794_6_2011 format to
	 * JPEG image format.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t014ConvertTest() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/resources/iris.txt");
		String bioData = IOUtils.toString(fis, StandardCharsets.UTF_8);

		String req = "{" + "\"values\":{" + "\"Left Iris\": \"" + bioData + "\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_6_2011\"," + "\"targetFormat\":\"IMAGE/JPEG\","
				+ "\"sourceParameters\":{" + "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\""
				+ "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil.checkResponse(
				mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
				200, SourceFormatCode.ISO19794_6_2011, TargetFormatCode.IMAGE_JPEG.getCode());
	}

	/**
	 * Integration test for converting biometric data from ISO19794_6_2011 format to
	 * PNG image format.
	 *
	 * @throws Exception if an error occurs during execution
	 */
	@Test
	@WithMockUser("reg-officer")
	public void t0141ConvertTest() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/resources/iris.txt");
		String bioData = IOUtils.toString(fis, StandardCharsets.UTF_8);

		String req = "{" + "\"values\":{" + "\"Left Iris\": \"" + bioData + "\"" + "},"
				+ "\"sourceFormat\":\"ISO19794_6_2011\"," + "\"targetFormat\":\"IMAGE/PNG\"," + "\"sourceParameters\":{"
				+ "\"key\":\"value\"" + "}," + "\"targetParameters\":{" + "\"key\":\"value\"" + "}" + "}";
		convertRequestDto.setRequest(mapper.readValue(req, ConvertRequestDto.class));

		ConverterDataUtil.checkResponse(
				mockMvc.perform(post("/convert").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(convertRequestDto))).andReturn(),
				200, SourceFormatCode.ISO19794_6_2011, TargetFormatCode.IMAGE_PNG.getCode());
	}
	
	@Test
	@WithMockUser("reg-officer")
	public void t004ConvertTest_invalidSourceFormat() throws Exception {
	    Map<String, String> values = Map.of("key1", "validData");

	    convertRequestDto.setRequest(new ConvertRequestDto(values, "INVALID_FORMAT", "TARGET_FORMAT", Map.of(), Map.of()));
	    String requestContent = mapper.writeValueAsString(convertRequestDto);

	    mockMvc.perform(post("/convert")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(requestContent))
	            .andExpect(status().isInternalServerError())
	            .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConversionException))
	            .andExpect(jsonPath("$.errors[0].errorCode").value("MOS-CNV-003"));
	}
}