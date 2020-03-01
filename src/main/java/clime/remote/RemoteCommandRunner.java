package clime.remote;

import clime.CommandRunner;

import java.rmi.RemoteException;

public class RemoteCommandRunner implements CliMeRmi {

    private CommandRunner commandRunner;

    public RemoteCommandRunner(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    @Override
    public String run(String... args) throws RemoteException {
        return commandRunner.run(args);
    }
}
