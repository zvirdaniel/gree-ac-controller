package com.gree.airconditioner.dto.packs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gree.airconditioner.models.GreeDeviceBinding;
import com.gree.airconditioner.dto.status.*;
import com.gree.airconditioner.utils.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.gree.airconditioner.Application.OBJECT_MAPPER;

@Slf4j
@RequiredArgsConstructor
public class StatusResponsePack {
    private final GreeDeviceBinding binding;
    private final String json;

    public GreeDeviceStatus asGreeDeviceStatus() {
        final JsonNode packJsonNode = this.getPackJsonNode();
        final Map<String, Integer> properties = this.getPropertiesAndValuesAsMap(packJsonNode);
        return this.getGreeDeviceStatus(properties);
    }

    private GreeDeviceStatus getGreeDeviceStatus(final Map<String, Integer> properties) {
        GreeDeviceStatus status = new GreeDeviceStatus();
        for (Field field : GreeDeviceStatus.class.getDeclaredFields()) {
            String property = getPropertyName(field);
            if (property == null) {
                continue;
            }

            Integer value = properties.get(property);
            if (value == null) {
                log.debug("No value set for {}", property);
                continue;
            }

            if (field.getType().equals(Switch.class)) {
                setValue(status, field, Switch.fromCode(value));
            } else if (field.getType().equals(SwingDirection.class)) {
                setValue(status, field, new SwingDirection(value));
            } else if (field.getType().equals(FanMode.class)) {
                setValue(status, field, FanMode.fromCode(value));
            } else if (field.getType().equals(OperationMode.class)) {
                setValue(status, field, OperationMode.fromCode(value));
            }
        }
        try {
            Field temperatureField = Temperature.class.getDeclaredField("temperature");
            String temperatureProperty = getPropertyName(temperatureField);
            Integer temperatureValue = properties.get(temperatureProperty);

            Field unitField = Temperature.class.getDeclaredField("unit");
            String unitProperty = getPropertyName(unitField);

            Integer unitValue = properties.get(unitProperty);
            Temperature temperature = new Temperature(temperatureValue, TemperatureUnit.fromCode(unitValue));
            status.setTemperature(temperature);

        } catch (NoSuchFieldException e) {
            log.error("No such field!", e);
        }

        return status;
    }

    private Object setValue(GreeDeviceStatus status, Field field, Object value) {
        String name = field.getName();
        String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        try {
            Method method = GreeDeviceStatus.class.getMethod(methodName, value.getClass());
            return method.invoke(status, value);
        } catch (NoSuchMethodException e) {
            log.error("Can't find method {}", methodName, e);
        } catch (IllegalAccessException e) {
            log.error("Method not accessible {}", methodName, e);
        } catch (InvocationTargetException e) {
            log.error("Can't invoke method {}", methodName, e);
        }
        return null;
    }

    private String getPropertyName(Field field) {
        if (!field.isAnnotationPresent(JsonProperty.class)) {
            return null;
        }
        JsonProperty property = field.getDeclaredAnnotation(JsonProperty.class);
        return property.value();
    }

    private JsonNode getPackJsonNode() {
        try {
            final JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
            final JsonNode packNode = jsonNode.get("pack");
            final String encryptedPack = packNode.asText();
            final String packJson = CryptoUtil.decryptPack(binding.getAesKey().getBytes(), encryptedPack);
            return OBJECT_MAPPER.readTree(packJson);
        } catch (IOException e) {
            log.error("Can't get pack from json {}", json, e);
            return null;
        }
    }

    private Map<String, Integer> getPropertiesAndValuesAsMap(final JsonNode packJsonNode) {
        Objects.requireNonNull(packJsonNode);
        Map<String, Integer> properties = new HashMap<>();
        JsonNode colsNode = packJsonNode.get("cols");
        List<String> colsList = new LinkedList<>();
        for (final JsonNode colNode : colsNode) {
            colsList.add(colNode.asText());
        }

        JsonNode datsNode = packJsonNode.get("dat");
        List<Integer> datList = new LinkedList<>();
        for (final JsonNode datNode : datsNode) {
            datList.add(datNode.asInt());
        }

        for (int i = 0; i < colsList.size(); i++) {
            String col = colsList.get(i);
            properties.put(col, datList.get(i));
        }

        return properties;
    }
}
