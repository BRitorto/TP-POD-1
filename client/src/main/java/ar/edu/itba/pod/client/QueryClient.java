package ar.edu.itba.pod.client;

import ar.edu.itba.pod.ClientInterface;
import ar.edu.itba.pod.QueryService;
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
import java.util.Collections;
import java.util.List;

public class QueryClient extends Client<QueryService>{

    /* Deberá dejar en archivos CSV los resultados de las consultas realizadas. */

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
        return this.remoteService.queryByTable(table);
    }

    public Collection<PartyResults> queryByProvince(Province province) throws RemoteException {
        return this.remoteService.queryByProvince(province);
    }

    public Collection<PartyResults> queryByCountry() throws RemoteException {
        return this.remoteService.queryByCountry();
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        logger.info("Starting query client");

        final QueryClient queryClient = new QueryClient(args);
        boolean hasStateName = queryClient.hasParameter("state");
        boolean hasPollingPlaceNumber = queryClient.hasParameter("id");

        if(hasPollingPlaceNumber && hasStateName) {
            throw new IllegalArgumentException("Exactly 1 or none parameter required !");
        }

        String fileName = queryClient.getParameter("outPath").orElseThrow(() -> new IllegalArgumentException("No file name specified"));

        if(hasStateName) {
            logger.info("has state name");
            String stateName = queryClient.getParameter("state").orElseThrow(() -> new IllegalArgumentException("No state name specified"));
            //System.out.println(queryClient.queryByProvince(Province.valueOf(stateName)));
            writeToCSV(fileName, queryClient.queryByProvince(Province.valueOf(stateName)));
            return;
        }

        if(hasPollingPlaceNumber) {
            logger.info("has polling place number");

            Long pollingPlaceNumber = Long.parseLong(queryClient.getParameter("id").orElseThrow(() -> new IllegalArgumentException("No polling place number specified")));
            //System.out.println(queryClient.queryByTable(pollingPlaceNumber));

           writeToCSV(fileName, queryClient.queryByTable(pollingPlaceNumber));
            return;
        }

        //System.out.println(queryClient.queryByCountry());
        writeToCSV(fileName, queryClient.queryByCountry());

    }

    public static void writeToCSV(String fileName, Collection<PartyResults> results){

        /* Porcentaje;Partido
            33,00%;OWL
            32,00%;GORILLA
            16,00%;TIGER */

        logger.info("Writing to CSV");

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
