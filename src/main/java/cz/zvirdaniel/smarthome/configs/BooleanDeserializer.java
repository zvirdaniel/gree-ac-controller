package cz.zvirdaniel.smarthome.configs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.List;

public class BooleanDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final String text = p.getText();
        if (text != null) {
            if (List.of("1", "yes", "true", "enabled").contains(text)) {
                return Boolean.TRUE;
            } else if (List.of("0", "no", "false", "disabled").contains(text)) {
                return Boolean.FALSE;
            }
        }

        return null;
    }

    public static SimpleModule asModule() {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(Boolean.class, new BooleanDeserializer());
        return module;
    }
}
