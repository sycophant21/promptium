package com.jadt.util;

import com.jadt.domain.image.BoundingBox;
import com.jadt.domain.pageSource.PageSource;
import com.jadt.domain.pageSource.XCUIElement;

import java.util.ArrayList;
import java.util.List;

public class PageSourceUtil {

    public static List<BoundingBox> getBoundingBoxes(PageSource pageSource) {
        List<BoundingBox> boundingBoxes = new ArrayList<>();
        pageSource.appiumAUT().getApplication().getWindows().forEach(k -> getBoundingBoxesRecursively(k, boundingBoxes));
        return boundingBoxes;
    }

    private static void getBoundingBoxesRecursively(XCUIElement element, List<BoundingBox> boundingBoxes) {
        for (XCUIElement el : element.getChildren()) {
            //if (el.isVisible()) {
                boundingBoxes.add(new BoundingBox(el.getX() + 10, el.getY() + 10, el.getHeight() - 10, el.getWidth() - 10));
            //}
            getBoundingBoxesRecursively(el, boundingBoxes);
        }
    }
}
