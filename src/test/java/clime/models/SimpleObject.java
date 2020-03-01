package clime.models;

import clime.annotations.CliMeCommand;

@CliMeCommand
public class SimpleObject {

    private static Integer NUMBER = 0;

    public String hello() {
        return "Hello!";
    }

    public Integer goodNumber(int number) {
        return number;
    }

    public Integer goodNumberString(String number) {
        return Integer.valueOf(number);
    }

    public String greeting(String greet) {
        return greet;
    }

    public void voidReturn() {
        NUMBER++;
    }

    public Integer getNumber() {
        return NUMBER;
    }
}
