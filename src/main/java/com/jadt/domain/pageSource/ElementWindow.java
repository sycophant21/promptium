package com.jadt.domain.pageSource;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "XCUIElementWindow")
public class ElementWindow extends XCUIElement {
    public ElementWindow(String type, boolean enabled, boolean visible, boolean accessible, int x, int y, int width, int height, int index, Object placeholderValue, List<XCUIElement> children) {
        super(type, enabled, visible, accessible, x, y, width, height, index, placeholderValue, children);
    }

    public ElementWindow() {
    }
}
