package com.raju;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class S3SinkFunction<T> implements SinkFunction<T> {
    private final String bucketName;
    private final String key;
    private final File tempFile;

    public S3SinkFunction(String bucketName, String key) throws IOException {
        this.bucketName = bucketName;
        this.key = key;
        this.tempFile = File.createTempFile("flink", "tmp");
    }

    @Override
    public void invoke(T value, Context context) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(value);

        try (FileWriter writer = new FileWriter(tempFile, true)) {
            writer.append(jsonString).append("\n");
        }
    }

    public void close() throws Exception {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        s3Client.putObject(new PutObjectRequest(bucketName, key, tempFile));
    }
}
