package com.clime.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CliMeRmi extends Remote {

    String run(String... args) throws RemoteException;
}
