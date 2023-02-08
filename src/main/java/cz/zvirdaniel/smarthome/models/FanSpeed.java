package cz.zvirdaniel.smarthome.models;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FanSpeed implements StatusEnum {
	AUTO(0),
	LOW(1),
	MEDIUM_LOW(2),
	MEDIUM(3),
	MEDIUM_HIGH(4),
	HIGH(5);

	@JsonValue
	private final int status;
}
