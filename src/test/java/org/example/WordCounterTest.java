package org.example;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class WordCounterTest {

    @Test
    void countsWordsAcrossMultipleLines_caseInsensitive_andIgnoresPunctuation() {
        // tag::test[]
        assertThat(WordCounter.countWords(Stream.of(
                "Asciidoctor makes docs-as-code: write, build, publish.",
                "ASCIIDOCTOR makes Docs-as-Code; write—build—publish!"
        ))).isEqualTo(Map.of(
                "asciidoctor", 2L,
                "makes", 2L,
                "docs", 2L,
                "as", 2L,
                "code", 2L,
                "write", 2L,
                "build", 2L,
                "publish", 2L
        ));
        // end::test[]
    }

    @Test
    void handlesEmptyAndNullLinesGracefully() {
        Stream<String> lines = Stream.of("", null, "  ", "One two two");
        Map<String, Long> result = WordCounter.countWords(lines);
        assertThat(result).isEqualTo(Map.of(
                "one", 1L, "two", 2L
        ));
        assertThat(result.size()).isEqualTo(2);
    }
}
