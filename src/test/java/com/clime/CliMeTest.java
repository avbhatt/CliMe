package com.clime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import com.clime.annotations.CliMeCommand;
import com.clime.badmodels.dupemodels.DupeObject;
import com.clime.badmodels.multimethod.SameMethodName;
import com.clime.models.CliMeInitCreator;
import com.clime.models.ExplicitDefaultConstructor;
import com.clime.models.SimpleObject;
import com.google.common.collect.Sets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reflections.Reflections;

class CliMeTest {

    @Test
    void dependencyContainerKeyedOnAnnotatedClass() {
        Reflections mockReflections = Mockito.mock(Reflections.class);
        Set<Class<?>> expectedClasses = Sets.newHashSet(CliMeInitCreator.class, ExplicitDefaultConstructor.class, SimpleObject.class);
        Mockito.when(mockReflections.getTypesAnnotatedWith(CliMeCommand.class)).thenReturn(expectedClasses);
        Map<String, ObjectContainer> classToObject = CliMe.initializeObjects(mockReflections);
        assertThat(classToObject).containsKeys("CliMeInitCreator".toLowerCase(), "ExplicitDefaultConstructor".toLowerCase(), "SimpleObject".toLowerCase());
    }

    @Test
    void dependencyContainerHasObjectOfKeyType() {
        Reflections mockReflections = Mockito.mock(Reflections.class);
        Set<Class<?>> expectedClasses = Sets.newHashSet(CliMeInitCreator.class, ExplicitDefaultConstructor.class, SimpleObject.class);
        Mockito.when(mockReflections.getTypesAnnotatedWith(CliMeCommand.class)).thenReturn(expectedClasses);
        Map<String, ObjectContainer> classToObject = CliMe.initializeObjects(mockReflections);
        assertThat(classToObject.get("CliMeInitCreator".toLowerCase()).getObject()).isExactlyInstanceOf(CliMeInitCreator.class);
        assertThat(classToObject.get("ExplicitDefaultConstructor".toLowerCase()).getObject()).isExactlyInstanceOf(ExplicitDefaultConstructor.class);
        assertThat(classToObject.get("SimpleObject".toLowerCase()).getObject()).isExactlyInstanceOf(SimpleObject.class);
    }

    @Test
    void throwIllegalArgExceptionOnDuplicateConstructorInjection() {
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
    void throwIllegalArgExceptionOnDuplicatesAnnotationScan() {
        try {
            new CliMe("cli", "com.clime.badmodels.dupemodels");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void dependencyHasMethodNameAsKey() {
        Map<String, ObjectContainer> classMethods = CliMe.initializeObjects(new Reflections("com.clime.models"));
        assertThat(classMethods).containsKeys("SimpleObject".toLowerCase(), "ExplicitDefaultConstructor".toLowerCase(), "ImpliedDefaultConstructor".toLowerCase());
        assertThat(classMethods.get("SimpleObject".toLowerCase()).getMethods()).containsOnlyKeys("hello", "goodnumber", "greeting");
        assertThat(classMethods.get("ImpliedDefaultConstructor".toLowerCase()).getMethods()).containsOnlyKeys("greet");
        assertThat(classMethods.get("ExplicitDefaultConstructor".toLowerCase()).getMethods()).containsOnlyKeys("getfield");
    }

    @Test
    void dependencyCreationByAnnotation() {
        Map<String, ObjectContainer> dependencyContainer = CliMe.initializeObjects(new Reflections("com.clime.models"));
        assertThat(CliMeInitCreator.class.cast(dependencyContainer.get("CliMeInitCreator".toLowerCase()).getObject()).listNames()).contains("CorrectName :)");
    }

    @Test
    void throwIllegalArgumentWhenParametersInInit() {
        try {
            CliMe.initializeObjects(new Reflections("com.clime.badmodels.parameters"));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void throwRuntimeExceptionWhenInvalidReturn() {
        try {
            CliMe.initializeObjects(new Reflections("com.clime.badmodels.badreturn"));
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void throwRuntimeExceptionWhenNonStatic() {
        try {
            CliMe.initializeObjects(new Reflections("com.clime.badmodels.nonstatic"));
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void throwRuntimeExceptionWhenMultipleInitializers() {
        try {
            CliMe.initializeObjects(new Reflections("com.clime.badmodels.multi"));
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void throwRuntimeExceptionWhenMultipleMethodsWithSameName() {
        try {
            CliMe.initializeObjects(new Reflections("com.clime.badmodels.multimethod"));
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(RuntimeException.class);
        }
    }

    @Test
    void throwRuntimeExceptionWhenMultipleMethodsWithSameNameManualInject() {
        try {
            CliMe.initializeObjects(Sets.newHashSet(new SameMethodName("name")));
            failBecauseExceptionWasNotThrown(RuntimeException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(RuntimeException.class);
        }
    }

    // Integration Tests

    @Test
    void callTestObjectOneHelloFromCommandLineArgs() {
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String args = "SimpleObject hello";
        ByteArrayInputStream inputContent = new ByteArrayInputStream(args.getBytes());
        System.setIn(inputContent);

        CliMe cliMe = new CliMe("cli", "com.clime.models");
        cliMe.interactive();

        String actualHello = new SimpleObject().hello();

        String output = outContent.toString();
        assertThat(output).contains(actualHello);

        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    @Disabled("Only Supports String Args")
    public void callTestObjectOneGoodNumberWithParameterFromCommandLineArgs() {
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String args = "SimpleObject goodNumber 100";
        ByteArrayInputStream inputContent = new ByteArrayInputStream(args.getBytes());
        System.setIn(inputContent);

        CliMe cliMe = new CliMe("cli", "com.clime.models");
        cliMe.interactive();

        Integer goodNumber = new SimpleObject().goodNumber(100);

        String output = outContent.toString();
        assertThat(output).contains(goodNumber.toString());

        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void callTestObjectOneGreetingWithParameterFromCommandLineArgs() {
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String args = "SimpleObject greeting Hey_Whats_Up";
        ByteArrayInputStream inputContent = new ByteArrayInputStream(args.getBytes());
        System.setIn(inputContent);

        CliMe cliMe = new CliMe("cli", "com.clime.models");
        cliMe.interactive();

        String greet = new SimpleObject().greeting("Hey_Whats_Up");

        String output = outContent.toString();
        assertThat(output).contains(greet);

        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void injectedMapCreatesDependencyContainer() {
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String args = "ExplicitDefaultConstructor getField\n";
        ByteArrayInputStream inputContent = new ByteArrayInputStream(args.getBytes());
        System.setIn(inputContent);

        LinkedHashSet<Object> dependencies = Sets.newLinkedHashSet();
        SimpleObject expectedOne = new SimpleObject();
        ExplicitDefaultConstructor expectedTwo = new ExplicitDefaultConstructor();
        dependencies.add(expectedOne);
        dependencies.add(expectedTwo);

        CliMe cliMe = new CliMe("bestCommandLine", dependencies);
        cliMe.interactive();

        String zap = expectedTwo.getField();

        String output = outContent.toString();
        assertThat(output).contains(zap);

        System.setOut(originalOut);
        System.setIn(originalIn);
    }
}