package com.alibaba.intl.bertforabsa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;

import java.io.FileWriter;
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

class Prediction {
    public Prediction() {}
    public List<List<List<Double>>> logits;
    public List<List<String>> raw_X;
    public List<List<Integer>> idx_map;
}

public class ResultExporter {
    public static void main(String[] args) throws IOException {

        createTsvFile();
    }

    private static void createTsvFile() throws IOException {
        Path predictionFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\pytorch-pretrained-bert\\run\\pt_ae\\assurance\\1\\predictions.json");
        String jsonStr = String.join("", Files.readAllLines(predictionFilePath));
        Prediction prediction = new ObjectMapper().readValue(jsonStr, Prediction.class);

        List<List<TargetLabel>> labels = prediction.logits.stream().map(l -> getLabel(l)).collect(Collectors.toList());

        List<List<String>> terms = IntStream.range(0, prediction.logits.size())
                .mapToObj(i -> extractTerms2(labels.get(i), prediction.raw_X.get(i), prediction.idx_map.get(i)))
                .collect(Collectors.toList());

//        List<String> aspects = getAspects();
//        List<String> dataLines = IntStream.range(0, allLines.size())
//                .mapToObj(i -> new AbstractMap.SimpleEntry<>(i, allLines.get(i).trim()))
//                .filter(e -> e.getValue().length() >= 1)
//                .filter(e -> isPureAscii(e.getValue()))
//                .flatMap(e -> buildLines2(e.getKey(), e.getValue()).stream())
//                .collect(Collectors.toList());

        Path resultFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\prediction_terms.txt");
        Path file = Files.createFile(resultFilePath);
        List<String> resultList = IntStream.range(0, prediction.logits.size())
                .mapToObj(i -> "" + i + System.lineSeparator()
                        + String.join(" ", prediction.raw_X.get(i)) + System.lineSeparator()
                        + String.join(System.lineSeparator(), terms.get(i))
                        + System.lineSeparator())
                .collect(Collectors.toList());
        //Files.writeString(resultFilePath, Strings.join(resultList, System.lineSeparator()));

        FileWriter writer = new FileWriter(resultFilePath.toFile());
        for(String str: resultList) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();
    }

    private static List<String> extractTerms(List<TargetLabel> targetLabels, List<String> strings, List<Integer> idxMap) {
        List<String> resultList = new ArrayList<>();
        List<String> term = new ArrayList<>();
        for (int i = 0; i < Math.min(strings.size(), targetLabels.size()); i ++) {
            TargetLabel label = targetLabels.get(i);
            int idx = idxMap.get(i);
            if (label == TargetLabel.B) {
                if (term.size() > 0) {
                    resultList.add(String.join(" ", term));
                    term.clear();
                }
                term.add(strings.get(idx));
            } else if (label == TargetLabel.I) {
                term.add(strings.get(idx));
            } else if (label == TargetLabel.O) {
                if (term.size() > 0) {
                    resultList.add(String.join(" ", term));
                    term.clear();
                }
            }
        }
        if (term.size() > 0) {
            resultList.add(String.join(" ", term));
        }
        return resultList;
    }

    /**
     * 人为将本该连续的term连接在一起
     */
    private static List<String> extractTerms2(List<TargetLabel> targetLabels, List<String> strings, List<Integer> idxMap) {
        List<String> resultList = new ArrayList<>();
        List<String> term = new ArrayList<>();
        List<Integer> termIdx = new ArrayList<>();
        for (int i = 0; i < Math.min(idxMap.size(), targetLabels.size()); i ++) {
            TargetLabel label = targetLabels.get(i);
            int idx = idxMap.get(i);

            if (label == TargetLabel.B && term.size() > 0 && termIdx.get(termIdx.size() - 1) == idx - 1) {
                label = TargetLabel.I;
            }

            if (label == TargetLabel.B) {
                if (term.size() > 0) {
                    resultList.add(String.join(" ", term));
                    term.clear();
                    termIdx.clear();
                }
                term.add(strings.get(idx));
                termIdx.add(idx);
            } else if (label == TargetLabel.I) {
                term.add(strings.get(idx));
                termIdx.add(idx);
            } else if (label == TargetLabel.O) {
                if (term.size() > 0) {
                    resultList.add(String.join(" ", term));
                    term.clear();
                    termIdx.clear();
                }
            }
        }
        if (term.size() > 0) {
            resultList.add(String.join(" ", term));
        }
        return resultList;
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
