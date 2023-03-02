package cz.zvirdaniel.smarthome.configs;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import cz.zvirdaniel.smarthome.models.StatusEnum;

import java.io.IOException;

public class StatusEnumSerializer extends StdSerializer<StatusEnum> {
	public StatusEnumSerializer() {
		super(StatusEnum.class);
	}

	@Override
	public void serialize(StatusEnum value, JsonGenerator generator, SerializerProvider provider) throws IOException {
		generator.writeNumber(value.getStatus());
	}
}
