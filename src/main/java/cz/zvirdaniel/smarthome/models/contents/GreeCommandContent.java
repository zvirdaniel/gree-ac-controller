package cz.zvirdaniel.smarthome.models.contents;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zvirdaniel.smarthome.models.GreeType;
import lombok.Data;

@Data
public class GreeCommandContent {
	@JsonProperty("t")
	private GreeType type;

	@JsonProperty("opt")
	private String[] columns;

	@JsonProperty("p")
	private Object[] data;
}
