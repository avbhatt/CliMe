package com.clime.models;

import com.clime.annotations.CliMeCommand;

@CliMeCommand
public class ImpliedDefaultConstructor {

    public String greet(String greeting) {
        return greeting;
    }
}
