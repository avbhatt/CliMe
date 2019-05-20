package com.clime.remote;

import com.clime.ArgCollector;
import com.clime.Arguments;
import com.clime.ObjectContainer;
import com.clime.exceptions.CliMeUsageException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CliMeCommandRunner implements CliMeRmi {

    private static Logger logger = Logger.getLogger(CliMeCommandRunner.class.getName());
    private Map<String, ObjectContainer> dependencyContainer;

    public CliMeCommandRunner(Map<String, ObjectContainer> dependencyContainer) {
        this.dependencyContainer = dependencyContainer;
    }

    @Override
    public String run(String... args) {
        ArgCollector argCollector;
        try {
            argCollector = new ArgCollector(String.join(" ", args));
        } catch (CliMeUsageException e) {
            logger.log(Level.SEVERE, e.toString(), e);
            return e.toString();
        }
        Arguments arguments = argCollector.getArguments();

        return run(arguments);
    }

    public String run(Arguments arguments) {
        if (arguments.exit()) {
            return null;
        }
        String className = arguments.command();

        String methodName = arguments.subCommand();

        try {
            ObjectContainer object = dependencyContainer.get(className.toLowerCase());
            Method method = object.getMethods().get(methodName.toLowerCase());
            Parameter[] parameters = method.getParameters();
            if (parameters.length == 0) {
                return method.invoke(object.getObject()).toString();
            } else {
                Object[] methodArgs = new Object[parameters.length];
                List<String> commandLineArgs = arguments.parameters();
                if (parameters.length != commandLineArgs.size()) {
                    // TODO: THROW
                }
                for (int i = 0; i < commandLineArgs.size(); i++) {
                    if (parameters[i].getType() != String.class) {
                        // TODO: THROW
                    }
                    methodArgs[i] = commandLineArgs.get(i);
                }
                methodArgs[0] = arguments.parameters().get(0);
                return method.invoke(object.getObject(), methodArgs).toString();
            }
        } catch (IllegalAccessException e) {
            return e.toString();
        } catch (InvocationTargetException e) {
            return e.toString();
        }
    }

}
