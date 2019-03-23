package com.clime.models;

import com.clime.annotations.CliCommand;

@CliCommand
public class TestObjectTwo {

    private String field;

    public TestObjectTwo() {
        field = "Zap! /\\/";
    }

    public String getField() {
        return field;
    }

}
