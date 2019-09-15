package ar.edu.itba.pod.client;

import ar.edu.itba.pod.AdministrationService;

import java.rmi.RemoteException;

/**
 * Created by estebankramer on 15/09/2019.
 */

public class AdministrationClient implements AdministrationService {
    @Override
    public boolean startElections() throws RemoteException {
        return false;
    }

    @Override
    public String getElectionsState() throws RemoteException {
        return null;
    }

    @Override
    public void endElections() throws RemoteException {

    }
}
