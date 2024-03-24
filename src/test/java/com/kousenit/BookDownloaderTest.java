package com.kousenit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class BookDownloaderTest {

    @Autowired
    private BookDownloader bookDownloader;

    @Test
    void extractAllChapters() {
        Map<String, List<String>> chapterSummaries = bookDownloader.extractAllChapters();
        chapterSummaries.forEach((title, summary) -> {
            System.out.println();
            System.out.println(title);
            summary.forEach(System.out::println);
        });

    }
}