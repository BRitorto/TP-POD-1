package ar.edu.itba.pod.server;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileManager {

    private Map<Party, Double> tablePercentages; // sistema tradicional
    private Map<Party, Double> provincePercentages; // STV
    private Map<Party, Double> nationalPercentages; // AV

    private static List<Vote> votes;

    public static void main(String[] args) {
        readCSV("/home/bianca/Desktop/TP-POD/server */src/main/resources/test.csv");
    }

    public static void readCSV(final String path) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';'));
            votes = new LinkedList<>();
            for (CSVRecord csvRecord : csvParser) {
                Integer table = Integer.parseInt(csvRecord.get(0));
                String provinceString = csvRecord.get(1);
                String choices = csvRecord.get(2);
                List<String> choicesList = Arrays.asList(choices.split(","));
                List<Party> partyList = choicesList.stream()
                        .map(Party::valueOf)
                        .collect(Collectors.toList());
                votes.add(new Vote(table, Province.valueOf(provinceString), partyList));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeCSV(final String path) {
        try {
            FileWriter out = new FileWriter("book_new.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
