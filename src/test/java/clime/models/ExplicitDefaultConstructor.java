package clime.models;

import clime.annotations.CliMeCommand;

@CliMeCommand
public class ExplicitDefaultConstructor {

    private String field;

    public ExplicitDefaultConstructor() {
        field = "Zap! /\\/";
    }

    public String getField() {
        return field;
    }

}
