package org.example;

import org.apache.flink.api.common.functions.MapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordsCapitalizer implements MapFunction<String, String> {
    private static final Logger log = LoggerFactory.getLogger(WordsCapitalizer.class);
    public String map(String s) {
        System.out.println("s : " + s);
        return s.toUpperCase();
    }
}