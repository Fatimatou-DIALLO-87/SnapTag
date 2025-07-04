package com.example.projet.filter;

import javafx.scene.paint.Color;

public class GrayScaleFilter extends AbstractFilter {
    @Override
    protected Color transform(Color color) {
        double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
        return Color.gray(gray);
    }
}
