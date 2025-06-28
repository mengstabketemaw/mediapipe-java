package dev;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class Main {


    public static void main(String[] args) throws Throwable {
        EmbeddingExtractor embeddingExtractor = new EmbeddingExtractor();
        String modelPath = "/home/mengstab/IdeaProjects/java-native-code/src/main/resources/model.onnx";
        String graphPath = "./";

        File file = new File("/home/mengstab/IdeaProjects/java-native-code/src/main/resources/me.jpg");
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] imageByte = inputStream.readAllBytes();
            float[] floats = embeddingExtractor.extractEmbedding(imageByte, modelPath, graphPath);
            System.out.println(Arrays.toString(floats));
        }



    }
}