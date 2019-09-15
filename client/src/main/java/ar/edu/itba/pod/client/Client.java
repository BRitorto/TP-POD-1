package ar.edu.itba.pod.client;

import org.apache.commons.cli.*;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;

public class Client<T extends Remote> extends UnicastRemoteObject {

    private static final String OPTION_NAME = "serverAddress";
    private static final String OPTION_DESCRIPTION = "IP address for server";

    private String remoteServiceName;
    protected T remoteService;
    private Options options;
    private CommandLine commandLine;

    public Client(String remoteServiceName) throws RemoteException, NotBoundException, MalformedURLException {
        this.remoteServiceName = remoteServiceName;
        this.options = new Options();
        this.addOption(OPTION_NAME, OPTION_DESCRIPTION, true, true);
    }

    protected void lookup() throws RemoteException, NotBoundException, MalformedURLException {
        this.remoteService = (T) Naming.lookup("//" + getParameter("serverAddress").orElseThrow(IllegalArgumentException::new)
                + "/" + this.remoteServiceName);
    }

    public void parse(String[] args){
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            this.commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println( "Parsing failed.  Reason: " + e.getMessage());
            formatter.printHelp(this.remoteServiceName, options);
            System.exit(1);
        }
    }

    public void addOption(final String name, final String description, final boolean hasArguments, final boolean required) {
        Option op = new Option(name, name, hasArguments, description);
        op.setRequired(required);
        this.options.addOption(op);
    }

    public boolean hasParameter(final String name){
        return commandLine.hasOption(name);
    }

    public Optional<String> getParameter(final String name){
        if(!commandLine.hasOption(name))
            return Optional.empty();
        return Optional.ofNullable(commandLine.getOptionValue(name));
    }

}
