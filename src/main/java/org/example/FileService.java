package org.example;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileService {

    private final Lock lock = new ReentrantLock();

    private final ArgsService argsService;

    public FileService(ArgsService argsService) {
        this.argsService = argsService;
    }

    public Map<String, List<Path>> getFiles() {
        Map<String, List<Path>> result = new ConcurrentHashMap<>();
        try (ForkJoinPool pool = new ForkJoinPool(argsService.getThreadNumber())) {
            pool.submit(() -> scanDirectory(argsService.getPath(), pool, result, 0));
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private void scanDirectory(Path path, ForkJoinPool pool, Map<String, List<Path>> result, int depth) {
        if (!Files.isDirectory(path) || (depth > argsService.getMaxDepth())) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry) && argsService.isRecursive()) {
                    pool.submit(() -> scanDirectory(entry, pool, result, depth + 1));
                } else if (Files.isRegularFile(entry) && isAllowed(entry)) {
                    if (!result.containsKey(getExtension(entry))) {
                        lock.lock();
                        if (!result.containsKey(getExtension(entry))) {
                            result.put(getExtension(entry), new CopyOnWriteArrayList<>());
                        }
                        lock.unlock();
                    }
                    result.get(getExtension(entry)).add(entry);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isAllowed(Path file) {
        String ext = getExtension(file);
        if (!argsService.getIncludedExt().isEmpty() && !argsService.getIncludedExt().contains(ext)) {
            return false;
        }
        if (!argsService.getExcludedExt().isEmpty() && argsService.getExcludedExt().contains(ext)) {
            return false;
        }
        return true;
    }

    private String getExtension(Path file) {
        if (file.getFileName().endsWith(".") || !file.getFileName().toString().contains(".")) {
            return "";
        }
        String name = file.getFileName().toString();
        return name.split("\\.")[1];
    }
}
