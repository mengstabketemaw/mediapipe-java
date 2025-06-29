package dev;

public class EmbeddingExtractor {

    static {
        System.loadLibrary("6");
    }
    public native float[] extractEmbedding(byte[] image, String modelPath);
    public native byte[] getNormalizedImage(byte[] image);
}