package com.alibaba.intl.bertforabsa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AspectExtractionResultExporter {
    public static void main(String[] args) throws IOException {

        createTsvFile();
    }

    private static void createTsvFile() throws IOException {
        Path predictionFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\pytorch-pretrained-bert\\run\\pt_ae\\shopping_cart\\1\\predictions.json");
        String jsonStr = String.join("", Files.readAllLines(predictionFilePath));
        AEPrediction prediction = new ObjectMapper().readValue(jsonStr, AEPrediction.class);

        List<List<TargetLabel>> labels = prediction.logits.stream().map(l -> getLabel(l)).collect(Collectors.toList());

        List<List<String>> terms = IntStream.range(0, prediction.logits.size())
                .mapToObj(i -> extractTerms3(labels.get(i), prediction.raw_X.get(i), prediction.idx_map.get(i)))
                .collect(Collectors.toList());

//        List<String> aspects = getAspects();
//        List<String> dataLines = IntStream.range(0, allLines.size())
//                .mapToObj(i -> new AbstractMap.SimpleEntry<>(i, allLines.get(i).trim()))
//                .filter(e -> e.getValue().length() >= 1)
//                .filter(e -> isPureAscii(e.getValue()))
//                .flatMap(e -> buildLines2(e.getKey(), e.getValue()).stream())
//                .collect(Collectors.toList());

        Path assuranceResultFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\assurance\\prediction_terms.txt");
        Path resultFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\shopping_cart\\prediction_terms.txt");
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

    /**
     * 人为将本该连续的term连接在一起且去除严格重复的term
     */
    private static List<String> extractTerms3(List<TargetLabel> targetLabels, List<String> strings, List<Integer> idxMap) {
        List<List<Integer>> resultList = new ArrayList<>();
        List<Integer> termIdx = new ArrayList<>();
        for (int i = 0; i < Math.min(idxMap.size(), targetLabels.size()); i ++) {
            TargetLabel label = targetLabels.get(i);
            int idx = idxMap.get(i);

            if (label == TargetLabel.B && termIdx.size() > 0 && termIdx.get(termIdx.size() - 1) == idx - 1) {
                label = TargetLabel.I;
            }

            if (label == TargetLabel.B) {
                if (termIdx.size() > 0) {
                    resultList.add(new ArrayList<>(termIdx));
                    termIdx.clear();
                }
                termIdx.add(idx);
            } else if (label == TargetLabel.I) {
                termIdx.add(idx);
            } else if (label == TargetLabel.O) {
                if (termIdx.size() > 0) {
                    resultList.add(new ArrayList<>(termIdx));
                    termIdx.clear();
                }
            }
        }
        if (termIdx.size() > 0) {
            resultList.add(termIdx);
        }
        for (int i = resultList.size() - 1; i >= 0; i --) {
            for (int j = resultList.size() - 1; j > i; j --) {
                if (equals(resultList.get(i), resultList.get(j))) {
                    resultList.remove(j);
                }
            }
        }
        return resultList.stream()
                .map(l -> l.stream().map(i -> strings.get(i)).collect(Collectors.joining(" ")))
                .collect(Collectors.toList());
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
