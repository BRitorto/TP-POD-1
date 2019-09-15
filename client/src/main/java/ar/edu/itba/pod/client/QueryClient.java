package ar.edu.itba.pod.client;

import ar.edu.itba.pod.QueryService;
import ar.edu.itba.pod.model.PartyResults;
import ar.edu.itba.pod.model.Province;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

public class QueryClient implements QueryService {

    /* Deberá dejar en archivos CSV los resultados de las consultas realizadas. */

    @Override
    public Collection<PartyResults> queryByTable(long table) throws RemoteException {
        return null;
    }

    @Override
    public Collection<PartyResults> queryByProvince(Province province) throws RemoteException {
        return null;
    }

    @Override
    public Collection<PartyResults> queryByCountry() throws RemoteException {
        return null;
    }

    public void main(String[] args) {

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
