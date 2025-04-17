package com.jadt.domain.pageSource;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;

import java.util.List;

@SuppressWarnings("unused")
public abstract class XCUIElement {
    @XmlAttribute
    private String type;
    @XmlAttribute
    private boolean enabled;
    @XmlAttribute
    private boolean visible;
    @XmlAttribute
    private boolean accessible;
    @XmlAttribute
    private int x;
    @XmlAttribute
    private int y;
    @XmlAttribute
    private int width;
    @XmlAttribute
    private int height;
    @XmlAttribute
    private int index;
    @XmlAttribute
    private Object placeholderValue;
    @XmlAnyElement(lax = true)
    private List<XCUIElement> children;

    public XCUIElement(String type, boolean enabled, boolean visible, boolean accessible, int x, int y, int width, int height, int index, Object placeholderValue, List<XCUIElement> children) {
        this.type = type;
        this.enabled = enabled;
        this.visible = visible;
        this.accessible = accessible;
        this.x = x * 2;
        this.y = y * 2;
        this.width = width * 2;
        this.height = height * 2;
        this.index = index;
        this.placeholderValue = placeholderValue;
        this.children = children;
    }

    public XCUIElement() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public int getX() {
        return x * 2;
    }

    public void setX(int x) {
        this.x = x * 2;
    }

    public int getY() {
        return y * 2;
    }

    public void setY(int y) {
        this.y = y * 2;
    }

    public int getWidth() {
        return width * 2;
    }

    public void setWidth(int width) {
        this.width = width * 2;
    }

    public int getHeight() {
        return height * 2;
    }

    public void setHeight(int height) {
        this.height = height * 2;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Object getPlaceholderValue() {
        return placeholderValue;
    }

    public void setPlaceholderValue(Object placeholderValue) {
        this.placeholderValue = placeholderValue;
    }

    public List<XCUIElement> getChildren() {
        return children;
    }

    public void setChildren(List<XCUIElement> children) {
        this.children = children;
    }
}
