package dev;

public class EmbeddingExtractor {

    static {
        System.loadLibrary("6");
    }
    public native float[] extractEmbedding(byte[] image, String modelPath, String graphPath);
}