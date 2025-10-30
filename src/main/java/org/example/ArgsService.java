package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class ArgsService {
    private final Path path;
    private boolean isRecursive = false;
    private int maxDepth = 0;
    private int threadNumber = 1;
    private Set<String> includedExt = Set.of();
    private Set<String> excludedExt = Set.of();

    public ArgsService(String[] args) {
        path = Path.of(args[0]);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            System.exit(1);
        }
        for (String arg : args) {
            if (arg.equals("--recursive")) {
                isRecursive = true;
                continue;
            }
            if (arg.startsWith("--max-depth")) {
                maxDepth = Integer.parseInt(arg.split("=")[1]);
                continue;
            }
            if (arg.startsWith("--thread")) {
                threadNumber = Integer.parseInt(arg.split("=")[1]);
                continue;
            }
            if (arg.startsWith("--include-ext")) {
                includedExt = Set.of(arg.split("=")[1].split(","));
                continue;
            }
            if (arg.startsWith("--exclude-ext")) {
                excludedExt = Set.of(arg.split("=")[1].split(","));
            }
        }
    }

    public boolean isRecursive() {
        return isRecursive;
    }

    public Path getPath() {
        return path;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public Set<String> getIncludedExt() {
        return includedExt;
    }

    public Set<String> getExcludedExt() {
        return excludedExt;
    }
}
