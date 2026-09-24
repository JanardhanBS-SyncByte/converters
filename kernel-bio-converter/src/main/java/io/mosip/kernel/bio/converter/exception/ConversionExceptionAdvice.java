package io.mosip.kernel.bio.converter.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.bio.converter.constant.ConverterErrorCode;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.EmptyCheckUtils;

/**
 * Global {@link RestControllerAdvice} that maps conversion and validation
 * failures into MOSIP {@link ResponseWrapper}{@ containing {@link ServiceError}
 * entries (HTTP 500, matching existing converter-service contract).
 *
 * <p>
 * Bean-validation failures on the convert request are remapped to
 * {@code MOS-CNV-*} codes via {@link #mapValidationErrors(MethodArgumentNotValidException)}.
 * Domain failures thrown as {@link ConversionException} keep their error code;
 * unexpected exceptions become {@code MOS-CNV-500}.
 * </p>
 *
 * @author Janardhan B S
 * @since 1.0.0
 */
@RestControllerAdvice
public class ConversionExceptionAdvice {
	/** SLF4J logger for validation and conversion failure diagnostics. */
	private static final Logger logger = LoggerFactory.getLogger(ConversionExceptionAdvice.class);

	/** Jackson mapper used to copy {@code id}/{@code version} from the cached request body. */
	private ObjectMapper objectMapper;

	/**
	 * Creates advice with the application {@link ObjectMapper}.
	 *
	 * @param objectMapper Jackson 2 mapper (from {@code spring-boot-jackson2})
	 */
	@Autowired
	public ConversionExceptionAdvice(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Handles {@link MethodArgumentNotValidException} from {@code @Valid} on the convert API.
	 *
	 * @param request HTTP request (preferably a {@link ContentCachingRequestWrapper})
	 * @param e       binding / constraint violations
	 * @return envelope with a single {@link ServiceError} and HTTP 500
	 * @throws Exception if the cached request body cannot be parsed
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseWrapper<ServiceError>> handleValidation(HttpServletRequest request,
			MethodArgumentNotValidException e) throws Exception {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(request);
		ConverterErrorCode code = mapValidationErrors(e);
		FieldError fieldError = e.getBindingResult().getFieldError();
		String message = fieldError != null ? fieldError.getDefaultMessage() : code.getErrorMessage();
		responseWrapper.getErrors().add(new ServiceError(code.getErrorCode(), message));
		logger.error("Validation failure: {} ", e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
				.body(responseWrapper);
	}

	/**
	 * Catch-all handler for {@link ConversionException}, {@link RuntimeException}, and other
	 * {@link Exception} types thrown from controllers or filters.
	 *
	 * @param request HTTP request used to populate envelope metadata
	 * @param e       the thrown exception
	 * @return envelope with {@link ServiceError} and HTTP 500
	 * @throws Exception if the cached request body cannot be parsed
	 */
	@ExceptionHandler(value = { Exception.class, RuntimeException.class, ConversionException.class })
	public ResponseEntity<ResponseWrapper<ServiceError>> defaultServiceErrorHandler(HttpServletRequest request,
			Exception e) throws Exception {
		ResponseWrapper<ServiceError> responseWrapper = setErrors(request);
		ServiceError error = null;
		if (e instanceof ConversionException conversionException) {
			error = new ServiceError(conversionException.getErrorCode(), conversionException.getMessage());
		} else {
			error = new ServiceError(ConverterErrorCode.TECHNICAL_ERROR_EXCEPTION.getErrorCode(), e.getMessage());
		}
		responseWrapper.getErrors().add(error);
		logger.error("Exception Root Cause: {} ", e.getMessage());
		logger.debug("Exception Root Cause:", e);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
				.body(responseWrapper);
	}

	/**
	 * Maps field-level validation failures to the closest {@link ConverterErrorCode}.
	 *
	 * @param e Spring validation exception
	 * @return matching converter error code (defaults to invalid request)
	 */
	private static ConverterErrorCode mapValidationErrors(MethodArgumentNotValidException e) {
		boolean source = false;
		boolean target = false;
		boolean values = false;
		for (FieldError err : e.getBindingResult().getFieldErrors()) {
			String simple = err.getField().contains(".")
					? err.getField().substring(err.getField().lastIndexOf('.') + 1)
					: err.getField();
			switch (simple) {
			case "sourceFormat" -> source = true;
			case "targetFormat" -> target = true;
			case "values" -> values = true;
			default -> {
			}
			}
		}
		if (source && target) {
			return ConverterErrorCode.INPUT_SOURCE_EXCEPTION;
		}
		if (source) {
			return ConverterErrorCode.INVALID_SOURCE_EXCEPTION;
		}
		if (target) {
			return ConverterErrorCode.INVALID_TARGET_EXCEPTION;
		}
		if (values) {
			return ConverterErrorCode.INVALID_REQUEST_EXCEPTION;
		}
		return ConverterErrorCode.INVALID_REQUEST_EXCEPTION;
	}

	/**
	 * Builds an empty {@link ResponseWrapper} and copies {@code id} / {@code version}
	 * from the JSON request body when available.
	 *
	 * @param httpServletRequest inbound request
	 * @return response wrapper with optional metadata populated
	 * @throws Exception if JSON parsing fails
	 */
	public ResponseWrapper<ServiceError> setErrors(HttpServletRequest httpServletRequest) throws Exception {
		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
		String requestBody = null;
		if (httpServletRequest instanceof ContentCachingRequestWrapper requestWrapper) {
			requestBody = new String(requestWrapper.getContentAsByteArray());
		}
		if (EmptyCheckUtils.isNullEmpty(requestBody)) {
			return responseWrapper;
		}
		JsonNode reqNode = objectMapper.readTree(requestBody);
		responseWrapper.setId(reqNode.path("id").asText());
		responseWrapper.setVersion(reqNode.path("version").asText());
		return responseWrapper;
	}
}
