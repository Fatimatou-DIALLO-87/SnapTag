package com.example.projet.filter;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SobelFilter implements Filter {

    private static final int[][] GX = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
    };

    private static final int[][] GY = {
            {-1, -2, -1},
            { 0,  0,  0},
            { 1,  2,  1}
    };

    @Override
    public WritableImage apply(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();
        WritableImage output = new WritableImage(width, height);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double gx = 0, gy = 0;
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        Color c = reader.getColor(x + j, y + i);
                        double gray = (c.getRed() + c.getGreen() + c.getBlue()) / 3.0;
                        gx += GX[i + 1][j + 1] * gray;
                        gy += GY[i + 1][j + 1] * gray;
                    }
                }
                double g = Math.min(1.0, Math.sqrt(gx * gx + gy * gy));
                writer.setColor(x, y, new Color(g, g, g, 1.0));
            }
        }
        return output;
    }
}
