package com.jadt.domain.pageSource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

public class ElementApplication extends XCUIElement {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String label;
    @XmlElement(name = "XCUIElementTypeWindow")
    private List<ElementWindow> windows;

    public ElementApplication(String type, boolean enabled, boolean visible, boolean accessible, int x, int y, int width, int height, int index, Object placeholderValue, List<XCUIElement> children, String name, String label, List<ElementWindow> windows) {
        super(type, enabled, visible, accessible, x, y, width, height, index, placeholderValue, children);
        this.name = name;
        this.label = label;
        this.windows = windows;
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

    public List<ElementWindow> getWindows() {
        return windows;
    }

    public void setWindows(List<ElementWindow> windows) {
        this.windows = windows;
    }
}
