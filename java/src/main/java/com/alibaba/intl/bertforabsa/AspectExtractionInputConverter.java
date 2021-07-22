package com.alibaba.intl.bertforabsa;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.text.StringEscapeUtils;

public class AspectExtractionInputConverter {
    public static void main(String[] args) throws IOException {

        createTestXmlFile();
    }

    private static void createTestXmlFile() throws IOException {
        Path buyerFeedbackFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\assurance\\reason_recommend_ta.txt");
        Path suggestionOnShoppingCartFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\shopping_cart\\suggestion_on_shopping_cart.txt");
        List<String> allLines = Files.readAllLines(suggestionOnShoppingCartFilePath);


        List<String> aspects = getAspects();
        int skipLines = 1;
        List<String> dataLines = IntStream.range(skipLines, allLines.size())
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(i, allLines.get(i).trim()))
                .filter(e -> e.getValue().length() >= 1)
                .filter(e -> isPureAscii(e.getValue()))
                .flatMap(e -> buildLines2(e.getKey() - skipLines, e.getValue()).stream())
                .collect(Collectors.toList());
        Path resultFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\assurance\\ae_test.xml");
        Path resultFilePath2 = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\shopping_cart\\ae_test.xml");
        Path file = Files.createFile(resultFilePath2);
        List<String> resultList = new ArrayList<>();
        resultList.add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        resultList.add("<sentences>");
        resultList.addAll(dataLines);
        resultList.add("</sentences>");
        Files.write(file, resultList);
        List<String> strings = Files.readAllLines(file);

        assert strings.size() > 1000;
    }

    private static List<String> buildLines2(Integer id, String sentence) {
        List<String> result = new ArrayList<>();

        result.add("    <sentence id=\"" + id +"\">");
        result.add("        <text>" + StringEscapeUtils.escapeXml11(sentence) + "</text>");
        result.add("    </sentence>");
        return result;
    }

    public static boolean isPureAscii(String v) {
        return Charset.forName("US-ASCII").newEncoder().canEncode(v);
        // or "ISO-8859-1" for ISO Latin 1
        // or StandardCharsets.US_ASCII with JDK1.7+
    }

    private static List<String> getAspects() {
        List<String> aspects = new ArrayList<>();
        aspects.add("general");
        aspects.add("price");
        aspects.add("cost");
        aspects.add("fee");
        aspects.add("speed");
        aspects.add("fast");
        aspects.add("time");
        aspects.add("convinience");
        aspects.add("easy_to_use");
        aspects.add("quality");
        aspects.add("count");
        aspects.add("amount");
        aspects.add("fairness");
        aspects.add("transparent");
        aspects.add("clear");
        aspects.add("professionalism");
        aspects.add("safe");
        aspects.add("warranty");
        aspects.add("insurrance");
        aspects.add("protect");
        aspects.add("accurate");
        aspects.add("precision");
        aspects.add("genuine");
        aspects.add("real");
        aspects.add("trust-worthy");
        return aspects;
    }
}
