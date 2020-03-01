package clime;

import clime.exceptions.CliMeUsageException;
import clime.remote.CliMeRmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandRunner {

    private static Logger logger = Logger.getLogger(CommandRunner.class.getName());
    private Map<String, ObjectContainer> dependencyContainer;

    public CommandRunner(Map<String, ObjectContainer> dependencyContainer) {
        this.dependencyContainer = dependencyContainer;
    }

    public String run(String... args) {
        try {
            ArgCollector argCollector = new ArgCollector(String.join(" ", args));
            return run(argCollector.getArguments());
        } catch (CliMeUsageException e) {
            logger.log(Level.SEVERE, e.toString(), e);
            return e.toString();
        }
    }

    public String run(Arguments arguments) throws CliMeUsageException {
        if (arguments.exit()) {
            return null;
        }
        if (arguments.help()) {
            return buildHelpMessage();
        }
        String className = arguments.command();
        if (arguments.helpSubCommand()) {
            return buildHelpMessageForCommand(className);
        }

        String methodName = arguments.subCommand();

        try {
            ObjectContainer object = dependencyContainer.get(className.toLowerCase());
            Method method = object.getMethods().get(methodName.toLowerCase());
            Parameter[] parameters = method.getParameters();
            if (parameters.length == 0) {
                Object result = method.invoke(object.getObject());
                return result != null ? result.toString() : null;
            } else {
                Object[] methodArgs = new Object[parameters.length];
                List<String> commandLineArgs = arguments.parameters();
                if (parameters.length != commandLineArgs.size()) {
                    throw new CliMeUsageException(
                        methodName + " requires " + commandLineArgs.size() + " parameters\n" + buildHelpMessageForCommand(className));
                }
                for (int i = 0; i < commandLineArgs.size(); i++) {
                    if (parameters[i].getType() != String.class) {
                        throw new CliMeUsageException("CliMe requires String parameter types\n" + buildHelpMessageForCommand(className));
                    }
                    methodArgs[i] = commandLineArgs.get(i);
                }
                return method.invoke(object.getObject(), methodArgs).toString();
            }
        } catch (IllegalAccessException e) {
            throw new CliMeUsageException("IllegalAccess for command " + className + " and subcommand " + methodName, e);
        } catch (InvocationTargetException e) {
            throw new CliMeUsageException("Exception in remote method for command " + className + " and subcommand " + methodName, e);
        }
    }

    private String buildHelpMessage() {
        StringBuilder cliHelp = dependencyContainer.keySet().stream()
            .reduce(new StringBuilder(),
                (helpMessage, command) -> helpMessage.append(buildHelpMessageForCommand(command)).append("\n"),
                (a, b) -> a.append("\n").append(b));
        cliHelp.insert(0, "All Commands\n");
        return cliHelp.toString();
    }

    private String buildHelpMessageForCommand(String className) {
        ObjectContainer object = dependencyContainer.get(className.toLowerCase());
        StringBuilder commandHelp = object.getMethods().entrySet().stream()
            .filter(entry -> entry.getValue().getReturnType().equals(String.class) || entry.getValue().getReturnType().equals(Void.class))
            .reduce(new StringBuilder(),
                (helpMessage, entry) -> helpMessage.append("\t\t").append(entry.getKey()).append(" (")
                    .append(methodParameterTypes(entry.getValue()))
                    .append(") ").append(entry.getValue().getReturnType()).append("\n"),
                (a, b) -> a.append("\n").append(b));
        commandHelp.insert(0, "\t" + className + "\n");
        return commandHelp.toString();
    }

    private String methodParameterTypes(Method method) {
        if (method.getParameterCount() == 0) {
            return "";
        } else {
            StringBuilder parameterTypesMessage = new StringBuilder();
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < method.getParameterCount() - 1; i++) {
                parameterTypesMessage.append(parameterTypes[i]).append(" ");
            }
            parameterTypesMessage.append(parameterTypes[method.getParameterCount() - 1]);
            return parameterTypesMessage.toString();
        }
    }

}
