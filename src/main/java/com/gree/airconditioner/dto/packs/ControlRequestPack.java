package com.gree.airconditioner.dto.packs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gree.airconditioner.models.GreeDeviceBinding;
import com.gree.airconditioner.dto.status.*;
import com.gree.airconditioner.utils.CryptoUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.gree.airconditioner.Application.OBJECT_MAPPER;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ControlRequestPack {
    private GreeDeviceStatus status;

    public String toJson() {
        ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
        objectNode.put("t", "cmd");

        ArrayNode optNode = OBJECT_MAPPER.createArrayNode();
        ArrayNode pNode = OBJECT_MAPPER.createArrayNode();

        for (Field field : GreeDeviceStatus.class.getDeclaredFields()) {
            String property = getPropertyName(field);
            Object value = getValue(status, field);
            if (value == null) {
                continue;
            }

            if (value instanceof Integer) {
                optNode.add(property);
                pNode.add((Integer) value);

            } else if (value instanceof String) {
                optNode.add(property);
                pNode.add((String) value);

            } else if (value instanceof Long) {
                optNode.add(property);
                pNode.add((Long) value);

            } else if (value instanceof Switch) {
                optNode.add(property);
                pNode.add(((Switch) value).getStatus());

            } else if (value instanceof FanMode) {
                optNode.add(property);
                pNode.add(((FanMode) value).getStatus());

            } else if (value instanceof OperationMode) {
                optNode.add(property);
                pNode.add(((OperationMode) value).getStatus());

            } else if (value instanceof SwingDirection) {
                optNode.add(property);
                pNode.add(((SwingDirection) value).getStatus());

            } else if (value instanceof Temperature) {
                Temperature castedValue = (Temperature) value;

                optNode.add("SetTem");
                pNode.add(castedValue.getTemperature());

                optNode.add("TemUn");
                pNode.add(castedValue.getUnit().getStatus());
            }
        }

        objectNode.put("opt", optNode);
        objectNode.put("p", pNode);

        return objectNode.toString();
    }

    private String getPropertyName(Field field) {
        if (!field.isAnnotationPresent(JsonProperty.class)) {
            return null;
        }
        JsonProperty property = field.getDeclaredAnnotation(JsonProperty.class);
        return property.value();
    }

    private Object getValue(GreeDeviceStatus status, Field field) {
        String name = field.getName();
        String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
        try {
            Method method = GreeDeviceStatus.class.getMethod(methodName);
            return method.invoke(status);
        } catch (NoSuchMethodException e) {
            log.error("Can't find method {}", methodName, e);
        } catch (IllegalAccessException e) {
            log.error("Method not accessible {}", methodName, e);
        } catch (InvocationTargetException e) {
            log.error("Can't invoke method {}", methodName, e);
        }
        return null;
    }

    public String encrypted(final String aesKey) {
        return CryptoUtil.encryptPack(aesKey.getBytes(), this.toJson());
    }
}
