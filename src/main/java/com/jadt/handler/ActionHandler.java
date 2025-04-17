package com.jadt.handler;

import com.jadt.builder.DriverOptionsBuilder;
import com.jadt.connector.DriverConnector;
import com.jadt.domain.image.BoundingBox;
import com.jadt.domain.pageSource.PageSource;
import com.jadt.util.ImageUtils;
import com.jadt.util.PageSourceUtil;
import com.jadt.util.XMLParser;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONArray;
import kong.unirest.core.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class ActionHandler {
    private DriverHandler driverHandler;
    private List<BoundingBox> boundingBoxes;

    public ActionHandler() {
    }

    public Map<String, Object> connectDriver() {
        try {
            AppiumDriver driver = new DriverConnector<XCUITestOptions>().initialiseDriver(initDriverOptions());
            driverHandler = new DriverHandler(driver);
            driverHandler.init();
            return Map.of("result", true);
        } catch (URISyntaxException | MalformedURLException e) {
            return Map.of("result", false, "error", e.getMessage());
        }
    }

    private XCUITestOptions initDriverOptions() {
        DriverOptionsBuilder<XCUITestOptions> driverOptionsBuilder = new DriverOptionsBuilder<>();
        driverOptionsBuilder
                .setCapabilities("platformName", "iOS") // Currently only ios is supported
                .setCapabilities("deviceName", "") // can be viewed in Settings > General > About > Name (for physical/real devices)
                .setCapabilities("platformVersion", "") // Current ios version installed on device/simulator
                .setCapabilities("automationName", "XCUITest") //Do not change (test plugin used by Appium for ios)
                .setCapabilities("noReset", true) //Do not change
                .setCapabilities("useNewWDA", false) //"False" -> if WDA already installed, True if not (see: https://appium.github.io/appium-xcuitest-driver/4.24/wda-custom-server/ and https://github.com/appium/appium-xcuitest-driver)
                .setCapabilities("wdaStartupRetries", 10)
                .setCapabilities("wdaStartupRetryInterval", 20000)
                .setCapabilities("udid", "") //UDID of the physical device (found in xcode, see: https://medium.com/@christiegmarie89/6-easy-steps-to-find-unique-device-identifier-udid-on-the-iphone-updated-2023-b9e57b2f3a53)
                .setCapabilities("bundleId", ""); //Bundle Id for the application to run by default, can be kept empty.
        return driverOptionsBuilder.build(new XCUITestOptions());
    }

    public String takeScreenshotAsPng() {
        try {
            File file = driverHandler.captureScreenshotAsFile();
            return file.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] takeScreenshotAsBytes() {
        try {
            byte[] image = driverHandler.captureScreenshotAsByteArray();
            PageSource pageSource = getCurrentPageSource();
            boundingBoxes = PageSourceUtil.getBoundingBoxes(pageSource);
            return ImageUtils.drawBoundingBoxes(image, boundingBoxes, driverHandler.getScreenSize());
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public String takeScreenshotAsBase64() {
        return driverHandler.captureScreenshotAsBase64();
        /*String image = driverHandler.captureScreenshotAsBase64();
        PageSource pageSource = getCurrentPageSource();
        boundingBoxes = PageSourceUtil.getBoundingBoxes(pageSource);
        return Base64.getEncoder().encodeToString(ImageUtils.drawBoundingBoxes(Base64.getDecoder().decode(image), boundingBoxes, driverHandler.getScreenSize()));*/

    }

    public PageSource getCurrentPageSource() throws Exception {
        String pageSourceXML = driverHandler.getCurrentPageSource();
        return XMLParser.parsePageSourceXML(pageSourceXML);
    }

    public String getCurrentPageSourceRaw() {
        return driverHandler.getCurrentPageSource();
    }

    public void tapOnCoordinates(int x, int y) {
        driverHandler.tapOnCoordinates(x, y);
    }

    public void doubleTapOnCoordinates(int x, int y) {
        driverHandler.doubleTapOnCoordinates(x, y);
    }

    public void multiTapOnCoordinates(int x, int y, int timeInterval, int times) throws InterruptedException {
        driverHandler.multiTapOnCoordinatesUsingW3CActions(x, y, timeInterval, times);
    }

    public void swipeOnCoordinates(int x1, int y1, int x2, int y2) {
        driverHandler.swipeFromToCoordinates(x1, y1, x2, y2);
    }

    public void tapAndSwipeOnCoordinates(int x1, int y1, int x2, int y2, Integer interval) {
        driverHandler.tapAndSwipeFromToCoordinates(x1, y1, x2, y2, interval);
    }

    public void tapAndSwipeOnCoordinates(int x1, int y1, int x2, int y2, int x3, int y3, Integer interval) {
        driverHandler.tapAndSwipeFromToCoordinates(x1, y1, x2, y2, x3, y3, interval);
    }

    /// Uses Moondream for getting accurate (relative) coordinates of the element specified
    /// ## Important: Element Label is usually a human perceived label of an element. Example: Pause Button, Video Player, Progress Bar, Image, Heading etc.
    /// Image is a base64 encoded string, currently tested for png images.
    public List<Double> getAccurateCoordinatesFromMoonDream(String image, String elementLabel) {
        JSONArray coordinates = Unirest.post("https://api.moondream.ai/v1/detect").header("Content-Type", "application/json").header("X-Moondream-Auth", System.getProperty("moondream.api.key")).body(Unirest.config().getObjectMapper().writeValue(new MoondreamRequestBody("data:image/png;base64," + image, elementLabel))).asJson().getBody().getObject().getJSONArray("objects");
        JSONObject coordinate = coordinates.getJSONObject(0);
        return List.of(coordinate.getDouble("x_min"), coordinate.getDouble("y_min"), coordinate.getDouble("x_max"), coordinate.getDouble("y_max"));
    }

    /// image_url -> base64 encoded string of the image
    /// object -> the element to be detected
    record MoondreamRequestBody(String image_url, String object) {
    }
}
