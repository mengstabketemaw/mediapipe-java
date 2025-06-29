package dev;

import java.util.*;

public class FaceRecognizer {
    HashMap<String, List<float[]>> faces = new HashMap<>();
    EmbeddingExtractor embeddingExtractor = new EmbeddingExtractor();

    public byte[] getNormalizedImage(byte[] image) {
        return embeddingExtractor.getNormalizedImage(image);
    }

    public static float cosineSimilarity(float[] vec1, float[] vec2) {
        if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectors must be the same length");

        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normA += vec1[i] * vec1[i];
            normB += vec2[i] * vec2[i];
        }

        if (normA == 0 || normB == 0) return 0.0f; // can't divide by zero (zero vector), consider as no similarity

        float v = (float) (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
        System.out.println(v);
        return v;
    }

    public Optional<String> detectAndClassify(byte[] imageBytes) {
        try {
            String modelPath = "/home/mengstab/Desktop/mediapipe/mediapipe/examples/practice/6/model.onnx";
            float[] floats = embeddingExtractor.extractEmbedding(imageBytes, modelPath);
            String groupId = faces
                    .entrySet()
                    .stream()
                    .filter(
                            entry -> entry
                                    .getValue()
                                    .stream()
                                    .limit(1)
                                    .anyMatch(target -> cosineSimilarity(target, floats) >= 0.70)
                    )
                    .findAny()
                    .map(Map.Entry::getKey)
                    .orElse(UUID.randomUUID().toString());
            List<float[]> similarFaces = faces.getOrDefault(groupId, new ArrayList<>());
            similarFaces.add(floats);
            faces.put(groupId, similarFaces);
            return Optional.of(groupId);
        } catch (Exception e) {
            return Optional.empty();
        }

    }
}
