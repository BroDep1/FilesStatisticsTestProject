package org.example;

public class Main {
    public static void main(String[] args) {
        ArgsService argsService = new ArgsService(args);
        FileService fileService = new FileService(argsService);
        var files = fileService.getFiles();
        StatsService statsService = new StatsService();
        statsService.printStats(files);
    }
}