package clime.models;

import clime.annotations.CliMeCommand;

@CliMeCommand
public class ImpliedDefaultConstructor {

    public String greet(String greeting) {
        return greeting;
    }
}
