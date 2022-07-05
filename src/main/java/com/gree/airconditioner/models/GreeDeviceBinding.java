package com.gree.airconditioner.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.GregorianCalendar;

@Data
@RequiredArgsConstructor
public class GreeDeviceBinding {
    private final GreeDevice device;
    private final String aesKey;
    private final Date creationDate = GregorianCalendar.getInstance().getTime();
}
