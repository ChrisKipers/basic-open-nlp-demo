package com.kipers.test;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        String content =
                "Former first lady Nancy Reagan was taken to a suburban Los Angeles " +
                        "hospital as a precaution Sunday after a fall at her home, an" +
                        "aide said. The 86-year-old Reagan will remain overnight for observation at a " +
                        "hospital in Santa Monia, California, said Joanne Drake " +
                        "chief of staff for the Reagan Foundation.";

        NameFinderME personFinder =
                new NameFinderME(new TokenNameFinderModel(getTokenizerInputStream("en-ner-person.bin")));
        NameFinderME organizationFinder =
                new NameFinderME(new TokenNameFinderModel(getTokenizerInputStream("en-ner-organization.bin")));
        SentenceDetectorME sentenceDetector =
                new SentenceDetectorME(new SentenceModel(getTokenizerInputStream("en-sent.bin")));

        String[] sentences = sentenceDetector.sentDetect(content);
        for (int si = 0; si < sentences.length; si++) {
            String[] people = getEntities(sentences[si], personFinder);
            String[] organizations = getEntities(sentences[si], organizationFinder);
            System.out.println(Arrays.toString(people));
            System.out.println(Arrays.toString(organizations));
        }

        personFinder.clearAdaptiveData();
        organizationFinder.clearAdaptiveData();
    }

    private static InputStream getTokenizerInputStream(String fileName) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(fileName);
    }

    private static String[] getEntities(String sentence, NameFinderME finder) {
        Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(sentence);
        Span[] spans = finder.find(tokens);
        return Span.spansToStrings(spans, tokens);
    }
}
