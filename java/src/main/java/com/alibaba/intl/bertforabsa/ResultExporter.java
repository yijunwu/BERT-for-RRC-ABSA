package com.alibaba.intl.bertforabsa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ResultExporter {
    public static void main(String[] args) throws IOException {

        createTsvFile();
    }

    class Prediction {
        List<List<List<Double>>> logits;
        List<List<String>> raw_X;
        List<List<Integer>> idx_map;
    }

    private static void createTsvFile() throws IOException {
        Path predictionFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\pytorch-pretrained-bert\\run\\pt_ae\\assurance\\1\\predictions.json");
        Prediction prediction = new ObjectMapper().convertValue(String.join("", Files.readAllLines(predictionFilePath)), Prediction.class);

        IntStream.range(0, prediction.logits.size()).mapToObj(i -> getLabel(prediction.logits.get(i)));

        List<List<TargetLabel>> labels = prediction.logits.stream().map(l -> getLabel(l)).collect(Collectors.toList());

        List<List<String>> terms = IntStream.range(0, prediction.logits.size())
                .mapToObj(i -> extractTerms(labels.get(i), prediction.raw_X.get(i)))
                .collect(Collectors.toList());

        List<String> aspects = getAspects();
        List<String> dataLines = IntStream.range(0, allLines.size())
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(i, allLines.get(i).trim()))
                .filter(e -> e.getValue().length() >= 1)
                .filter(e -> isPureAscii(e.getValue()))
                .flatMap(e -> buildLines2(e.getKey(), e.getValue()).stream())
                .collect(Collectors.toList());

        Path resultFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\ae_test.xml");
        Path file = Files.createFile(resultFilePath);
        List<String> resultList = new ArrayList<>();
        resultList.add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        resultList.add("<sentences>");
        resultList.addAll(dataLines);
        resultList.add("</sentences>");
        Files.write(file, resultList);
        List<String> strings = Files.readAllLines(file);

        assert strings.size() > 1000;
    }

    private static List<String> extractTerms(List<TargetLabel> targetLabels, List<String> strings) {
        
        for (int i = 0; i < targetLabels.size(); i ++) {
            TargetLabel label = targetLabels.get(i);
            if (label == TargetLabel.B)
        }
    }

    enum TargetLabel {
        B, I, O
    }

    private static List<TargetLabel> getLabel(List<List<Double>> lists) {
        return lists.stream()
                .map(l -> argmax(l))
                .map(i -> (i == 1) ? TargetLabel.B : (i == 2 ? TargetLabel.I : TargetLabel.O))
                .collect(Collectors.toList());
    }

    private static Integer argmax(List<Double> l) {
        double max = -1000;
        int index = -1;

        for(int i = 0; i < l.size(); i ++){
            if(max < l.get(i)){
                max = l.get(i);
                index = i;
            }
        }
        return index;
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
