package com.jadt.util;

import com.jadt.domain.pageSource.AppiumAUT;
import com.jadt.domain.pageSource.ElementApplication;
import com.jadt.domain.pageSource.ElementButton;
import com.jadt.domain.pageSource.ElementCell;
import com.jadt.domain.pageSource.ElementCollectionView;
import com.jadt.domain.pageSource.ElementImage;
import com.jadt.domain.pageSource.ElementOther;
import com.jadt.domain.pageSource.ElementStaticText;
import com.jadt.domain.pageSource.ElementWindow;
import com.jadt.domain.pageSource.PageSource;
import com.jadt.domain.pageSource.XCUIElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLParser {
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static final DocumentBuilder builder;

    static {
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static PageSource parsePageSourceXML(String pageSourceXml) throws Exception {
        String xml = pageSourceXml.replaceAll("\n", "");
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
        Element root = doc.getDocumentElement();
        ElementApplication elementApplication = (ElementApplication) iterateChildren(root.getChildNodes()).removeFirst();
        List<XCUIElement> elements = elementApplication.getChildren();
        List<ElementWindow> windows = new ArrayList<>();
        for (XCUIElement window : elements) {
            windows.add((ElementWindow) window);
        }
        elementApplication.setWindows(windows);
        elementApplication.setChildren(null);
        AppiumAUT appiumAUT = new AppiumAUT(elementApplication);
        return new PageSource(appiumAUT);
    }

    private static List<XCUIElement> iterateChildren(NodeList children) throws Exception {
        List<XCUIElement> elements = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            String value = node.getNodeValue();
            XCUIElement element;
            if (value == null) {
                element = getElement(node.getNodeName(), getAttributesAsMap(node.getAttributes()));
                element.setChildren(iterateChildren(node.getChildNodes()));
                elements.add(element);
            }
        }
        return elements;
    }

    private static Map<String, Object> getAttributesAsMap(NamedNodeMap attributes) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node node = attributes.item(i);
            result.put(node.getNodeName(), parseSmart(node.getNodeValue()));
        }
        return result;
    }

    private static XCUIElement getElement(String name, Map<String, Object> attributes) throws Exception {
        if (name.equalsIgnoreCase("XCUIElementTypeButton")) {
            return mapToObject(attributes, ElementButton.class);
        } else if (name.equalsIgnoreCase("XCUIElementTypeCell")) {
            return mapToObject(attributes, ElementCell.class);
        } else if (name.equalsIgnoreCase("XCUIElementTypeCollectionView")) {
            return mapToObject(attributes, ElementCollectionView.class);
        } else if (name.equalsIgnoreCase("XCUIElementTypeImage")) {
            return mapToObject(attributes, ElementImage.class);
        } else if (name.equalsIgnoreCase("XCUIElementTypeOther")) {
            return mapToObject(attributes, ElementOther.class);
        } else if (name.equalsIgnoreCase("XCUIElementTypeStaticText")) {
            return mapToObject(attributes, ElementStaticText.class);
        } else if (name.equalsIgnoreCase("XCUIElementTypeWindow")) {
            return mapToObject(attributes, ElementWindow.class);
        } else if (name.equalsIgnoreCase("XCUIElementTypeApplication")) {
            return mapToObject(attributes, ElementApplication.class);
        } else {
            return new XCUIElement() {
            };
        }
    }

    public static <T> T mapToObject(Map<String, Object> data, Class<T> clazz) throws Exception {
        T obj = clazz.getDeclaredConstructor().newInstance();

        Class<?> current = clazz;
        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                String name = field.getName();
                if (data.containsKey(name)) {
                    field.setAccessible(true);
                    Object value = data.get(name);
                    if (obj instanceof ElementStaticText && (name.equalsIgnoreCase("name") || name.equalsIgnoreCase("label"))) {
                        value = value.toString();
                    }
                    current.getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), field.getType()).invoke(obj, value);
                    //field.set(obj, value);
                }
                //System.out.println();
            }
            current = current.getSuperclass(); // go up the hierarchy
        }

        return obj;
    }

    public static Object parseSmart(String input) {
        if (input == null) return null;

        String trimmed = input.trim().toLowerCase();
        if (trimmed.equals("true") || trimmed.equals("false")) {
            return Boolean.parseBoolean(trimmed);
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException _) {
        }
        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException _) {
        }
        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException _) {
        }
        return input;
    }
}
