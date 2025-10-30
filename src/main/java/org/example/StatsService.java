package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class StatsService {

    public void printStats(Map<String, List<Path>> files) {
        long fileCount = 0;
        StringBuilder exceptions = new StringBuilder();
        for (Map.Entry<String, List<Path>> filesEntry : files.entrySet()) {
            System.out.print("Файлы с расширением " + filesEntry.getKey() + ": ");
            long size = 0, count = 0, blankCount = 0, comCount = 0;
            for (Path file : filesEntry.getValue()) {
                fileCount++;
                try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
                    size += Files.size(file);
                    String str;
                    while ((str = reader.readLine()) != null) {
                        if (str.startsWith("//") || str.startsWith("#")) {
                            comCount++;
                        }
                        if (str.isBlank()) {
                            blankCount++;
                        }
                        count++;
                    }
                } catch (IOException e) {
                    exceptions.append(e);
                }
            }
            System.out.print("Общий Размер: " + size + " байт, ");
            System.out.print("Всего строк: " + count + ", ");
            System.out.print("Пустых строк: " + blankCount + ", ");
            System.out.println("Строк с комментариями: " + comCount);
        }
        System.out.println("Общее количество файлов: " + fileCount);
        System.out.println(exceptions);
    }
}
