package com.jadt.domain.pageSource;

import java.util.List;

public class ElementCell extends XCUIElement {

    public ElementCell(String type, boolean enabled, boolean visible, boolean accessible, int x, int y, int width, int height, int index, Object placeholderValue, List<XCUIElement> children) {
        super(type, enabled, visible, accessible, x, y, width, height, index, placeholderValue, children);
    }

    public ElementCell() {
    }
}
