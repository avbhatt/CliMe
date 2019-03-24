package com.clime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import com.clime.annotations.CliMeCommand;
import com.clime.badmodels.dupemodels.DupeObject;
import com.clime.models.CliMeInitCreator;
import com.clime.models.ExplicitDefaultConstructor;
import com.clime.models.SimpleObject;
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
        Mockito.when(mockReflections.getTypesAnnotatedWith(CliMeCommand.class)).thenReturn(expectedClasses);
        Map<String, ObjectContainer> classToObject = CliMe.initializeObjects(mockReflections);
        assertThat(classToObject).containsKeys("HashMap", "Object", "LinkedHashSet");
    }

    @Test
    public void dependencyContainerHasObjectOfKeyType() throws Exception {
        Reflections mockReflections = Mockito.mock(Reflections.class);
        Set<Class<?>> expectedClasses = Sets.newHashSet(HashMap.class, Object.class, LinkedHashSet.class);
        Mockito.when(mockReflections.getTypesAnnotatedWith(CliMeCommand.class)).thenReturn(expectedClasses);
        Map<String, ObjectContainer> classToObject = CliMe.initializeObjects(mockReflections);
        assertThat(classToObject.get("HashMap").getObject()).isExactlyInstanceOf(HashMap.class);
        assertThat(classToObject.get("Object").getObject()).isExactlyInstanceOf(Object.class);
        assertThat(classToObject.get("LinkedHashSet").getObject()).isExactlyInstanceOf(LinkedHashSet.class);
    }

    @Test
    public void throwIllegalArgExceptionOnDuplicateConstructorInjection() {
        LinkedHashSet<Object> dependencies = Sets.newLinkedHashSet();
        com.clime.badmodels.dupemodels.dupe.DupeObject badDupe1 = new com.clime.badmodels.dupemodels.dupe.DupeObject();
        DupeObject badDupe = new DupeObject();
        dependencies.add(badDupe1);
        dependencies.add(badDupe);
        try {
            new CliMe("cli", dependencies);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void throwIllegalArgExceptionOnDuplicatesAnnotationScan() {
        try {
            new CliMe("cli", "com.clime.badmodels.dupemodels");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void dependencyHasMethodNameAsKey() throws Exception {
        Map<String, ObjectContainer> classMethods = CliMe.initializeObjects(new Reflections("com.clime.models"));
        assertThat(classMethods).containsKeys("SimpleObject", "ExplicitDefaultConstructor", "ImpliedDefaultConstructor");
        assertThat(classMethods.get("SimpleObject").getMethods()).containsOnlyKeys("hello", "goodNumber");
        assertThat(classMethods.get("ImpliedDefaultConstructor").getMethods()).containsOnlyKeys("greet");
        assertThat(classMethods.get("ExplicitDefaultConstructor").getMethods()).containsOnlyKeys("getField");
    }

    @Test
    public void dependencyCreationByAnnotation() {
        Map<String, ObjectContainer> dependencyContainer = CliMe.initializeObjects(new Reflections("com.clime.models"));
        assertThat(CliMeInitCreator.class.cast(dependencyContainer.get("CliMeInitCreator").getObject()).listNames()).contains("CorrectName :)");
    }

    @Test
    public void throwIllegalArgumentWhenParametersInInit() {
        try {
            CliMe.initializeObjects(new Reflections("com.clime.badmodels.parameters"));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void throwRuntimeExceptionWhenInvalidReturn() {
        try {
            CliMe.initializeObjects(new Reflections("com.clime.badmodels.badreturn"));
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(RuntimeException.class);
        }
    }

    @Test
    public void throwRuntimeExceptionWhenNonStatic() {
        try {
            CliMe.initializeObjects(new Reflections("com.clime.badmodels.nonstatic"));
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(RuntimeException.class);
        }
    }

    @Test
    public void throwRuntimeExceptionWhenMultipleInitializers() {
        try {
            CliMe.initializeObjects(new Reflections("com.clime.badmodels.multi"));
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(RuntimeException.class);
        }
    }

    // Integration Tests

    @Test
    public void callTestObjectOneHelloFromCommandLineArgs() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        System.setOut(new PrintStream(outContent));
        String args = "SimpleObject hello exit";
        System.setIn(new ByteArrayInputStream(args.getBytes()));

        CliMe cliMe = new CliMe("cli", "com.clime.models");
        cliMe.run();

        String actualHello = new SimpleObject().hello();

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
        String args = "SimpleObject hello\n";
        args += "ExplicitDefaultConstructor getField\n";
//        args += "ImpliedDefaultConstructor greet\n";
        args += "exit";
        System.setIn(new ByteArrayInputStream(args.getBytes()));

        LinkedHashSet<Object> dependencies = Sets.newLinkedHashSet();
        SimpleObject expectedOne = new SimpleObject();
        ExplicitDefaultConstructor expectedTwo = new ExplicitDefaultConstructor();
//        ImpliedDefaultConstructor expectedThree = new ImpliedDefaultConstructor();
        dependencies.add(expectedOne);
        dependencies.add(expectedTwo);
//        dependencies.add(expectedThree);

        CliMe cliMe = new CliMe("bestCommandLine", dependencies);
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