package com.alibaba.intl.bertforabsa;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;

public class AspectExtractionInputConverter {
    public static void main(String[] args) throws IOException {

        createTestXmlFile();
    }

    private static void createTestXmlFile() throws IOException {
        Path buyerFeedbackFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\assurance\\reason_recommend_ta.txt");
        Path suggestionOnShoppingCartFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\shopping_cart\\suggestion_on_shopping_cart.txt");
        Path xiniuFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\xiniu\\xiniu_survey.txt");
        Path ccoFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\cco");

        File[] files = ccoFilePath.toFile().listFiles((dir1, name) -> name.startsWith("export") && name.endsWith(".csv"));
        List<String> allLines = readAllLines(files);
        //List<String> allLines = Files.readAllLines(xiniuFilePath);


        List<String> aspects = getAspects();
        int skipLines = 1;
        List<String> dataLines = IntStream.range(skipLines, allLines.size())
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(i, allLines.get(i).trim()))
                .filter(e -> e.getValue().length() >= 1)
                .filter(e -> isPureAscii(e.getValue()) && StringUtils.isAsciiPrintable(e.getValue()))
                .flatMap(e -> buildLinesForCco(e.getKey() - skipLines, e.getValue()).stream())
                .collect(Collectors.toList());
        Path resultFilePath = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\assurance\\ae_test.xml");
        Path resultFilePath2 = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\shopping_cart\\ae_test.xml");
        Path resultFilePath3 = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\xiniu\\ae_test.xml");
        Path resultFilePath4 = Paths.get("D:\\Dev\\ProjectsNew\\NLP\\BERT-for-RRC-ABSA\\java\\src\\main\\resources\\cco\\ae_test.xml");
        Path file = Files.createFile(resultFilePath4);
        List<String> resultList = new ArrayList<>();
        resultList.add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        resultList.add("<sentences>");
        resultList.addAll(dataLines);
        resultList.add("</sentences>");
        Files.write(file, resultList);
        List<String> strings = Files.readAllLines(file);

        assert strings.size() > 1000;
    }

    private static List<String> readAllLines(File[] files) {
        assert files != null;
        List<String> allLines = Arrays.stream(files).sorted(comparing(File::lastModified))
                .flatMap(f -> {
                    try {
                        System.out.println("Reading from file: " + f);
                        return Files.readAllLines(f.toPath()).stream().skip(1);
                    } catch (Exception e) { return Stream.empty(); }
                }).collect(Collectors.toList());
        return allLines;
    }

    private static List<String> buildLines2(Integer id, String sentence) {
        List<String> result = new ArrayList<>();

        result.add("    <sentence id=\"" + id +"\">");
        result.add("        <text>" + StringEscapeUtils.escapeXml11(sentence) + "</text>");
        result.add("    </sentence>");
        return result;
    }

    private static List<String> buildLinesForCco(Integer idx, String line) {
        //"date_id","biz_id","date_time","content_txt","buyer_id","seller_id","user_id"

        if (idx % 1000 == 0) {
            System.out.println("idx: " + idx);
        }

        try (Reader reader = new StringReader(line)) {

            // read csv file
            List<CSVRecord> records = CSVFormat.DEFAULT.parse(reader).getRecords();

            if (records == null || records.isEmpty()) {
                return emptyList();
            }

            CSVRecord r = records.get(0);
            if (r.size() == 7) {
                String date = r.get(0);
                String idStr = r.get(1);
                String sentence = r.get(3);
                if (StringUtils.isAlphanumeric(date) && StringUtils.isAlphanumeric(idStr)) {
                    String id = "" + idx + "_" + date + "_" + idStr;
                    List<String> result = new ArrayList<>();

                    //result.add("    <!--" + id +" -->");
                    result.add("    <sentence id=\"" + idx + "\">");
                    result.add("        <text>" + StringEscapeUtils.escapeXml11(sentence) + "</text>");
                    result.add("    </sentence>");
                    return result;
                }
            }
            return emptyList();
        } catch (IOException ex) {
            //ex.printStackTrace();
            return emptyList();
        }
//        String[] strings = line.split(",");
//        if (strings.length == 7) {
//            String sentence = strings[3];
//            String id = "" + idx + "_" + strings[0] + "_" + strings[1];
//            List<String> result = new ArrayList<>();
//
//            result.add("    <sentence id=\"" + id +"\">");
//            result.add("        <text>" + StringEscapeUtils.escapeXml11(sentence) + "</text>");
//            result.add("    </sentence>");
//            return result;
//        } else {
//            return emptyList();
//        }
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
