package clime;

import static org.assertj.core.api.Assertions.assertThat;

import clime.CliMe;
import clime.CommandRunner;
import clime.models.ExplicitDefaultConstructor;
import clime.models.SimpleObject;
import com.google.common.collect.Sets;

import java.util.LinkedHashSet;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CommandRunnerTest {

    @Test
    void callTestObjectOneHelloFromCommandLineArgs() {
        String actualHello = new SimpleObject().hello();
        String args = "SimpleObject hello";
        CommandRunner commandRunner = new CliMe("cli", "clime.models").commandRunner();

        assertThat(commandRunner.run(args.split(" "))).isEqualTo(actualHello);
    }

    @Test
    @Disabled("Only support String arguments")
    public void callTestObjectOneGoodNumberWithParameterFromCommandLineArgs() {
        Integer goodNumber = new SimpleObject().goodNumber(100);
        String args = "SimpleObject goodNumber 100";
        CommandRunner commandRunner = new CliMe("cli", "clime.models").commandRunner();

        assertThat(commandRunner.run(args.split(" "))).isEqualTo(goodNumber.toString());
    }

    @Test
    public void callTestObjectOneGoodNumberStringWithParameterFromCommandLineArgs() {
        Integer goodNumber = new SimpleObject().goodNumberString("100");
        String args = "SimpleObject goodNumberString 100";
        CommandRunner commandRunner = new CliMe("cli", "clime.models").commandRunner();

        assertThat(commandRunner.run(args.split(" "))).isEqualTo(goodNumber.toString());
    }

    @Test
    void callTestObjectOneGreetingWithParameterFromCommandLineArgs() {
        String greet = new SimpleObject().greeting("Hey_Whats_Up");
        String args = "SimpleObject greeting Hey_Whats_Up";
        CommandRunner commandRunner = new CliMe("cli", "clime.models").commandRunner();

        assertThat(commandRunner.run(args.split(" "))).isEqualTo(greet);
    }

    @Test
    public void callTestObjectOnVoidReturnWithParameterFromCommandLineArgs() {
        CommandRunner commandRunner = new CliMe("cli", "clime.models").commandRunner();
        String args = "SimpleObject voidReturn";
        commandRunner.run(args.split(" "));

        args = "SimpleObject getNumber";
        assertThat(commandRunner.run(args.split(" "))).isEqualTo("1");
    }

    @Test
    void injectedMapCreatesDependencyContainer() {
        LinkedHashSet<Object> dependencies = Sets.newLinkedHashSet();
        SimpleObject expectedOne = new SimpleObject();
        ExplicitDefaultConstructor expectedTwo = new ExplicitDefaultConstructor();
        dependencies.add(expectedOne);
        dependencies.add(expectedTwo);
        CommandRunner commandRunner = new CliMe("bestCommandLine", dependencies).commandRunner();

        String zap = expectedTwo.getField();
        String args = "ExplicitDefaultConstructor getField";

        assertThat(commandRunner.run(args.split(" "))).isEqualTo(zap);
    }

    @Test
    void exitCommandReturnsNull() {
        LinkedHashSet<Object> dependencies = Sets.newLinkedHashSet();
        SimpleObject expectedOne = new SimpleObject();
        dependencies.add(expectedOne);
        CommandRunner commandRunner = new CliMe("bestCommandLine", dependencies).commandRunner();

        String args = "exit";

        assertThat(commandRunner.run(args.split(" "))).isNull();
    }

    @Test
    void helpSubCommandReturnsPossibleMethods() {
        String args = "SimpleObject help";
        CommandRunner commandRunner = new CliMe("cli", "clime.models").commandRunner();

        assertThat(commandRunner.run(args.split(" ")))
                .containsIgnoringCase("greeting")
                .containsIgnoringCase("string")
                .containsIgnoringCase("hello");
    }

    @Test
    void helpCommandReturnsPossibleCommands() {
        String args = "help";
        CommandRunner commandRunner = new CliMe("cli", "clime.models").commandRunner();

        assertThat(commandRunner.run(args.split(" ")))
                .containsIgnoringCase("climeinitcreator")
                .containsIgnoringCase("implieddefaultconstructor")
                .containsIgnoringCase("explicitdefaultconstructor")
                .containsIgnoringCase("exceptionthrower")
                .containsIgnoringCase("simpleObject")
                .containsIgnoringCase("getfield")
                .containsIgnoringCase("greeting")
                .containsIgnoringCase("string")
                .containsIgnoringCase("hello");
    }

    @Test
    void run_ExceptionToStringOnInvocationTarget() {
        String args = "ExceptionThrower bigSad";
        CommandRunner commandRunner = new CliMe("cli", "clime.models").commandRunner();

        assertThat(commandRunner.run(args.split(" ")))
                .containsIgnoringCase("Exception in remote method for command")
                .containsIgnoringCase("exceptionthrower")
                .containsIgnoringCase("bigsad");
    }

    @Test
    void run_BadParameterLength() {
        String args = "SimpleObject greeting Hey_Whats_Up plus nonsense";
        CommandRunner commandRunner = new CliMe("cli", "clime.models").commandRunner();

        assertThat(commandRunner.run(args.split(" ")))
                .containsIgnoringCase("climeusageexception")
                .containsIgnoringCase("greeting")
                .containsIgnoringCase("string")
                .containsIgnoringCase("hello");
    }

    @Test
    void run_BadReturnType() {
        String args = "SimpleObject goodNumber 5";
        CommandRunner commandRunner = new CliMe("cli", "clime.models").commandRunner();

        assertThat(commandRunner.run(args.split(" ")))
                .containsIgnoringCase("climeusageexception")
                .containsIgnoringCase("greeting")
                .containsIgnoringCase("string")
                .containsIgnoringCase("hello");
    }
}