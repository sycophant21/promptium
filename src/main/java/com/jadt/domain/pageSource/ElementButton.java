package com.jadt.domain.pageSource;

import java.util.List;

public class ElementButton extends XCUIElement {
    private String name;
    private String label;

    public ElementButton(String type, boolean enabled, boolean visible, boolean accessible, int x, int y, int width, int height, int index, Object placeholderValue, List<XCUIElement> children, String name, String label) {
        super(type, enabled, visible, accessible, x, y, width, height, index, placeholderValue, children);
        this.name = name;
        this.label = label;
    }

    public ElementButton() {
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
