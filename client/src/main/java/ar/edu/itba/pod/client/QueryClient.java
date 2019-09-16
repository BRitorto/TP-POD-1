package ar.edu.itba.pod.client;

import ar.edu.itba.pod.QueryService;
import ar.edu.itba.pod.model.PartyResults;
import ar.edu.itba.pod.model.Province;
import org.graalvm.compiler.api.replacements.Snippet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

public class QueryClient extends Client<QueryService> {

    /* Deberá dejar en archivos CSV los resultados de las consultas realizadas. */

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public QueryClient(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        super("Query");
        this.addOption("stateName", "name of the state chose to solve type 2 query", true, false);
        this.addOption("pollingPlaceNumber", "Number of the polling place chose to solve type 3 query", true, false);
        this.addOption("fileName", "Path of the output file which contains the query data", true, true);
        this.parse(args);
        this.lookup();
    }

    public Collection<PartyResults> queryByTable(long table) throws RemoteException {
        return this.remoteService.queryByTable(table);
    }

    public Collection<PartyResults> queryByProvince(Province province) throws RemoteException {
        return this.remoteService.queryByProvince(province);
    }

    public Collection<PartyResults> queryByCountry() throws RemoteException {
        return this.remoteService.queryByCountry();
    }

    public void main (@Snippet.NonNullParameter String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        logger.info("Starting query client");

        final QueryClient queryClient = new QueryClient(args);
        boolean hasStateName = queryClient.hasParameter("stateNumber");
        boolean hasPollingPlaceNumber = queryClient.hasParameter("pollingPlaceNumber");

        if(hasPollingPlaceNumber && hasStateName) {
            throw new IllegalArgumentException("Exactly 1 or none parameter required !");
        }

        String fileName = queryClient.getParameter("fileName").orElseThrow(() -> new IllegalArgumentException("No file name specified"));

        if(hasStateName) {
            String stateName = queryClient.getParameter("stateNumber").orElseThrow(() -> new IllegalArgumentException("No state name specified"));
            System.out.println(queryClient.queryByProvince(Province.valueOf(stateName)));
            return;
        }

        if(hasPollingPlaceNumber) {
            Long pollingPlaceNumber = Long.parseLong(queryClient.getParameter("pollingPlaceNumber").orElseThrow(() -> new IllegalArgumentException("No polling place number specified")));
            System.out.println(queryClient.queryByTable(pollingPlaceNumber));
            return;
        }

        System.out.println(queryClient.queryByCountry());

    }

    public void writeToCSV(String fileName, List<String> results){

        /* Porcentaje;Partido
            33,00%;OWL
            32,00%;GORILLA
            16,00%;TIGER */

        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)
        )) {

            writer.write("Porcentaje;Partido" + "\n");

            for (String s : results) {
                writer.write(s + "\n");
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }

    }

}
