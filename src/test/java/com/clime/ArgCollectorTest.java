package com.clime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import com.clime.exceptions.CliMeUsageException;
import org.junit.jupiter.api.Test;

class ArgCollectorTest {

    private ArgCollector argCollector;

    @Test
    void collectClassAndMethod() throws Exception {
        String command = "bestCommand okSubCommand";
        argCollector = new ArgCollector(command);

        Arguments arguments = argCollector.getArguments();

        assertThat(arguments.command()).isEqualToIgnoringCase("bestCommand");
        assertThat(arguments.subCommand()).isEqualToIgnoringCase("okSubCommand");
        assertThat(arguments.exit()).isFalse();
        assertThat(arguments.parameters()).isNull();
    }

    @Test
    void collectClassAndMethodAndParameter() throws Exception {
        String command = "bestCommand okSubCommand val1";
        argCollector = new ArgCollector(command);

        Arguments arguments = argCollector.getArguments();

        assertThat(arguments.command()).isEqualToIgnoringCase("bestCommand");
        assertThat(arguments.subCommand()).isEqualToIgnoringCase("okSubCommand");
        assertThat(arguments.parameters().get(0)).isEqualToIgnoringCase("val1");
        assertThat(arguments.parameters().size()).isEqualTo(1);
        assertThat(arguments.exit()).isFalse();
    }

    @Test
    void collectClassAndMethodAndParameters() throws Exception {
        String command = "bestCommand okSubCommand val1 me";
        argCollector = new ArgCollector(command);

        Arguments arguments = argCollector.getArguments();

        assertThat(arguments.command()).isEqualToIgnoringCase("bestCommand");
        assertThat(arguments.subCommand()).isEqualToIgnoringCase("okSubCommand");
        assertThat(arguments.parameters().get(0)).isEqualToIgnoringCase("val1");
        assertThat(arguments.parameters().get(1)).isEqualToIgnoringCase("me");
        assertThat(arguments.parameters().size()).isEqualTo(2);
        assertThat(arguments.exit()).isFalse();
    }

    @Test
    void exitTrueWhenExitCommand() throws Exception {
        String command = "exit";
        argCollector = new ArgCollector(command);

        Arguments arguments = argCollector.getArguments();

        assertThat(arguments.command()).isNull();
        assertThat(arguments.subCommand()).isNull();
        assertThat(arguments.parameters()).isNull();
        assertThat(arguments.exit()).isTrue();
    }

    @Test
    void exitFalseWhenExitCommandAndMore() throws Exception {
        String command = "exit subCommand 123";
        argCollector = new ArgCollector(command);

        Arguments arguments = argCollector.getArguments();

        assertThat(arguments.command()).isEqualToIgnoringCase("exit");
        assertThat(arguments.subCommand()).isEqualToIgnoringCase("subCommand");
        assertThat(arguments.parameters().get(0)).isEqualToIgnoringCase("123");
        assertThat(arguments.exit()).isFalse();
    }

    @Test
    void exceptionWhenNoCommand() throws Exception {
        String command = "";

        try {
            argCollector = new ArgCollector(command);
            failBecauseExceptionWasNotThrown(CliMeUsageException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(CliMeUsageException.class);
        }
    }

    @Test
    void exceptionWhenNoSubCommand() throws Exception {
        String command = "bestCommand";

        try {
            argCollector = new ArgCollector(command);
            failBecauseExceptionWasNotThrown(CliMeUsageException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(CliMeUsageException.class);
        }
    }

    @Test
    void collectLongParameters() throws Exception {
        String command = "bestCommand okSubCommand \"val1 is best value\" \"me is good dude\"";
        argCollector = new ArgCollector(command);

        Arguments arguments = argCollector.getArguments();

        assertThat(arguments.command()).isEqualToIgnoringCase("bestCommand");
        assertThat(arguments.subCommand()).isEqualToIgnoringCase("okSubCommand");
        assertThat(arguments.parameters().get(0)).isEqualToIgnoringCase("val1 is best value");
        assertThat(arguments.parameters().get(1)).isEqualToIgnoringCase("me is good dude");
        assertThat(arguments.parameters().size()).isEqualTo(2);
        assertThat(arguments.exit()).isFalse();
    }

    @Test
    void collectHyphenAndUnderscoreParameters() throws Exception {
        String command = "bestCommand okSubCommand val1_is_best_value me-is-good-dude";
        argCollector = new ArgCollector(command);

        Arguments arguments = argCollector.getArguments();

        assertThat(arguments.command()).isEqualToIgnoringCase("bestCommand");
        assertThat(arguments.subCommand()).isEqualToIgnoringCase("okSubCommand");
        assertThat(arguments.parameters().get(0)).isEqualToIgnoringCase("val1_is_best_value");
        assertThat(arguments.parameters().get(1)).isEqualToIgnoringCase("me-is-good-dude");
        assertThat(arguments.parameters().size()).isEqualTo(2);
        assertThat(arguments.exit()).isFalse();
    }

}