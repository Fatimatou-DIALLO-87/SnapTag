package com.example.projet.filter;

import javafx.scene.paint.Color;

public class SwapRGBFilter extends AbstractFilter {
    @Override
    protected Color transform(Color color) {
        return Color.color(color.getBlue(), color.getRed(), color.getGreen());
    }
}