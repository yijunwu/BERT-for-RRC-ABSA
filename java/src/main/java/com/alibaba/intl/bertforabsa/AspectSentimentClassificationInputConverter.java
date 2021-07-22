package com.alibaba.intl.bertforabsa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AspectSentimentClassificationInputConverter {
    public static void main(String[] args) throws IOException {

        createTestXmlFile();
    }

    private static void createTestXmlFile() throws IOException {
        Path feedbackFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\assurance\\prediction_terms.txt");
        Path shoppingCartSuggestionFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\shopping_cart\\prediction_terms.txt");
        List<String> allLines = Files.readAllLines(shoppingCartSuggestionFilePath);

        if (allLines.size() < 2) { return; }

        Map<String, AscInput> resultMap = new LinkedHashMap<>();
        String lastLine = null;
        int lastBegin = 0;
        for (int i = 0; i < allLines.size(); i++) {
           String temp = allLines.get(i);
           if (!temp.isEmpty() && temp.chars().allMatch(Character::isDigit)) {
               if (lastLine != null && (lastLine.isEmpty() || lastLine.trim().isEmpty())) {
                    List<AscInput> list = getEntries(allLines, lastBegin, i);
                    list.forEach(l -> resultMap.put(l.id, l));
                    lastBegin = i;
               }
           }
            lastLine = temp;
        }

        Path resultFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\assurance\\asc_test.json");
        Path shoppingCartResultFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\shopping_cart\\asc_test.json");
        Path file = Files.createFile(shoppingCartResultFilePath);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(file.toFile(), resultMap);

        List<String> strings = Files.readAllLines(file);

        assert strings.size() >= 1;
    }

    private static List<AscInput> getEntries(List<String> allLines, int beginInclusive, int endExclusive) {
        // TODO wuyijun 待实现
        String idStr = allLines.get(beginInclusive);
        int id = Integer.parseInt(idStr);
        String text = allLines.get(beginInclusive + 1);

        List<String> termList = IntStream.range(beginInclusive + 2, endExclusive)
                .mapToObj(idx -> allLines.get(idx).trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<AscInput> resultList = new ArrayList<>();
        for (int i = 0; i <termList.size(); i ++) {
            AscInput entry = new AscInput();
            entry.id = "" + (id * 1000 + i);
            entry.sentenceId = "" + id;
            entry.sentence = text;
            entry.term = termList.get(i);
            entry.polarity = "neutral";
            resultList.add(entry);
        }
        return resultList;
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
