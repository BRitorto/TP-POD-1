package ar.edu.itba.pod.client;

import ar.edu.itba.pod.model.Party;
import ar.edu.itba.pod.model.Province;
import ar.edu.itba.pod.model.Vote;
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
import java.util.stream.Collectors;

public class FileManager {

    public static void main(String[] args) {
        List<Vote> votes = readCSV("/home/bianca/Desktop/TP-POD/client/src/main/resources/test.csv");
    }

    private static List<Vote> readCSV(final String path) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';'));
            List<Vote> votes = new LinkedList<>();
            for (CSVRecord csvRecord : csvParser) {
                Long table = Long.parseLong(csvRecord.get(0));
                String provinceString = csvRecord.get(1);
                String choices = csvRecord.get(2);
                List<String> choicesList = Arrays.asList(choices.split(","));
                List<Party> partyList = choicesList.stream()
                        .map(Party::valueOf)
                        .collect(Collectors.toList());
                votes.add(new Vote(table, Province.valueOf(provinceString), partyList));
            }
            return votes;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
