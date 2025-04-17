package com.jadt.domain.pageSource;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AppiumAUT")
public class AppiumAUT {
    @XmlElement(name = "XCUIElementTypeApplication")
    private final ElementApplication application;

    public AppiumAUT(ElementApplication application) {
        this.application = application;
    }

    public ElementApplication getApplication() {
        return application;
    }
}
