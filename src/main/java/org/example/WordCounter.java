package org.example;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
// tag::static_imports[]
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
// end::static_imports[]

public class WordCounter {
    public static Map<String, Long> countWords(Stream<String> lines) {
        return lines.
                // tag::pipeline[] @formatter:off
filter(Objects::nonNull)
  .flatMap(line -> stream(line.split("[^A-Za-z0-9']+")))
  .filter(word -> !word.isEmpty())
  .map(String::toLowerCase)
  .collect(Collectors.groupingBy(identity(), counting()));
        // end::pipeline[] @formatter:on
    }
}
