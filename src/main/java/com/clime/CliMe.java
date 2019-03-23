package com.clime;

import com.clime.annotations.CliCommand;
import com.google.common.collect.Maps;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;

public class CliMe {

    private static Logger logger = Logger.getLogger(CliMe.class.getName());

    private Map<Class, ObjectContainer> dependencyContainer;
    private String packageToScan;
    private String prompt;

    public CliMe(String packageToScan, String prompt) {
        this.packageToScan = packageToScan;
        this.prompt = prompt;
        this.dependencyContainer = initializeObjects(new Reflections(packageToScan));
    }

    public CliMe(Set<Object> dependencies, String prompt) {
        this.dependencyContainer = Maps.newHashMap();
        this.prompt = prompt;
        dependencies.forEach(object -> dependencyContainer.put(object.getClass(), new ObjectContainer(object, getMethods(object.getClass()))));
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(prompt + " => ");
            String className = scanner.next();
            if (className.equals("exit")) {
                break;
            }
            String methodName = scanner.next();

            try {
                ObjectContainer object = dependencyContainer.get(Class.forName(className));
                System.out.println(object.getMethods().get(methodName).invoke(object.getObject()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    static Map<Class, ObjectContainer> initializeObjects(Reflections reflections) {

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CliCommand.class);
        Map<Class, ObjectContainer> dependencyContainer = Maps.newHashMap();
        for (Class clazz : annotatedClasses) {
            try {
                Map<String, Method> methods = getMethods(clazz);
                dependencyContainer.put(clazz, new ObjectContainer(clazz.getDeclaredConstructor().newInstance(), methods));
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                logger.log(Level.SEVERE, e.toString(), e);
            }
        }
        return dependencyContainer;
    }

    private static Map<String, Method> getMethods(Class clazz) {
        Map<String, Method> methods = Maps.newHashMap();
        for (Method method : clazz.getDeclaredMethods()) {
            methods.put(method.getName(), method);
        }
        return methods;
    }
}
