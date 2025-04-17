package com.jadt.builder;

import io.appium.java_client.remote.options.BaseOptions;

import java.util.HashMap;
import java.util.Map;

public class DriverOptionsBuilder<T extends BaseOptions<T>> {
    private final Map<String, Object> capabilities;

    public DriverOptionsBuilder() {
        this.capabilities = new HashMap<>();
    }

    public DriverOptionsBuilder<T> setCapabilities(String key, Object value) {
        this.capabilities.put(key, value);
        return this;
    }

    public T build(T t) {
        t = t.merge(new BaseOptions<>(capabilities));
        return t;
    }
}
