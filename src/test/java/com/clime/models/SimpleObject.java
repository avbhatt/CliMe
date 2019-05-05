package com.clime.models;

import com.clime.annotations.CliMeCommand;

@CliMeCommand
public class SimpleObject {

    public String hello() {
        return "Hello!";
    }

    public Integer goodNumber(int number) {
        return number;
    }

    public String greeting(String greet) {
        return greet;
    }
}
