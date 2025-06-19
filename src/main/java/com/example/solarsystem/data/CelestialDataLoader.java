package com.example.solarsystem.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CelestialDataLoader {
    private static final Logger LOGGER = Logger.getLogger(CelestialDataLoader.class.getName());
    private static final String DEFAULT_DATA_PATH = "data/planet_data.json";
    private static final String USER_DATA_PATH = "user/planet_data.json";

    public static class CelestialData {
        public String version;
        public List<CelestialBodyData> celestialBodies;
    }

    public static class CelestialBodyData {
        public String id;
        public String name;
        public String type;
        public double mass;
        public double radius;
        public OrbitData orbit;
        public RotationData rotation;
        public String texture;
        public String color;
    }

    public static class OrbitData {
        public double semiMajorAxis;
        public double eccentricity;
        public double inclination;
        public double period;
        public double initialAngle;
        public double radius;
    }

    public static class RotationData {
        public double period;
        public AxisData axis;
    }

    public static class AxisData {
        public double x;
        public double y;
        public double z;
    }

    public static CelestialData loadData() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        CelestialData data = null;

        // ユーザーデータを優先的に読み込む
        try {
            data = loadFromFile(USER_DATA_PATH, gson);
            LOGGER.info("ユーザーデータを読み込みました: " + USER_DATA_PATH);
        } catch (IOException e) {
            LOGGER.warning("ユーザーデータの読み込みに失敗しました: " + e.getMessage());
        }

        // ユーザーデータが読み込めない場合はデフォルトデータを使用
        if (data == null) {
            try {
                data = loadFromFile(DEFAULT_DATA_PATH, gson);
                LOGGER.info("デフォルトデータを読み込みました: " + DEFAULT_DATA_PATH);
            } catch (IOException e) {
                LOGGER.severe("デフォルトデータの読み込みに失敗しました: " + e.getMessage());
                throw new RuntimeException("天体データの読み込みに失敗しました", e);
            }
        }

        validateData(data);
        return data;
    }

    private static CelestialData loadFromFile(String path, Gson gson) throws IOException {
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            throw new IOException("ファイルが存在しません: " + path);
        }

        try (FileReader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, CelestialData.class);
        }
    }

    private static void validateData(CelestialData data) {
        if (data == null || data.celestialBodies == null) {
            throw new RuntimeException("無効なデータ形式です");
        }

        for (CelestialBodyData body : data.celestialBodies) {
            if (body.id == null || body.name == null || body.type == null) {
                throw new RuntimeException("必須フィールドが不足しています: " + body.name);
            }
            if (body.orbit == null) {
                throw new RuntimeException("軌道データが不足しています: " + body.name);
            }
        }
    }
} 