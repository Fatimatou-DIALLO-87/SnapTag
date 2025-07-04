package com.example.projet.filter;


public class ImageViewState {
    public final double rotation;
    public final double scaleX;
    public final double scaleY;

    public ImageViewState(double rotation, double scaleX, double scaleY) {
        this.rotation = rotation;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }


    public double getRotationAngle() {
        return rotation;
    }

    public double getSymetryScaleX() {
        return scaleX;
    }

    public double getSymetryScaleY() {
        return scaleY;
    }
}
