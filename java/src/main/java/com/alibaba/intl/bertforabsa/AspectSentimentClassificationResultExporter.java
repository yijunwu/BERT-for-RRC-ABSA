package com.alibaba.intl.bertforabsa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.text.StringEscapeUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AspectSentimentClassificationResultExporter {
    public static void main(String[] args) throws IOException {

        createTsvFile();
    }

    private static void createTsvFile() throws IOException {
        String dataset = "order_review";
        Path assuranceInputFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\pytorch-pretrained-bert\\asc\\assurance\\test.json");
        Path assurancePredictionFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\pytorch-pretrained-bert\\run\\pt_asc\\assurance\\1\\predictions.json");
        Path inputFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\pytorch-pretrained-bert\\asc\\" + dataset + "\\test.json");
        Path predictionFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\pytorch-pretrained-bert\\run\\pt_asc\\" + dataset + "\\1\\predictions.json");
        String ascInputJsonStr = String.join("", Files.readAllLines(inputFilePath));
        String ascPredictionJsonStr = String.join("", Files.readAllLines(predictionFilePath));

        TypeFactory factory;
        MapType type;

        factory = TypeFactory.defaultInstance();
        type    = factory.constructMapType(LinkedHashMap.class, String.class, AscInput.class);

        Map<String, AscInput> input = new ObjectMapper().readValue(ascInputJsonStr, type);
        AscPrediction prediction = new ObjectMapper().readValue(ascPredictionJsonStr, AscPrediction.class);

        List<SentimentLabel> labels = prediction.logits.stream().map(l -> getLabel(l)).collect(Collectors.toList());

        List<AscInput> ascInputList = new ArrayList<>(input.values());
        for (int i = 0; i < Math.min(labels.size(), ascInputList.size()); i++) {
            ascInputList.get(i).polarity = labels.get(i).toString();
        }

        Path assuranceResultFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\assurance\\prediction_sentiment.txt");
        Path resultFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\" + dataset + "\\prediction_sentiment.txt");
        Path file = Files.createFile(resultFilePath);

        FileWriter writer = new FileWriter(resultFilePath.toFile());
        List<String> content = ascInputList.stream().map(item -> item.id + "\t" + item.sentenceId + "\t" + item.polarity + "\t" + item.term + "\t" + item.sentence).collect(Collectors.toList());
        writer.write(String.join(System.lineSeparator(), content));
        writer.close();
    }

    private static boolean equals(List<Integer> integers0, List<Integer> integers1) {
        if (integers0.size() != integers1.size()) {
            return false;
        }
        for (int i = 0; i < integers0.size(); i++) {
            if (integers0.get(i).intValue() != integers1.get(i).intValue()) {
                return false;
            }
        }
        return true;
    }


    enum SentimentLabel {
        POSITIVE, NEGATIVE, NEUTRAL
    }

    private static SentimentLabel getLabel(List<Double> lists) {
        Integer i = argmax(lists);
        return (i == 0) ? SentimentLabel.POSITIVE : (i == 1 ? SentimentLabel.NEGATIVE : SentimentLabel.NEUTRAL);
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
