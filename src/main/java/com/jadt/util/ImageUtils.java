package com.jadt.util;

import com.jadt.domain.image.BoundingBox;
import com.jadt.domain.image.Size;
//import com.jadt.ui.Frame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ImageUtils {
    public static byte[] drawBoundingBoxes(byte[] originalImage, List<BoundingBox> boundingBoxes, Size screenSize) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(originalImage));
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage overlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D draw = overlay.createGraphics();
        draw.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        draw.setFont(new Font("Arial", Font.BOLD, 20)); // Set font for labels

        int labelIndex = 0; // Start from label 0
        for (BoundingBox box : boundingBoxes) {
            int x = box.x();
            int y = box.y();
            int boxWidth = box.width();
            int boxHeight = box.height();

            int scaledX = (int) (x * screenSize.pixelRatioX());
            int scaledY = (int) (y * screenSize.pixelRatioY());
            int scaledWidth = (int) (boxWidth * screenSize.pixelRatioX());
            int scaledHeight = (int) (boxHeight * screenSize.pixelRatioX());

            Color boxColor = generatePleasingColor();
            draw.setColor(boxColor);
            draw.setStroke(new BasicStroke(5));
            draw.drawRect(scaledX, scaledY, scaledWidth, scaledHeight);

            // Draw label above the box
            String label = String.valueOf(labelIndex);
            draw.drawString(label, scaledX, scaledY - 5); // 5 pixels above the box
            labelIndex++;
        }
        draw.dispose();

        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.drawImage(overlay, 0, 0, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(finalImage, "png", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        return drawGuidelinesWithLabels(imageBytes, 50);
    }

    public static Color generatePleasingColor() {
        Random random = new Random();
        float h = random.nextFloat();                          // Hue: 0.0 to 1.0
        float s = 0.5f + random.nextFloat() / 2.0f;            // Saturation: 0.5 to 1.0
        float l = 0.4f + random.nextFloat() / 5.0f;            // Lightness: 0.4 to 0.6

        return hslToRgb(h, s, l);
    }

    public static byte[] drawGuidelinesWithLabels(byte[] imageData, int spacing) {
        try {
            Color lineColor = new Color(240, 240, 240, 128);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            int width = image.getWidth();
            int height = image.getHeight();

            // Create a graphics object with alpha support
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(lineColor);
            g2d.setStroke(new BasicStroke(1));

            // Use anti-aliasing for better font rendering
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font font;
            try {
                font = new Font("Arial", Font.PLAIN, 18);
            } catch (Exception e) {
                font = UIManager.getDefaults().getFont("Label.font");
            }
            g2d.setFont(font);

            for (int x = 0; x < width; x += spacing) {
                g2d.drawLine(x, 0, x, height);
                double luminance = getAverageLuminance(image, x + 5, 5, 5);
                g2d.setColor(luminance > 128 ? Color.BLACK : Color.WHITE);
                g2d.drawString(String.valueOf(x), x + 2, 15);
                g2d.setColor(lineColor);  // Reset line color
            }

            for (int y = 0; y < height; y += spacing) {
                g2d.drawLine(0, y, width, y);
                double luminance = getAverageLuminance(image, 5, y + 5, 5);
                g2d.setColor(luminance > 128 ? Color.BLACK : Color.WHITE);
                g2d.drawString(String.valueOf(y), 2, y + 15);
                g2d.setColor(lineColor);
            }

            g2d.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            System.out.println("Unable to draw guidelines on the given image");
            //Frame.logMessage("Unable to draw guidelines on the given image");
            return new byte[0];
        }
    }

    public static double getAverageLuminance(BufferedImage img, int x, int y, int boxSize) {
        double sum = 0.0;
        int count = 0;
        int halfBox = boxSize / 2;

        for (int dx = -halfBox; dx <= halfBox; dx++) {
            for (int dy = -halfBox; dy <= halfBox; dy++) {
                int px = Math.min(Math.max(x + dx, 0), img.getWidth() - 1);
                int py = Math.min(Math.max(y + dy, 0), img.getHeight() - 1);

                int rgb = img.getRGB(px, py);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                double luminance = 0.299 * r + 0.587 * g + 0.114 * b;
                sum += luminance;
                count++;
            }
        }

        return sum / count;
    }


    private static Color hslToRgb(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h * 6) % 2 - 1));
        float m = l - c / 2;

        float r = 0, g = 0, b = 0;
        float hSegment = h * 6;

        if (0 <= hSegment && hSegment < 1) {
            r = c;
            g = x;
            b = 0;
        } else if (1 <= hSegment && hSegment < 2) {
            r = x;
            g = c;
            b = 0;
        } else if (2 <= hSegment && hSegment < 3) {
            r = 0;
            g = c;
            b = x;
        } else if (3 <= hSegment && hSegment < 4) {
            r = 0;
            g = x;
            b = c;
        } else if (4 <= hSegment && hSegment < 5) {
            r = x;
            g = 0;
            b = c;
        } else if (5 <= hSegment && hSegment < 6) {
            r = c;
            g = 0;
            b = x;
        }

        int red = Math.round((r + m) * 255);
        int green = Math.round((g + m) * 255);
        int blue = Math.round((b + m) * 255);

        return new Color(red, green, blue);
    }

}
