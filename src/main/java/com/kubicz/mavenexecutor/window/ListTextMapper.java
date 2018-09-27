package com.kubicz.mavenexecutor.window;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListTextMapper {

    public static final String listAsText(List<String> list, String delimiter) {
        return streamAsText(list.stream(), delimiter);
    }

    public static final String streamAsText(Stream<String> stream, String delimiter) {
        return stream.collect(Collectors.joining(delimiter));
    }

}