package com.clime;

import com.clime.annotations.CliMeCommand;
import com.clime.annotations.CliMeInit;
import com.clime.exceptions.CliMeUsageException;
import com.clime.remote.CliMeCommandRunner;
import com.clime.remote.CliMeRmi;
import com.google.common.collect.Maps;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.reflections.Reflections;

public class CliMe {

    private static Logger logger = Logger.getLogger(CliMe.class.getName());
    private static final Pattern inputEnd = Pattern.compile("\\z");
    private CliMeCommandRunner cliMeCommandRunner;

    private Map<String, ObjectContainer> dependencyContainer;
    private String prompt;

    public CliMe(String prompt, String packageToScan) {
        this.prompt = prompt;
        this.dependencyContainer = initializeObjects(new Reflections(packageToScan));
        this.cliMeCommandRunner = new CliMeCommandRunner(dependencyContainer);
    }

    public CliMe(String prompt, Set<Object> dependencies) {
        this.prompt = prompt;
        this.dependencyContainer = initializeObjects(dependencies);
        this.cliMeCommandRunner = new CliMeCommandRunner(dependencyContainer);
    }

    public void interactive() {
        try {
            ArgCollector argCollector;
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter(inputEnd);
            while (true) {
                System.out.print(prompt + " => ");
                try {
                    argCollector = new ArgCollector(scanner.next());
                } catch (CliMeUsageException e) {
                    logger.log(Level.SEVERE, e.toString(), e);
                    continue;
                }
                Arguments arguments = argCollector.getArguments();
                if (arguments.exit()) {
                    break;
                }
                System.out.println(cliMeCommandRunner.run(arguments));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.toString(), e);
            System.out.println(e.toString());
        }
    }

    public void rmi(int port) throws RemoteException {
        CliMeRmi stub = (CliMeRmi) UnicastRemoteObject.exportObject(cliMeCommandRunner, 0);
        try {
            LocateRegistry.createRegistry(port);
            logger.log(Level.FINEST, "java RMI registry created.");
            Naming.rebind("//localhost/" + prompt, stub);
        } catch (RemoteException e) {
            logger.log(Level.FINEST, "java RMI registry already exists.");
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "prompt must be valid in a URL", e);
        }
    }

    public void rmi() throws RemoteException {
        rmi(1099);
    }

    public CliMeCommandRunner commandRunner() {
        return cliMeCommandRunner;
    }

    static Map<String, ObjectContainer> initializeObjects(Reflections reflections) {
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CliMeCommand.class);
        Map<String, ObjectContainer> dependencyContainer = Maps.newHashMap();
        for (Class clazz : annotatedClasses) {
            String className = clazz.getSimpleName();
            if (dependencyContainer.containsKey(className.toLowerCase())) {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException(clazz.getName()
                    + " command conflicts with "
                    + dependencyContainer.get(clazz.getSimpleName().toLowerCase()).getObject().getClass().getName());
                logger.log(Level.SEVERE, illegalArgumentException.toString(), illegalArgumentException);
                throw illegalArgumentException;
            }
            dependencyContainer.put(className.toLowerCase(), createObjectContainer(clazz));
        }
        return dependencyContainer;
    }

    private static ObjectContainer createObjectContainer(Class clazz) {
        Map<String, Method> methods = Maps.newHashMap();
        Object o = null;

        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(CliMeInit.class)) {
                    if (o == null) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            if (method.getReturnType().equals(clazz)) {
                                if (method.getParameterCount() == 0) {
                                    o = method.invoke(null);
                                } else {
                                    IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                                        "CliMeInit requires a no-arg initializer");
                                    logger.log(Level.SEVERE, illegalArgumentException.toString(), illegalArgumentException);
                                    throw illegalArgumentException;
                                }
                            } else {
                                RuntimeException runtimeException = new RuntimeException(
                                    "CliMeInit requires initializer to return the same type as the object (" + clazz.getName() + ")");
                                logger.log(Level.SEVERE, runtimeException.toString(), runtimeException);
                                throw runtimeException;
                            }
                        } else {
                            RuntimeException runtimeException = new RuntimeException("CliMeInit requires initializer to be static");
                            logger.log(Level.SEVERE, runtimeException.toString(), runtimeException);
                            throw runtimeException;
                        }
                    } else {
                        RuntimeException runtimeException = new RuntimeException("CliMeInit requires at most one initializer");
                        logger.log(Level.SEVERE, runtimeException.toString(), runtimeException);
                        throw runtimeException;
                    }
                } else {
                    addIfMethodNotPresent(methods, method);
                }
            }
            if (o == null) {
                o = clazz.getDeclaredConstructor().newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            RuntimeException runtimeException = new RuntimeException("Error in creating object of type " + clazz.getName(), e);
            logger.log(Level.SEVERE, "CliMe could not create object of type "
                + clazz.getName()
                + "\nPerhaps try CliMe(String prompt, Set<Object> dependencies) to manually inject command objects", runtimeException);
            throw runtimeException;
        }

        return new ObjectContainer(o, methods);
    }

    static Map<String, ObjectContainer> initializeObjects(Set<Object> dependencies) {
        Map<String, ObjectContainer> dependencyContainer = Maps.newHashMap();
        for (Object o : dependencies) {
            if (dependencyContainer.containsKey(o.getClass().getSimpleName().toLowerCase())) {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException(o.getClass().getName()
                    + " command conflicts with "
                    + dependencyContainer.get(o.getClass().getSimpleName().toLowerCase()).getObject().getClass().getName());
                logger.log(Level.SEVERE, "CliMe requires unique class (command) names", illegalArgumentException);
                throw illegalArgumentException;
            } else {
                dependencyContainer.put(o.getClass().getSimpleName().toLowerCase(), new ObjectContainer(o, getMethods(o.getClass())));
            }
        }
        return dependencyContainer;
    }

    private static Map<String, Method> getMethods(Class clazz) {
        Map<String, Method> methods = Maps.newHashMap();
        for (Method method : clazz.getDeclaredMethods()) {
            addIfMethodNotPresent(methods, method);
        }
        return methods;
    }

    private static Map<String, Method> addIfMethodNotPresent(Map<String, Method> methods, Method method) {
        String methodName = method.getName();
        if (methods.containsKey(methodName.toLowerCase())) {
            RuntimeException runtimeException = new RuntimeException(
                "CliMe requires unique method (subcommand) names. Method " + methodName + " already defined in class (command)" + method
                    .getDeclaringClass().getSimpleName());
            logger.log(Level.SEVERE, runtimeException.toString(), runtimeException);
            throw runtimeException;
        }
        methods.put(methodName.toLowerCase(), method);

        return methods;
    }

}
