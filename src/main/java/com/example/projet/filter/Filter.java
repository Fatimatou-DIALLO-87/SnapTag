package com.example.projet.filter;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;


public interface Filter {
    WritableImage apply(Image inputImage);
}