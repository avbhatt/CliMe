package com.clime;

import com.google.common.collect.Lists;
import java.util.List;

public class Arguments {

    private String command;
    private String subCommand;
    private List<String> parameters;
    private boolean exit;

    public Arguments(Builder builder) {
        command = builder.command;
        subCommand = builder.subCommand;
        parameters = builder.parameters;
        exit = builder.exit;
    }


    public String command() {
        return command;
    }

    public String subCommand() {
        return subCommand;
    }

    public List<String> parameters() {
        return parameters;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean exit() {
        return exit;
    }

    public static class Builder {

        private String command;
        private String subCommand;
        private List<String> parameters;
        private boolean exit;

        public Builder withCommand(String command) {
            this.command = command.toLowerCase();
            return this;
        }

        public Builder withSubCommand(String subCommand) {
            this.subCommand = subCommand.toLowerCase();
            return this;
        }

        public Builder withParameter(String param) {
            if (parameters == null) {
                parameters = Lists.newLinkedList();
            }
            parameters.add(param);
            return this;
        }

        public void exit() {
            exit = true;
        }

        public Arguments build() {
            return new Arguments(this);
        }
    }
}
