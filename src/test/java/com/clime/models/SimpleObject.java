package com.clime.models;

import com.clime.annotations.CliMeCommand;

@CliMeCommand
public class SimpleObject {

    public String hello() {
        return "Hello!";
    }

    public Integer goodNumber(Integer number) {
        return number;
    }
}
