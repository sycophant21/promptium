package com.jadt.domain.pageSource;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;
@XmlRootElement(name = "XCUIElementStaticText")
public class ElementStaticText extends XCUIElement {
    private Object value;
    private String name;
    private String label;

    public ElementStaticText(String type, boolean enabled, boolean visible, boolean accessible, int x, int y, int width, int height, int index, Object placeholderValue, List<XCUIElement> children, Object value, String name, String label) {
        super(type, enabled, visible, accessible, x, y, width, height, index, placeholderValue, children);
        this.value = value;
        this.name = name;
        this.label = label;
    }

    public ElementStaticText() {
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
