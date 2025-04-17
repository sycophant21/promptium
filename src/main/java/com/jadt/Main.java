package com.jadt;

import com.jadt.handler.ActionHandler;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {
    private static String lastImage;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        ActionHandler actionHandler = new ActionHandler();
        McpSyncServer server = McpServer.sync(new StdioServerTransportProvider(new com.fasterxml.jackson.databind.ObjectMapper())).serverInfo("my-mcp-server-java", "0.0.1").capabilities(McpSchema.ServerCapabilities.builder().resources(true, true).tools(true).logging().build()).build();
        server.addTool(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "connect",
                                "Makes a connection to a real physical ios device, no configurations required, everything is preset, returns success in case of success and an error string in case of failure",
                                new McpSchema.JsonSchema(
                                        "object",
                                        Map.of(),
                                        Collections.emptyList(),
                                        false
                                )
                        ),
                        (_, _) -> {
                            List<McpSchema.Content> response = new ArrayList<>();
                            Map<String, Object> result = actionHandler.connectDriver();
                            boolean isError;
                            boolean connectionResult = Boolean.parseBoolean(result.get("result").toString());
                            if (connectionResult) {
                                response.add(new McpSchema.TextContent("Connection made successfully"));
                                isError = false;
                            } else {
                                response.add(new McpSchema.TextContent("Connection failure"));
                                response.add(new McpSchema.TextContent(result.get("error").toString()));
                                isError = true;
                            }
                            return new McpSchema.CallToolResult(response, isError);
                        }));
        server.addTool(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "captureScreenshot",
                                "Captures the current state of the device as a png image and returns the png image as a base64 string.",
                                new McpSchema.JsonSchema(
                                        "object",
                                        Map.of(),
                                        Collections.emptyList(),
                                        false
                                )
                        ),
                        (_, _) -> {
                            List<McpSchema.Content> response = new ArrayList<>();
                            boolean isError = takeScreenshot(actionHandler, response);
                            return new McpSchema.CallToolResult(response, isError);
                        }));
        server.addTool(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "getPageSource",
                                "Captures the snapshot (XML) of the current screen.",
                                new McpSchema.JsonSchema(
                                        "object",
                                        Map.of(),
                                        Collections.emptyList(),
                                        false
                                )
                        ), (_, _) -> {
                    List<McpSchema.Content> response = new ArrayList<>();
                    boolean isError;
                    try {
                        String result = actionHandler.getCurrentPageSourceRaw();
                        response.add(new McpSchema.TextContent(result));
                        isError = false;
                    } catch (Exception e) {
                        response.add(new McpSchema.TextContent("Unable to capture and store the page source"));
                        response.add(new McpSchema.TextContent("error: " + e.getMessage()));
                        isError = true;
                    }
                    return new McpSchema.CallToolResult(response, isError);
                }));
        server.addTool(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "tap",
                                "Taps on the provided coordinates and clicks a screenshot of the state right after tapping and returns the asked object",
                                new McpSchema.JsonSchema(
                                        "object",
                                        Map.of(
                                                "x",
                                                Map.of("type", "integer", "description", "The x-coordinate for the tap action."),
                                                "y",
                                                Map.of("type", "integer", "description", "The y-coordinate for the tap action."),
                                                "required_obj",
                                                Map.of("type", "string", "description", "the required object after the tap, if null, defaults to PAGE_SOURCE", "enum", new String[]{"SCREENSHOT", "PAGE_SOURCE"}, "default", "PAGE_SOURCE")
                                        ),
                                        List.of("x", "y"),
                                        false
                                )
                        ), (_, arguments) -> {
                    List<McpSchema.Content> response = new ArrayList<>();
                    boolean isError;
                    try {
                        String requiredObj = arguments.getOrDefault("required_obj", "PAGE_SOURCE").toString();
                        actionHandler.tapOnCoordinates((Integer) arguments.get("x"), (Integer) arguments.get("y"));
                        if (requiredObj.equalsIgnoreCase("SCREENSHOT")) {
                            isError = takeScreenshot(actionHandler, response);
                        } else {
                            String pageSource = actionHandler.getCurrentPageSourceRaw();
                            response.add(new McpSchema.TextContent(pageSource));
                            isError = false;
                        }

                    } catch (Exception e) {
                        response.clear();
                        response.add(new McpSchema.TextContent("Error: " + e.getMessage()));
                        isError = true;
                    }
                    return new McpSchema.CallToolResult(response, isError);
                }));
        server.addTool(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "double_tap",
                                "Double Taps on the provided coordinates and clicks a screenshot of the state right after tapping and returns the png image as a base64 string",
                                new McpSchema.JsonSchema(
                                        "object",
                                        Map.of(
                                                "x",
                                                Map.of("type", "integer", "description", "The x-coordinate for the tap action."),
                                                "y",
                                                Map.of("type", "integer", "description", "The y-coordinate for the tap action.")
                                        ),
                                        List.of("x", "y"),
                                        false
                                )
                        ), (_, arguments) -> {
                    List<McpSchema.Content> response = new ArrayList<>();
                    boolean isError;
                    try {
                        actionHandler.doubleTapOnCoordinates((Integer) arguments.get("x"), (Integer) arguments.get("y"));
                        isError = takeScreenshot(actionHandler, response);
                    } catch (Exception e) {
                        response.clear();
                        response.add(new McpSchema.TextContent("Error: " + e.getMessage()));
                        isError = true;
                    }
                    return new McpSchema.CallToolResult(response, isError);
                }));
        server.addTool(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "multi_tap",
                                "Taps multiple times on the provided coordinates after the given interval and clicks a screenshot of the state right after tapping and returns the path of the PNG file of the captured screenshot. It can be used to perform actions that require one tap to show element and another to tap the element",
                                new McpSchema.JsonSchema(
                                        "object",
                                        Map.of(
                                                "x",
                                                Map.of("type", "integer", "description", "The x-coordinate for the tap action."),
                                                "y",
                                                Map.of("type", "integer", "description", "The y-coordinate for the tap action."),
                                                "interval",
                                                Map.of("type", "integer", "description", "The interval between each tap"),
                                                "tap_quantity",
                                                Map.of("type", "integer", "description", "Number of times to tap at the coordinate")
                                        ),
                                        List.of("x", "y"),
                                        false
                                )
                        ), (_, arguments) -> {
                    List<McpSchema.Content> response = new ArrayList<>();
                    boolean isError;
                    try {
                        actionHandler.multiTapOnCoordinates((Integer) arguments.get("x"), (Integer) arguments.get("y"), (Integer) arguments.get("interval"), (Integer) arguments.get("tap_quantity"));
                        isError = takeScreenshot(actionHandler, response);
                    } catch (Exception e) {
                        response.clear();
                        response.add(new McpSchema.TextContent("Error: " + e.getMessage()));
                        isError = true;
                    }
                    return new McpSchema.CallToolResult(response, isError);
                }));

        server.addTool(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "swipe",
                                "Performs a swipe gesture across the screen between the provided coordinates, from (x1,y1) to (x2,y2)",
                                new McpSchema.JsonSchema(
                                        "object",
                                        Map.of(
                                                "x1",
                                                Map.of("type", "integer", "description", "The x-coordinate for the origin for the swipe action."),
                                                "y1",
                                                Map.of("type", "integer", "description", "The y-coordinate for the origin point for the swipe action."),
                                                "x2",
                                                Map.of("type", "integer", "description", "The x-coordinate for the destination point for the swipe action."),
                                                "y2",
                                                Map.of("type", "integer", "description", "The y-coordinate for the destination point for the swipe action.")
                                        ),
                                        List.of("x1", "y1", "x2", "y2"),
                                        false
                                )
                        ), (_, arguments) -> {
                    List<McpSchema.Content> response = new ArrayList<>();
                    boolean isError;
                    try {
                        actionHandler.swipeOnCoordinates((Integer) arguments.get("x1"), (Integer) arguments.get("y1"), (Integer) arguments.get("x2"), (Integer) arguments.get("y2"));
                        isError = takeScreenshot(actionHandler, response);
                    } catch (Exception e) {
                        response.clear();
                        response.add(new McpSchema.TextContent("Error: " + e.getMessage()));
                        isError = true;
                    }
                    return new McpSchema.CallToolResult(response, isError);
                }));
        server.addTool(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "tap_and_swipe_with_two_coordinates",
                                "Performs a tap at (x1,y1) then waits for the given interval and then performs a swipe gesture across the screen between the provided coordinates, from (x1,y1) to (x2,y2)",
                                new McpSchema.JsonSchema(
                                        "object",
                                        Map.of(
                                                "x1",
                                                Map.of("type", "integer", "description", "The x-coordinate for the origin for the tap and swipe action."),
                                                "y1",
                                                Map.of("type", "integer", "description", "The y-coordinate for the origin point for the tap and swipe action."),
                                                "x2",
                                                Map.of("type", "integer", "description", "The x-coordinate for the destination point for the swipe action."),
                                                "y2",
                                                Map.of("type", "integer", "description", "The y-coordinate for the destination point for the swipe action."),
                                                "interval",
                                                Map.of("type", "integer", "description", "The interval (in ms) to wait between the tap and swipe gesture, defaults to 1000ms.")
                                        ),
                                        List.of("x1", "y1", "x2", "y2"),
                                        false
                                )
                        ), (_, arguments) -> {
                    List<McpSchema.Content> response = new ArrayList<>();
                    boolean isError;
                    try {
                        actionHandler.tapAndSwipeOnCoordinates((Integer) arguments.get("x1"), (Integer) arguments.get("y1"), (Integer) arguments.get("x2"), (Integer) arguments.get("y2"), (Integer) arguments.getOrDefault("interval", 1000));
                        isError = takeScreenshot(actionHandler, response);
                    } catch (Exception e) {
                        response.clear();
                        response.add(new McpSchema.TextContent("Error: " + e.getMessage()));
                        isError = true;
                    }
                    return new McpSchema.CallToolResult(response, isError);
                }));

        server.addTool(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "tap_and_swipe_with_three_coordinates",
                                "Performs a tap at (x1,y1) then waits for the given interval and then performs a swipe gesture across the screen between the provided coordinates, from (x2,y2) to (x3,y3)",
                                new McpSchema.JsonSchema(
                                        "object",
                                        Map.of(
                                                "x1",
                                                Map.of("type", "integer", "description", "The x-coordinate for the origin for the tap and swipe action."),
                                                "y1",
                                                Map.of("type", "integer", "description", "The y-coordinate for the origin point for the tap and swipe action."),
                                                "x2",
                                                Map.of("type", "integer", "description", "The x-coordinate for the destination point for the swipe action."),
                                                "y2",
                                                Map.of("type", "integer", "description", "The y-coordinate for the destination point for the swipe action."),
                                                "x3",
                                                Map.of("type", "integer", "description", "The x-coordinate for the destination point for the swipe action."),
                                                "y3",
                                                Map.of("type", "integer", "description", "The y-coordinate for the destination point for the swipe action."),
                                                "interval",
                                                Map.of("type", "integer", "description", "The interval (in ms) to wait between the tap and swipe gesture, defaults to 1000ms.")
                                        ),
                                        List.of("x1", "y1", "x2", "y2", "x3", "y3"),
                                        false
                                )
                        ), (_, arguments) -> {
                    List<McpSchema.Content> response = new ArrayList<>();
                    boolean isError;
                    try {
                        actionHandler.tapAndSwipeOnCoordinates((Integer) arguments.get("x1"), (Integer) arguments.get("y1"), (Integer) arguments.get("x2"), (Integer) arguments.get("y2"), (Integer) arguments.get("x3"), (Integer) arguments.get("y3"), (Integer) arguments.getOrDefault("interval", 1000));
                        isError = takeScreenshot(actionHandler, response);
                    } catch (Exception e) {
                        response.clear();
                        response.add(new McpSchema.TextContent("Error: " + e.getMessage()));
                        isError = true;
                    }
                    return new McpSchema.CallToolResult(response, isError);
                }));
        server.addTool(
                new McpServerFeatures.SyncToolSpecification(
                        new McpSchema.Tool(
                                "get_accurate_coordinates",
                                "Gets the most accurate coordinates of an element using a human perceived label of the element of the last captured screenshot, like \"pause_button\", \"toggle\" etc. The coordinates are returned in the form of [x_min, y_min, x_max, y_max]",
                                new McpSchema.JsonSchema(
                                        "object",
                                        Map.of(
                                                "element_label",
                                                Map.of("type", "string", "description", "The human perceived label of the element to be searched")
                                        ),
                                        List.of("element_label"),
                                        false
                                )
                        ), (_, arguments) -> {
                    List<McpSchema.Content> response = new ArrayList<>();
                    boolean isError = false;
                    try {
                        List<Double> coordinates = actionHandler.getAccurateCoordinatesFromMoonDream(lastImage, String.valueOf(arguments.get("element_label")));
                        List<Integer> finalCoordinates = new ArrayList<>();
                        int i = 0;
                        for (Double coordinate : coordinates) {
                            if (coordinate < 0) {
                                isError = true;
                                break;
                            } else {
                                finalCoordinates.add((int) (i % 2 == 0 ? coordinate * 828 : coordinate * 1792));
                                i++;
                            }
                        }
                        if (isError) {
                            response.add(new McpSchema.TextContent("No element found with the given label, Label: " + arguments.get("element_label")));
                        } else {
                            response.add(new McpSchema.TextContent("result : " + finalCoordinates));
                        }
                    } catch (RuntimeException e) {
                        response.clear();
                        response.add(new McpSchema.TextContent("error: " + e.getMessage()));
                        isError = true;
                    }
                    return new McpSchema.CallToolResult(response, isError);
                }));

    }

    private static boolean takeScreenshot(ActionHandler actionHandler, List<McpSchema.Content> response) {
        try {
            String screenshotAsBase64 = actionHandler.takeScreenshotAsBase64();
            boolean isError;
            boolean result = screenshotAsBase64.isBlank();
            if (!result) {
                lastImage = screenshotAsBase64;
                byte[] imageBytes = Base64.getDecoder().decode(screenshotAsBase64);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.fromInputStreams(Collections.singletonList(inputStream)).scale(0.5).outputQuality(0.1).toOutputStream(outputStream);
                byte[] optimizedBytes = outputStream.toByteArray();
                String optimizedBase64 = Base64.getEncoder().encodeToString(optimizedBytes);
                response.add(new McpSchema.TextContent(String.valueOf(optimizedBase64.length())));
                response.add(new McpSchema.ImageContent(List.of(McpSchema.Role.ASSISTANT), 1.0, optimizedBase64, "image/png"));
                isError = false;
            } else {
                response.add(new McpSchema.TextContent("Unable to capture screenshot"));
                isError = true;
            }
            return isError;
        } catch (Exception e) {
            response.clear();
            response.add(new McpSchema.TextContent("Unable to capture screenshot, Error: " + e.getMessage()));
            return true;
        }
    }
}