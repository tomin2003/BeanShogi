package com.beanshogi.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.beanshogi.model.Piece;

import java.io.*;

public class Serialize {
    
    /**
     * Generic wrapper class to add metadata header to saved data
     */
    @SuppressWarnings("unused")
    private static class SaveData<T> {
        public String appName = "BeanShogi";
        public String version = "1.0";
        public T data;
        
        public SaveData(T data) {
            this.data = data;
        }
    }
    
    private static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Piece.class, new PieceTypeAdapter())
                .create();
    }
    
    /**
     * Save an object to a JSON file with metadata header.
     * @param data The object to save
     * @param filepath The file path where to save
     */
    protected static <T> void save(T data, String filepath) {
        Gson gson = createGson();
        try {
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                SaveData<T> saveData = new SaveData<>(data);
                gson.toJson(saveData, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save to " + filepath, e);
        }
    }
    
    /**
     * Load an object from a JSON file.
     * @param filepath The file path to load from
     * @param classType The class type to deserialize to
     * @return The loaded object
     */
    protected static <T> T load(String filepath, Class<T> classType) {
        Gson gson = createGson();
        
        try (FileReader reader = new FileReader(filepath)) {
            // Use a wrapper type to extract the data field
            SaveData<?> saveData = gson.fromJson(reader, SaveData.class);
            
            if (saveData == null || saveData.data == null) {
                throw new RuntimeException("Failed to load from " + filepath);
            }
            
            // Convert the data field to the correct type
            String json = gson.toJson(saveData.data);
            return gson.fromJson(json, classType);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load from " + filepath, e);
        }
    }
}


