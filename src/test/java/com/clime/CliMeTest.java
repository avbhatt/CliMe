package com.clime;

import static org.assertj.core.api.Assertions.assertThat;

import com.clime.annotations.CliCommand;
import com.clime.models.TestObjectOne;
import com.clime.models.TestObjectThree;
import com.clime.models.TestObjectTwo;
import com.google.common.collect.Sets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import org.reflections.Reflections;

public class CliMeTest {

    @Test
    public void dependencyContainerKeyedOnAnnotatedClass() throws Exception {
        Reflections mockReflections = Mockito.mock(Reflections.class);
        Set<Class<?>> expectedClasses = Sets.newHashSet(HashMap.class, Object.class, LinkedHashSet.class);
        Mockito.when(mockReflections.getTypesAnnotatedWith(CliCommand.class)).thenReturn(expectedClasses);
        Map<Class, ObjectContainer> classToObject = CliMe.initializeObjects(mockReflections);
        assertThat(classToObject).containsKeys(HashMap.class, Object.class, LinkedHashSet.class);
    }

    @Test
    public void dependencyContainerHasObjectOfKeyType() throws Exception {
        Reflections mockReflections = Mockito.mock(Reflections.class);
        Set<Class<?>> expectedClasses = Sets.newHashSet(HashMap.class, Object.class, LinkedHashSet.class);
        Mockito.when(mockReflections.getTypesAnnotatedWith(CliCommand.class)).thenReturn(expectedClasses);
        Map<Class, ObjectContainer> classToObject = CliMe.initializeObjects(mockReflections);
        assertThat(classToObject.get(HashMap.class).getObject()).isExactlyInstanceOf(HashMap.class);
        assertThat(classToObject.get(Object.class).getObject()).isExactlyInstanceOf(Object.class);
        assertThat(classToObject.get(LinkedHashSet.class).getObject()).isExactlyInstanceOf(LinkedHashSet.class);
    }

    // IT Tests

    @Test
    public void dependencyHasMethodNameAsKey() throws Exception {
        Map<Class, ObjectContainer> classMethods = CliMe.initializeObjects(new Reflections("com.clime.models"));
        assertThat(classMethods).containsKeys(TestObjectOne.class, TestObjectTwo.class, TestObjectThree.class);
        assertThat(classMethods.get(TestObjectOne.class).getMethods()).containsOnlyKeys("hello", "goodNumber");
        assertThat(classMethods.get(TestObjectThree.class).getMethods()).containsOnlyKeys("greet");
        assertThat(classMethods.get(TestObjectTwo.class).getMethods()).containsOnlyKeys("getField");
    }

    @Test
    public void callTestObjectOneHelloFromCommandLineArgs() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        System.setOut(new PrintStream(outContent));
        String args = "com.clime.models.TestObjectOne hello exit";
        System.setIn(new ByteArrayInputStream(args.getBytes()));

        CliMe cliMe = new CliMe("com.clime.models", "cli");
        cliMe.run();

        String actualHello = new TestObjectOne().hello();

        String output = outContent.toString();
        assertThat(output).contains("cli =>");
        assertThat(output).contains(actualHello);

        System.setOut(originalOut);
        System.setIn(originalIn);
    }


    @Test
    public void injectedMapCreatesDependencyContainer() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        System.setOut(new PrintStream(outContent));
        String args = "com.clime.models.TestObjectOne hello\n";
        args += "com.clime.models.TestObjectTwo getField\n";
//        args += "com.clime.models.TestObjectThree greet\n";
        args += "exit";
        System.setIn(new ByteArrayInputStream(args.getBytes()));

        LinkedHashSet<Object> dependencies = Sets.newLinkedHashSet();
        TestObjectOne expectedOne = new TestObjectOne();
        TestObjectTwo expectedTwo = new TestObjectTwo();
//        TestObjectThree expectedThree = new TestObjectThree();
        dependencies.add(expectedOne);
        dependencies.add(expectedTwo);
//        dependencies.add(expectedThree);

        CliMe cliMe = new CliMe(dependencies, "bestCommandLine");
        cliMe.run();

        String actualHello = expectedOne.hello();
        String zap = expectedTwo.getField();
//        String greet = expectedThree.greet("What's going on?");

        String output = outContent.toString();
        assertThat(output).contains("bestCommandLine =>");
        assertThat(output).contains(actualHello);
        assertThat(output).contains(zap);
//        assertThat(output).contains(greet);

        System.setOut(originalOut);
        System.setIn(originalIn);
    }

}