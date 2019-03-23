package com.clime.models;

import com.clime.annotations.CliCommand;

@CliCommand
public class TestObjectOne {

    public String hello() {
        return "Hello!";
    }

    public Integer goodNumber(Integer number) {
        return number;
    }
}
