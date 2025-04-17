package com.jadt.connector;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.options.BaseOptions;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class DriverConnector<T extends BaseOptions<T>> {
    public AppiumDriver initialiseDriver(T options) throws URISyntaxException, MalformedURLException {
        if (options instanceof XCUITestOptions) {
            return new IOSDriver(new URI("http://127.0.0.1:4723").toURL(), options);
        } else {
            return new AppiumDriver(new URI("http://127.0.0.1:4723").toURL(), options);
        }

    }
}
