package ar.edu.itba.pod.query;

import ar.edu.itba.pod.QueryService;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.exceptions.ElectionsNotStartedException;
import ar.edu.itba.pod.model.ElectionStatus;
import ar.edu.itba.pod.model.PartyResults;
import ar.edu.itba.pod.model.Province;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QueryClient extends Client<QueryService> {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public QueryClient(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        super("query");
        this.addOption("state", "Name of the state chose to solve type 2 query", true, false);
        this.addOption("id", "Number of the polling place chose to solve type 3 query", true, false);
        this.addOption("outPath", "Path of the output file which contains the query data", true, true);
        this.parse(args);
        this.lookup();
    }

    public Collection<PartyResults> queryByTable(long table) throws RemoteException {
        Collection<PartyResults> pr = this.remoteService.queryByTable(table);
        return processResults(pr);
    }

    public Collection<PartyResults> queryByProvince(Province province) throws RemoteException {
        Collection<PartyResults> pr = this.remoteService.queryByProvince(province);
        return processResults(pr);
    }

    public Collection<PartyResults> queryByCountry() throws RemoteException {
        Collection<PartyResults> pr = this.remoteService.queryByCountry();
        return processResults(pr);
    }

    private Collection<PartyResults> processResults(Collection<PartyResults> pr) throws RemoteException {
        if (this.remoteService.electionStatus().equals(ElectionStatus.FINISHED)) {
            throw new ElectionsNotStartedException("There are no results. Elections haven't started yet");
        }
        List<PartyResults> ps = pr.stream().collect(Collectors.toList());
        ps.sort(Comparator.comparing(PartyResults::getPercentage).reversed().thenComparing(PartyResults::compareTo));
        if (this.remoteService.electionStatus().equals(ElectionStatus.CLOSED)) {
            System.out.println(ps.get(0).getParty() + " won the election");
        }
        return ps;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        logger.info("Starting query client");

        final QueryClient queryClient = new QueryClient(args);
        boolean hasStateName = queryClient.hasParameter("state");
        boolean hasPollingPlaceNumber = queryClient.hasParameter("id");

        if(hasPollingPlaceNumber && hasStateName) {
            throw new IllegalArgumentException("Exactly 1 or none parameter required!");
        }

        String fileName = queryClient.getParameter("outPath").orElseThrow(() -> new IllegalArgumentException("No file name specified"));

        if(hasStateName) {
            logger.info("has state name");
            String stateName = queryClient.getParameter("state").orElseThrow(() -> new IllegalArgumentException("No state name specified"));
            writeToCSV(fileName, queryClient.queryByProvince(Province.valueOf(stateName)));
            return;
        }

        if(hasPollingPlaceNumber) {
            logger.info("has polling place number");
            Long pollingPlaceNumber = Long.parseLong(queryClient.getParameter("id").orElseThrow(() -> new IllegalArgumentException("No polling place number specified")));
            writeToCSV(fileName, queryClient.queryByTable(pollingPlaceNumber));
            return;
        }
        writeToCSV(fileName, queryClient.queryByCountry());
    }

    public static void writeToCSV(String fileName, Collection<PartyResults> results){

        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)
        )) {
            writer.write("Porcentaje;Partido" + "\n");

            for (PartyResults p : results) {
                StringBuilder str = new StringBuilder();
                str.append(String.format("%.2f", p.getPercentage()));
                str.append(";");
                str.append(p.getParty().toString());
                writer.write(str.toString() + "\n");
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }
}
