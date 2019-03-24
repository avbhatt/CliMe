package com.clime;

import com.clime.annotations.CliMeCommand;
import com.clime.annotations.CliMeInit;
import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.reflections.Reflections;

public class CliMe {

	private static Logger logger = Logger.getLogger(CliMe.class.getName());

	private Map<String, ObjectContainer> dependencyContainer;
	private String prompt;

	public CliMe(String prompt, String packageToScan) {
		this.prompt = prompt;
		this.dependencyContainer = initializeObjects(new Reflections(packageToScan));
	}

	public CliMe(String prompt, Set<Object> dependencies) {
		this.prompt = prompt;
		this.dependencyContainer = initializeObjects(dependencies);
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
				ObjectContainer object = dependencyContainer.get(className);
				System.out.println(object.getMethods().get(methodName).invoke(object.getObject()));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	static Map<String, ObjectContainer> initializeObjects(Reflections reflections) {
		Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CliMeCommand.class);
		Map<String, ObjectContainer> dependencyContainer = Maps.newHashMap();
		for (Class clazz : annotatedClasses) {
			String className = clazz.getSimpleName();
			if (dependencyContainer.containsKey(className)) {
				IllegalArgumentException illegalArgumentException = new IllegalArgumentException(clazz.getName()
						+ " command conflicts with "
						+ dependencyContainer.get(clazz.getSimpleName()).getObject().getClass().getName());
				logger.log(Level.SEVERE, illegalArgumentException.toString(), illegalArgumentException);
				throw illegalArgumentException;
			}
			dependencyContainer.put(className, createObjectContainer(clazz));
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
									IllegalArgumentException illegalArgumentException = new IllegalArgumentException("CliMeInit requires a no-arg initializer");
									logger.log(Level.SEVERE, illegalArgumentException.toString(), illegalArgumentException);
									throw illegalArgumentException;
								}
							} else {
								RuntimeException runtimeException = new RuntimeException("CliMeInit requires initializer to return the same type as the object (" + clazz.getName() + ")");
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
					methods.put(method.getName(), method);
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
			if (dependencyContainer.containsKey(o.getClass().getSimpleName())) {
				IllegalArgumentException illegalArgumentException = new IllegalArgumentException(o.getClass().getName()
						+ " command conflicts with "
						+ dependencyContainer.get(o.getClass().getSimpleName()).getObject().getClass().getName());
				logger.log(Level.SEVERE, "CliMe requires unique class (command) names", illegalArgumentException);
				throw illegalArgumentException;
			} else {
				dependencyContainer.put(o.getClass().getSimpleName(), new ObjectContainer(o, getMethods(o.getClass())));
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
