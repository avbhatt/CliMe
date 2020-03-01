package clime;

import com.google.common.collect.Lists;
import java.util.List;

class Arguments {

    private String command;
    private String subCommand;
    private List<String> parameters;
    private boolean exit;
    private boolean help;
    private boolean helpSubCommand;

    private Arguments(Builder builder) {
        command = builder.command;
        subCommand = builder.subCommand;
        parameters = builder.parameters;
        exit = builder.exit;
        help = builder.help;
        helpSubCommand = builder.helpSubCommand;
    }


    String command() {
        return command;
    }

    String subCommand() {
        return subCommand;
    }

    List<String> parameters() {
        return parameters;
    }

    static Builder newBuilder() {
        return new Builder();
    }

    boolean exit() {
        return exit;
    }

    boolean help() {
        return help;
    }

    boolean helpSubCommand() {
        return helpSubCommand;
    }

    static class Builder {

        private String command;
        private String subCommand;
        private List<String> parameters;
        private boolean exit;
        private boolean help;
        private boolean helpSubCommand;

        Builder withCommand(String command) {
            this.command = command.toLowerCase();
            return this;
        }

        Builder withSubCommand(String subCommand) {
            this.subCommand = subCommand.toLowerCase();
            return this;
        }

        Builder withParameter(String param) {
            if (parameters == null) {
                parameters = Lists.newLinkedList();
            }
            parameters.add(param);
            return this;
        }

        void exit() {
            exit = true;
        }

        void help() {
            help = true;
        }

        void helpSubCommand() {
            helpSubCommand = true;
        }

        Arguments build() {
            return new Arguments(this);
        }
    }
}
