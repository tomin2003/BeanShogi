package com.beanshogi.io;

import com.beanshogi.core.pieces.Piece;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Custom Gson adapter to handle polymorphic Piece serialization/deserialization.
 * Saves the concrete class type so we can recreate the correct piece type when loading.
 */
public class PieceTypeAdapter implements JsonSerializer<Piece>, JsonDeserializer<Piece> {
    
    private static final String CLASS_TYPE = "pieceType";
    
    @Override
    public JsonElement serialize(Piece src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        // Save the actual class name
        result.addProperty(CLASS_TYPE, src.getClass().getName());
        
        // Serialize all fields
        JsonElement fields = context.serialize(src, src.getClass());
        if (fields.isJsonObject()) {
            for (var entry : fields.getAsJsonObject().entrySet()) {
                result.add(entry.getKey(), entry.getValue());
            }
        }
        
        return result;
    }
    
    @Override
    public Piece deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String className = jsonObject.get(CLASS_TYPE).getAsString();
        
        try {
            Class<?> clazz = Class.forName(className);
            return context.deserialize(json, clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown piece class: " + className, e);
        }
    }
}
