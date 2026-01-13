package org.example;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;

public class WordCounter {
    public static Map<String, Long> countWords(Stream<String> lines) {
        return lines.
        // tag::pipeline[]
        filter(Objects::nonNull)
          .flatMap(line -> stream(line.split("[^A-Za-z0-9']+")))
          .filter(word -> !word.isEmpty())
          .map(String::toLowerCase)
          .collect(Collectors.groupingBy(identity(), counting()));
        // end::pipeline[]
    }
}
