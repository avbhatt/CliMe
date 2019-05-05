package com.clime;

import com.clime.exceptions.CliMeUsageException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgCollector {

    private Arguments.Builder argBuilder = Arguments.newBuilder();
    private static final Pattern inputEnd = Pattern.compile("\\z");
    private static final Pattern param = Pattern.compile("(\".*?\"|\\S+)");

    public ArgCollector(String argLine) throws CliMeUsageException {
        buildArguments(argLine);
    }

    private void buildArguments(String argLine) throws CliMeUsageException {
        Scanner scanner = new Scanner(argLine);
        if (scanner.hasNext()) {
            String command = scanner.next();
            if (!command.equalsIgnoreCase("exit") || scanner.hasNext()) {
                argBuilder.withCommand(command);
                if (scanner.hasNext()) {
                    argBuilder.withSubCommand(scanner.next());
                } else {
                    throw new CliMeUsageException("<SUB_COMMAND> required as second argument.");
                }
                scanner.useDelimiter(inputEnd);
                if (scanner.hasNext()) {
                    buildParameters(scanner.next());
                }
            } else {
                argBuilder.exit();
            }
        } else {
            throw new CliMeUsageException("<COMMAND> required as first argument.");
        }
    }

    private void buildParameters(String parameters) throws CliMeUsageException {
        parameters = parameters.trim();
        if (parameters.isEmpty()) {
            return;
        }
        Matcher matcher = param.matcher(parameters);
        while (matcher.find()) {
            argBuilder.withParameter(getParamValue(matcher.group()));
        }
    }

    private String getParamValue(String paramValue) {
        if (paramValue.charAt(0) == '"' && paramValue.charAt(paramValue.length() - 1) == '"') {
            paramValue = paramValue.substring(1, paramValue.length() - 1);
        }
        return paramValue;
    }

    public Arguments getArguments() {
        return argBuilder.build();
    }

}
