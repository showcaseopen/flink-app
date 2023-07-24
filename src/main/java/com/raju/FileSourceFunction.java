package com.raju;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JavaType;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class FileSourceFunction<T> implements SourceFunction<T> {
    private final String filePath;
    private final Class<T> type;

    public FileSourceFunction(String filePath, Class<T> type) {
        this.filePath = filePath;
        this.type = type;
    }

    @Override
    public void run(SourceContext<T> ctx) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
        List<T> data = objectMapper.readValue(new File(filePath), javaType);


        for (T item : data) {
            ctx.collect(item);
        }
    }

    @Override
    public void cancel() {
        // handle cancellation if necessary
    }
}
