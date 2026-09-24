package io.mosip.kernel.bio.converter.dto;

import java.util.Map;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

/**
 * Data Transfer Object for conversion requests in the bio converter service.
 * 
 * <p>
 * This DTO encapsulates the data required for converting biometric data from
 * one format to another.
 * </p>
 * 
 * <p>
 * Example usage:
 * 
 * <pre>
 * ConvertRequestDto request = new ConvertRequestDto();
 * request.setValues(valuesMap);
 * request.setSourceFormat("ISO19794_4_2011");
 * request.setTargetFormat("IMAGE/JPEG");
 * request.setSourceParameters(sourceParamsMap);
 * request.setTargetParameters(targetParamsMap);
 * </pre>
 * </p>
 * 
 * @author Janardhan B S
 * @since 1.0.0
 */

@Data
@ToString
public class ConvertRequestDto {
	/**
	 * A map of values required for the conversion. The map must contain at least
	 * one entry.
	 */
	@NotNull(message = "Values code can not be null")
	@Size(min = 1, message = "Minimum one entry required")
	public Map<String, String> values;

	/**
	 * The format of the source biometric data. The format code cannot be null,
	 * blank, or empty.
	 */
	@NotNull(message = "SourceFormat code can not be null")
	@NotBlank(message = "SourceFormat code can not be blank")
	@NotEmpty(message = "SourceFormat code can not be empty")
	public String sourceFormat;

	/**
	 * The format of the target biometric data. The format code cannot be null,
	 * blank, or empty.
	 */
	@NotNull(message = "TargetFormat code can not be null")
	@NotBlank(message = "TargetFormat code can not be blank")
	@NotEmpty(message = "TargetFormat code can not be empty")
	public String targetFormat;

	/**
	 * A map of source parameters specific to the source format.
	 */
	@SuppressWarnings({ "java:S1104" })
	public Map<String, String> sourceParameters;

	/**
	 * A map of target parameters specific to the target format.
	 */
	@SuppressWarnings({ "java:S1104" })
	public Map<String, String> targetParameters;

	/**
	 * No-arg constructor for Jackson / Spring binding.
	 */
	public ConvertRequestDto() {
		super();
	}

	/**
	 * Full constructor for programmatic request creation.
	 *
	 * @param values            modality key → Base64 URL-encoded ISO blob
	 * @param sourceFormat      source format code (e.g. {@code ISO19794_4_2011})
	 * @param targetFormat      target format code (e.g. {@code IMAGE/JPEG})
	 * @param sourceParameters  optional source hints
	 * @param targetParameters  optional target hints
	 */
	public ConvertRequestDto(
			@NotNull(message = "Values code can not be null") @Size(min = 1, message = "Minimum one entry required") Map<String, String> values,
			@NotNull(message = "SourceFormat code can not be null") @NotBlank(message = "SourceFormat code can not be blank") @NotEmpty(message = "SourceFormat code can not be empty") String sourceFormat,
			@NotNull(message = "TargetFormat code can not be null") @NotBlank(message = "TargetFormat code can not be blank") @NotEmpty(message = "TargetFormat code can not be empty") String targetFormat,
			Map<String, String> sourceParameters, Map<String, String> targetParameters) {
		super();
		this.values = values;
		this.sourceFormat = sourceFormat;
		this.targetFormat = targetFormat;
		this.sourceParameters = sourceParameters;
		this.targetParameters = targetParameters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ConvertRequestDto that = (ConvertRequestDto) o;
		return Objects.equals(values, that.values) && Objects.equals(sourceFormat, that.sourceFormat)
				&& Objects.equals(targetFormat, that.targetFormat)
				&& Objects.equals(sourceParameters, that.sourceParameters)
				&& Objects.equals(targetParameters, that.targetParameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(values, sourceFormat, targetFormat, sourceParameters, targetParameters);
	}

	/**
	 * Lombok-style equality helper used by generated {@code equals} patterns.
	 *
	 * @param other candidate object
	 * @return {@code true} if {@code other} is a {@link ConvertRequestDto}
	 */
	public boolean canEqual(Object other) {
		return other instanceof ConvertRequestDto;
	}
}