package com.kousenit;

import com.kousenit.claudeai.ClaudeService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class BookDownloader {
    private static final String URL = "https://producingoss.com/en/producingoss.html";
    private static final String CHAPTER_SELECTOR = "div.chapter";
    private static final String SECTION_SELECTOR = "div.simplesect";
    private static final String PROMPT_TEMPLATE =
            """
                    Please summarize the section contents in a few sentences.
                    The contents are contained in the following HTML:
                    %s""";

    private final ClaudeService claudeService;

    @Autowired
    public BookDownloader(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    public Map<String, List<String>> extractAllChapters() {
        try {
            Document doc = Jsoup.connect(URL).get();
            Elements chapters = doc.select(CHAPTER_SELECTOR);
            return processChapters(chapters);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Parallel streams version
    private Map<String, List<String>> processChapters(Elements chapters) {
        return chapters.parallelStream()
                .collect(Collectors.groupingByConcurrent(
                        chapter -> Objects.requireNonNull(chapter.select("h1").first()).text(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    Elements sections = list.getFirst().select(SECTION_SELECTOR);
                                    return processSections(sections);
                                }
                        )
                ));
    }

    // Sequential streams version
//    private Map<String, List<String>> processChapters(Elements chapters) {
//        return chapters.stream()
//                .collect(Collectors.toMap(
//                                chapter -> Objects.requireNonNull(chapter.select("h1").first()).text(),
//                                chapter -> {
//                                    Elements sections = chapter.select(SECTION_SELECTOR);
//                                    return processSections(sections);
//                                },
    // only need the four-arg version if duplicate keys are possible
//                                (oldSummary, newSummary) -> newSummary,
//                                LinkedHashMap::new
//                        ));
//    }

    private List<String> processSections(Elements sections) {
        return sections.stream()
                .map(section -> claudeService.getClaudeMessageResponse(
                        String.format(PROMPT_TEMPLATE, section), ClaudeService.CLAUDE_3_HAIKU))
                .collect(Collectors.toList());
    }
}
