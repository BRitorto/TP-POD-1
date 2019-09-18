package ar.edu.itba.pod.client;

import ar.edu.itba.pod.VoteService;
import ar.edu.itba.pod.exceptions.ElectionsNotStartedException;
import ar.edu.itba.pod.exceptions.EmptyVotesException;
import ar.edu.itba.pod.model.Party;
import ar.edu.itba.pod.model.Province;
import ar.edu.itba.pod.model.Vote;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class VoteClient extends Client<VoteService> {

    public VoteClient(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        super("vote");
        this.addOption("csvPath", "Path of the input file with votes", true, true);
        this.parse(args);
        this.lookup();
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        VoteClient voteClient = new VoteClient(args);
        String votesPath = voteClient.getParameter("csvPath").orElseThrow(IllegalArgumentException::new);
        List<Vote> votes = voteClient.readCSV(votesPath);
        voteClient.ballot(votes);
        System.out.println(votes.size() + " votes registered");
        //List<Vote> votes = readCSV("/home/bianca/Desktop/eTP-POD/client/src/main/resources/test.csv");
    }

    public void ballot(Collection<Vote> votes) throws RemoteException, ElectionsNotStartedException, EmptyVotesException {
        Objects.requireNonNull(votes);
        int ballot =  this.remoteService.ballot(votes);
        if(ballot == -1){
            throw new ElectionsNotStartedException("Elections haven't started yet!");
        }else if (ballot == -2){
            throw new EmptyVotesException("Please enter at least one vote");
        }

    }

    private List<Vote> readCSV(final String path) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';'));
            List<Vote> votes = new LinkedList<>();
            for (CSVRecord csvRecord : csvParser) {
                Long table = Long.parseLong(csvRecord.get(0));
                String provinceString = csvRecord.get(1);
                String choices = csvRecord.get(2);
                List<String> choicesList = Arrays.asList(choices.split(","));
                LinkedList<Party> partyList = choicesList.stream()
                        .map(Party::valueOf)
                        .collect(Collectors.toCollection(LinkedList::new));
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
