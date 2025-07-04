package com.example.projet.filter;

import javafx.scene.image.*;

import java.security.MessageDigest;
import java.util.*;

public class ImageSecurity {

    public static Image encrypt(Image image, String password) {
        return shufflePixels(image, password, true);
    }

    public static Image decrypt(Image image, String password) {
        return shufflePixels(image, password, false);
    }

    private static Image shufflePixels(Image image, String password, boolean isEncrypt) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int totalPixels = width * height;

        PixelReader reader = image.getPixelReader();
        WritableImage outputImage = new WritableImage(width, height);
        PixelWriter writer = outputImage.getPixelWriter();

        List<Integer> mapping = generateShuffledIndices(password, totalPixels);
        int[] inverseMap = null;

        if (!isEncrypt) {
            // Crée une table de correspondance inverse une seule fois
            inverseMap = new int[totalPixels];
            for (int i = 0; i < totalPixels; i++) {
                inverseMap[mapping.get(i)] = i;
            }
        }

        for (int i = 0; i < totalPixels; i++) {
            int srcX = i % width;
            int srcY = i / width;

            int destIndex = isEncrypt ? mapping.get(i) : inverseMap[i];
            int destX = destIndex % width;
            int destY = destIndex / width;

            writer.setArgb(destX, destY, reader.getArgb(srcX, srcY));
        }

        return outputImage;
    }

    //fabrique un ordre aleatoire de pixels
    private static List<Integer> generateShuffledIndices(String password, int total) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            indices.add(i);
        }

        long seed = hashPasswordToSeed(password);
        Collections.shuffle(indices, new Random(seed));
        return indices;
    }

    private static long hashPasswordToSeed(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            long seed = 0;
            for (int i = 0; i < Math.min(8, hash.length); i++) {
                seed = (seed << 8) | (hash[i] & 0xff);
            }
            return seed;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }
}
