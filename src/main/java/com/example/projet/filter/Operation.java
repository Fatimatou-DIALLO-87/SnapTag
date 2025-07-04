package com.example.projet.filter;

import javafx.scene.image.Image;

public class Operation {
    public enum Type { FILTER, TRANSFORM }

    private Type type;
    private Image imageBefore;
    private ImageViewState viewStateBefore;
    private String transformationName;

    public Operation(Type type, Image imageBefore, ImageViewState viewStateBefore, String transformationName) {
        this.type = type;
        this.imageBefore = imageBefore;
        this.viewStateBefore = viewStateBefore;
        this.transformationName = transformationName;
    }

    public Type getType() {
        return type;
    }
    public Image getImageBefore() {
        return imageBefore;
    }
    public ImageViewState getViewStateBefore() {
        return viewStateBefore;
    }
    public String getTransformationName() {
        return transformationName;
    }
}
