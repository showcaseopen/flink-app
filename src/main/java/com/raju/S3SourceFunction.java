package com.raju;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class S3SourceFunction<T> implements SourceFunction<T> {
    private final String bucketName;
    private final String key;
    private final Class<T> type;

    public S3SourceFunction(String bucketName, String key, Class<T> type) {
        this.bucketName = bucketName;
        this.key = key;
        this.type = type;
    }

    @Override
    public void run(SourceContext<T> ctx) throws Exception {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key));

        BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
        ObjectMapper objectMapper = new ObjectMapper();
        String line;
        while ((line = reader.readLine()) != null) {
            T item = objectMapper.readValue(line, type);
            ctx.collect(item);
        }
    }

    @Override
    public void cancel() {
        // handle cancellation if necessary
    }
}
