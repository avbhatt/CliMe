package com.clime.remote;

import static org.assertj.core.api.Assertions.assertThat;

import com.clime.CliMe;
import com.clime.models.ExplicitDefaultConstructor;
import com.clime.models.SimpleObject;
import com.clime.remote.CliMeCommandRunner;
import com.google.common.collect.Sets;
import java.util.LinkedHashSet;
import org.junit.Ignore;
import org.junit.Test;

public class CliMeCommandRunnerTest {

    @Test
    public void callTestObjectOneHelloFromCommandLineArgs() throws Exception {
        String actualHello = new SimpleObject().hello();
        String args = "SimpleObject hello";
        CliMeCommandRunner cliMeCommandRunner = new CliMe("cli", "com.clime.models").commandRunner();

        assertThat(cliMeCommandRunner.run(args.split(" "))).isEqualTo(actualHello);
    }

    @Test
    @Ignore("Only support String arguments")
    public void callTestObjectOneGoodNumberWithParameterFromCommandLineArgs() throws Exception {
        Integer goodNumber = new SimpleObject().goodNumber(100);
        String args = "SimpleObject goodNumber 100";
        CliMeCommandRunner cliMeCommandRunner = new CliMe("cli", "com.clime.models").commandRunner();

        assertThat(cliMeCommandRunner.run(args.split(" "))).isEqualTo(goodNumber.toString());
    }

    @Test
    public void callTestObjectOneGreetingWithParameterFromCommandLineArgs() throws Exception {
        String greet = new SimpleObject().greeting("Hey_Whats_Up");
        String args = "SimpleObject greeting Hey_Whats_Up";
        CliMeCommandRunner cliMeCommandRunner = new CliMe("cli", "com.clime.models").commandRunner();

        assertThat(cliMeCommandRunner.run(args.split(" "))).isEqualTo(greet);
    }

    @Test
    public void injectedMapCreatesDependencyContainer() {
        LinkedHashSet<Object> dependencies = Sets.newLinkedHashSet();
        SimpleObject expectedOne = new SimpleObject();
        ExplicitDefaultConstructor expectedTwo = new ExplicitDefaultConstructor();
        dependencies.add(expectedOne);
        dependencies.add(expectedTwo);
        CliMeCommandRunner cliMeCommandRunner = new CliMe("bestCommandLine", dependencies).commandRunner();

        String zap = expectedTwo.getField();
        String args = "ExplicitDefaultConstructor getField";

        assertThat(cliMeCommandRunner.run(args.split(" "))).isEqualTo(zap);
    }

    @Test
    public void exitCommandReturnsNull() {
        LinkedHashSet<Object> dependencies = Sets.newLinkedHashSet();
        SimpleObject expectedOne = new SimpleObject();
        dependencies.add(expectedOne);
        CliMeCommandRunner cliMeCommandRunner = new CliMe("bestCommandLine", dependencies).commandRunner();

        String args = "exit";

        assertThat(cliMeCommandRunner.run(args.split(" "))).isNull();
    }
}