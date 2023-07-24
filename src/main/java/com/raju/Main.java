package com.raju;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static boolean useFile = true;

    public static void main(String[] args) throws Exception {

        ParameterTool params = ParameterTool.fromPropertiesFile(
                Files.newInputStream(Paths.get("/config.properties"))
//                Main.class.getResourceAsStream("config_local.properties")
        );

        String inputPath = params.getRequired("input");
        String outputPath = params.getRequired("output");


        // Get the execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        SourceFunction<MyPojoInput> source;
        if (inputPath.startsWith("s3://")) {
            source = new S3SourceFunction<>(inputPath, "", MyPojoInput.class);
        } else {
            source = new FileSourceFunction<>(inputPath, MyPojoInput.class);
        }

        SinkFunction<MyPojoOutput> sink;
        if (outputPath.startsWith("s3://")) {
            sink = new S3SinkFunction<>(outputPath, "");
        } else {
            sink = new FileSinkFunction<>(outputPath);
        }


        DataStream<MyPojoInput> inputStream = env.addSource(source).returns(TypeInformation.of(MyPojoInput.class));

        // Transform the input data
        DataStream<MyPojoOutput> transformedStream = inputStream
                .map((MapFunction<MyPojoInput, MyPojoOutput>) Main::getMyPojoOutput);

        transformedStream.addSink(sink);

        env.execute("Flink Streaming Job");
    }

    private static MyPojoOutput getMyPojoOutput(MyPojoInput input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode docDataJson = objectMapper.readTree(input.get__doc_data());
        TransformationResult transformationResult = Transformer.transform(docDataJson);

        MyPojoOutput output = new MyPojoOutput();
        output.set_timestamp(input.get_timestamp());
        output.set__doc_data(transformationResult.getTransformedJson().toString());
        output.setSuccess(transformationResult.isSuccess());

        return output;
    }

}
