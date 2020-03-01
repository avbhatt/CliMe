package clime.models;

import clime.annotations.CliMeCommand;

@CliMeCommand
public class ExceptionThrower {

    public String bigSad() {
        throw new RuntimeException("Intended Invocation Target Big Sad");
    }

}
