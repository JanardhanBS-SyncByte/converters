package io.mosip.kernel.bio.converter.controller;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.bio.converter.dto.ConvertRequestDto;
import io.mosip.kernel.bio.converter.exception.ConversionException;
import io.mosip.kernel.bio.converter.service.IConverterApi;
import io.mosip.kernel.bio.converter.service.impl.ConverterServiceImpl;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * Controller class for handling conversion requests.
 * 
 * <p>
 * This controller class defines an endpoint for handling conversion requests.
 * It accepts a {@link ConvertRequestDto} object as input, validates it, and
 * delegates the conversion process to the {@link IConverterApi} service. The
 * controller method is annotated with {@link ResponseFilter} to apply response
 * filtering.
 * </p>
 * 
 * @author Janardhan B S
 * @since 1.0.0
 */
@RestController
public class ConvertController {
	/**
	 * Service instance {@link ConverterServiceImpl}
	 */
	private IConverterApi converterService;

	/**
	 * Constructor for initializing the controller with a converter service.
	 * 
	 * @param converterService the service implementation {@link IConverterApi}.
	 */
	@Autowired
	public ConvertController(IConverterApi converterService) {
		this.converterService = converterService;
	}

	/**
	 * Endpoint for handling conversion requests.
	 * 
	 * <p>
	 * This method processes POST requests to "/convert". It expects a JSON payload
	 * containing a {@link ConvertRequestDto} object. The request is validated using
	 * {@link Valid} annotation, and then passed to the {@code converterService} to
	 * perform the conversion. The response is wrapped in a {@link ResponseWrapper}.
	 * </p>
	 * 
	 * @param convertRequestDto the request body containing conversion parameters.
	 * @return a {@link ResponseWrapper} containing the converted output.
	 * @throws ConversionException if there is an error during the conversion
	 *                             process.
	 */
	@ResponseFilter
	@PostMapping(value = "/convert", produces = "application/json")
	public ResponseWrapper<Map<String, String>> convert(
			@RequestBody @Valid RequestWrapper<ConvertRequestDto> convertRequestDto) throws ConversionException {
		ResponseWrapper<Map<String, String>> responseDto = new ResponseWrapper<>();

		responseDto.setResponse(converterService.convert(convertRequestDto.getRequest().getValues(),
				convertRequestDto.getRequest().getSourceFormat(), convertRequestDto.getRequest().getTargetFormat(),
				convertRequestDto.getRequest().getSourceParameters(),
				convertRequestDto.getRequest().getTargetParameters()));

		return responseDto;
	}
}
