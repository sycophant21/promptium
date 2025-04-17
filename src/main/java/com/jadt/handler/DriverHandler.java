package com.jadt.handler;

import com.jadt.domain.image.Size;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@SuppressWarnings("unused")
public class DriverHandler {
    private final String screenshotPath;
    private final AppiumDriver appiumDriver;
    private Size screenSize;


    public DriverHandler(AppiumDriver appiumDriver) {
        this.appiumDriver = appiumDriver;
        screenshotPath = "ios_screenshot";
    }

    @SuppressWarnings({"all"})
    public void init() {
        Object object = appiumDriver.executeScript("mobile: viewportRect");
        Dimension dimension = appiumDriver.manage().window().getSize();
        if (object instanceof Map<?, ?> tempMap) {
            Map<String, Long> map = (Map<String, Long>) tempMap;
            long height = map.getOrDefault("height", 0L) + map.getOrDefault("top", 0L);
            long width = map.getOrDefault("width", 0L) + map.getOrDefault("left", 0L);
            long pixelRatioX = width / dimension.getWidth();
            long pixelRatioY = height / dimension.getHeight();
            screenSize = new Size(height, width, pixelRatioX, pixelRatioY);
            if (screenSize.height() == 0 || screenSize.width() == 0) {
                throw new RuntimeException("Unable to fetch screen size");
            }
        } else {
            throw new RuntimeException("Unable to fetch screen size");
        }
    }

    // Screenshot Handler Start

    public String captureScreenshotAsBase64() {
        //byte[] imageBytes = Base64.getDecoder().decode(imageData);
        //saveImageOnDisk(ImageIO.read(new ByteArrayInputStream(imageBytes)));
        return appiumDriver.getScreenshotAs(OutputType.BASE64);
    }

    public File captureScreenshotAsFile() throws Exception {
        File file = appiumDriver.getScreenshotAs(OutputType.FILE);
        String savedFilePath = saveImageOnDisk(ImageIO.read(file));
        return new File(savedFilePath);
    }

    public byte[] captureScreenshotAsByteArray() throws Exception {
        byte[] imageBytes = appiumDriver.getScreenshotAs(OutputType.BYTES);
        saveImageOnDisk(ImageIO.read(new ByteArrayInputStream(imageBytes)));
        return imageBytes;
    }

    private String saveImageOnDisk(BufferedImage image) {
        try {
            ImageIO.write(image, "png", new File(System.getProperty("java.io.tmpdir") + "/" + screenshotPath + LocalDateTime.now() + ".png"));
            return System.getProperty("java.io.tmpdir") + "/" + screenshotPath + LocalDateTime.now() + ".png";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Screenshot Handler End


    // Screen Input Handler Start

    public void tapOnCoordinates(int x, int y) {
        Actions actions = new Actions(appiumDriver);
        int scaledX = (int) (x / screenSize.pixelRatioX());
        int scaledY = (int) (y / screenSize.pixelRatioY());
        actions.moveToLocation(scaledX, scaledY).click().perform();
    }

    public void doubleTapOnCoordinates(int x, int y) {
        Actions actions = new Actions(appiumDriver);
        int scaledX = (int) (x / screenSize.pixelRatioX());
        int scaledY = (int) (y / screenSize.pixelRatioY());
        actions.moveToLocation(scaledX, scaledY).doubleClick().perform();
    }

    public void multiTapOnCoordinates(int x, int y, int interval, int times) {

        int scaledX = (int) (x / screenSize.pixelRatioX());
        int scaledY = (int) (y / screenSize.pixelRatioY());
        Actions actions = new Actions(appiumDriver);
        for (int i = 0; i < times; i++) {
            actions.moveToLocation(scaledX, scaledY).click().perform();
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void multiTapOnCoordinatesUsingSequence(int x, int y, int interval, int times) {

        int scaledX = (int) (x / screenSize.pixelRatioX());
        int scaledY = (int) (y / screenSize.pixelRatioY());

        PointerInput input = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(input, 1);
        sequence.addAction(input.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), scaledX, scaledY));
        for (int i = 0; i < times; i++) {
            sequence.addAction(input.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(input.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            sequence.addAction(new Pause(input, Duration.ofMillis(interval)));
        }
        appiumDriver.perform(Collections.singletonList(sequence));
    }

    public void multiTapOnCoordinatesUsingW3CActions(int x, int y, int interval, int times) {
        int scaledX = (int) (x / screenSize.pixelRatioX());
        int scaledY = (int) (y / screenSize.pixelRatioY());

        for (int i = 0; i < times; i++) {
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 0);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), scaledX, scaledY));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(new Pause(finger, Duration.ofMillis(interval)));
            appiumDriver.perform(Collections.singletonList(tap));
        }

    }

    public void swipeFromToCoordinates(int x1, int y1, int x2, int y2) {
        int scaledX1 = (int) (x1 / screenSize.pixelRatioX());
        int scaledY1 = (int) (y1 / screenSize.pixelRatioY());
        int scaledX2 = (int) (x2 / screenSize.pixelRatioX());
        int scaledY2 = (int) (y2 / screenSize.pixelRatioY());

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 0);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), scaledX1, scaledY1));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofSeconds(1), PointerInput.Origin.viewport(), scaledX2, scaledY2));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        appiumDriver.perform(Collections.singletonList(swipe));
    }

    public void tapAndSwipeFromToCoordinates(int x1, int y1, int x2, int y2, Integer interval) {
        int scaledX1 = (int) (x1 / screenSize.pixelRatioX());
        int scaledY1 = (int) (y1 / screenSize.pixelRatioY());
        int scaledX2 = (int) (x2 / screenSize.pixelRatioX());
        int scaledY2 = (int) (y2 / screenSize.pixelRatioY());

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapAndSwipe = new Sequence(finger, 0);
        tapAndSwipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), scaledX1, scaledY1));
        tapAndSwipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tapAndSwipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        tapAndSwipe.addAction(new Pause(finger, Duration.ofMillis(interval)));
        tapAndSwipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), scaledX1, scaledY1));
        tapAndSwipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tapAndSwipe.addAction(finger.createPointerMove(Duration.ofSeconds(1), PointerInput.Origin.viewport(), scaledX2, scaledY2));
        tapAndSwipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        appiumDriver.perform(Collections.singletonList(tapAndSwipe));
    }

    public void tapAndSwipeFromToCoordinates(int x1, int y1, int x2, int y2, int x3, int y3, Integer interval) {
        int scaledX2 = (int) (x2 / screenSize.pixelRatioX());
        int scaledY2 = (int) (y2 / screenSize.pixelRatioY());
        int scaledX3 = (int) (x3 / screenSize.pixelRatioX());
        int scaledY3 = (int) (y3 / screenSize.pixelRatioY());

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tapAndSwipe = new Sequence(finger, 0);
        tapOnCoordinates(x1, y1);
        tapAndSwipe.addAction(finger.createPointerMove(Duration.ofMillis(interval), PointerInput.Origin.viewport(), scaledX2, scaledY2));
        tapAndSwipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tapAndSwipe.addAction(finger.createPointerMove(Duration.ofSeconds(1), PointerInput.Origin.viewport(), scaledX3, scaledY3));
        tapAndSwipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        appiumDriver.perform(Collections.singletonList(tapAndSwipe));
    }

    // Screen Input Handler End


    // App State Handler Start

    public String getCurrentPageSource() {
        return appiumDriver.getPageSource();
    }

    public Size getScreenSize() {
        return screenSize;
    }

    public void quit() {
        appiumDriver.quit();
    }

    public void implicitWait(int ms) {
        appiumDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(ms));
    }
}
