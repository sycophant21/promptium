package com.jadt.domain.pageSource;

import java.util.List;

public class ElementCollectionView extends XCUIElement {
    private String name;

    public ElementCollectionView(String type, boolean enabled, boolean visible, boolean accessible, int x, int y, int width, int height, int index, Object placeholderValue, List<XCUIElement> children, String name) {
        super(type, enabled, visible, accessible, x, y, width, height, index, placeholderValue, children);
        this.name = name;
    }

    public ElementCollectionView() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
