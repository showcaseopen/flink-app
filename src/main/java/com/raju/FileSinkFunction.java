package com.raju;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;

public class FileSinkFunction<T> implements SinkFunction<T> {
    private final String filePath;

    public FileSinkFunction(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void invoke(T value, Context context) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(value);

        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.append(jsonString).append("\n");
        }
    }
}
