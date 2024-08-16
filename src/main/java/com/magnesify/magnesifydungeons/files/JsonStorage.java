package com.magnesify.magnesifydungeons.files;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

public class JsonStorage {

    private File file;

    public JsonStorage(String filePath) {
        this.file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
                JSONObject players_config = new JSONObject();
                players_config.put("json_config_version", "1");
                createJsonFile(players_config);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createJsonFile(JSONObject jsonObject) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonObject.toString(4)); // JSON nesnesini yaz
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeData(JSONObject jsonObject) {
        synchronized (this) {
            try (FileWriter fileWriter = new FileWriter(file, false)) {
                fileWriter.write(jsonObject.toString(4));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject readData() {
        JSONObject jsonObject = null;
        try (FileReader fileReader = new FileReader(file)) {
            JSONTokener tokener = new JSONTokener(fileReader);
            jsonObject = new JSONObject(tokener);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public double getDoubleValue(String key) {
        JSONObject jsonObject = readData();
        if (jsonObject != null && jsonObject.has(key)) {
            Object value = jsonObject.get(key);
            if (value instanceof BigDecimal) {
                return ((BigDecimal) value).doubleValue();
            } else if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else {
                throw new ClassCastException("Value is not a valid number");
            }
        }
        throw new ClassCastException("Key not found in JSON");
    }

    public Object getValue(String key) {
        JSONObject jsonObject = readData();
        if (jsonObject != null && jsonObject.has(key)) {
            return jsonObject.get(key);
        }
        return null;
    }

    public void updateData(String key, Object newValue) {
        try {
            JSONObject jsonObject = readData();
            if (jsonObject != null) {
                jsonObject.put(key, newValue);
                createJsonFile(jsonObject); // Güncellenmiş JSON'u tekrar yaz
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
