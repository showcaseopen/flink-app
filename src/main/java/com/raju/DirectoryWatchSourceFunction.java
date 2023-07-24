package com.raju;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JavaType;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
public class DirectoryWatchSourceFunction<T> extends FileAlterationListenerAdaptor implements SourceFunction<T> {

    private final String directoryPath;
    private final Class<T> type;
    private volatile boolean isRunning = true;
    private SourceContext<T> sourceContext;

    public DirectoryWatchSourceFunction(String directoryPath, Class<T> type) {
        this.directoryPath = directoryPath;
        this.type = type;
    }

    @Override
    public void run(SourceContext<T> ctx) throws Exception {
        this.sourceContext = ctx;
        FileAlterationObserver observer = new FileAlterationObserver(directoryPath);
        observer.addListener(this);
        FileAlterationMonitor monitor = new FileAlterationMonitor(1000, observer);
        monitor.start();

        while (isRunning) {
            Thread.sleep(1000);
        }

        monitor.stop();
    }

    @Override
    public void cancel() {
        isRunning = false;
    }

    @Override
    public void onFileCreate(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
            List<T> data = objectMapper.readValue(file, javaType);
            for (T item : data) {
                synchronized (sourceContext) {
                    sourceContext.collect(item);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing file " + file.getAbsolutePath(), e);
        }
    }
}
