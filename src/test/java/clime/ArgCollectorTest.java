package clime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import clime.exceptions.CliMeUsageException;
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
    void exceptionWhenNoCommand() {
        String command = "";

        try {
            argCollector = new ArgCollector(command);
            failBecauseExceptionWasNotThrown(CliMeUsageException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(CliMeUsageException.class);
        }
    }

    @Test
    void exceptionWhenNoSubCommand() {
        String command = "bestCommand";

        try {
            argCollector = new ArgCollector(command);
            failBecauseExceptionWasNotThrown(CliMeUsageException.class);
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(CliMeUsageException.class);
        }
    }

    @Test
    void collectLongParameters() throws CliMeUsageException {
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
    void collectQuoteAndNonQuotes() throws CliMeUsageException {
        String command = "assets vinstoassets \"3VW2K7AJ3EM425734\" 3C4FY58B15T609761 \"1N6BA0CH2DN311068\"";
        argCollector = new ArgCollector(command);

        Arguments arguments = argCollector.getArguments();

        assertThat(arguments.command()).isEqualToIgnoringCase("assets");
        assertThat(arguments.subCommand()).isEqualToIgnoringCase("vinstoassets");
        assertThat(arguments.parameters().get(0)).isEqualToIgnoringCase("3VW2K7AJ3EM425734");
        assertThat(arguments.parameters().get(1)).isEqualToIgnoringCase("3C4FY58B15T609761");
        assertThat(arguments.parameters().get(2)).isEqualToIgnoringCase("1N6BA0CH2DN311068");
        assertThat(arguments.parameters().size()).isEqualTo(3);
        assertThat(arguments.exit()).isFalse();
    }

    @Test
    void collectHyphenAndUnderscoreParameters() throws CliMeUsageException {
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

    @Test
    void collectHelpSubCommandMessage() throws CliMeUsageException {
        String command = "bestCommand help";
        argCollector = new ArgCollector(command);

        Arguments arguments = argCollector.getArguments();

        assertThat(arguments.help()).isFalse();
        assertThat(arguments.helpSubCommand()).isTrue();
    }

    @Test
    void collectHelpCommand() throws CliMeUsageException {
        String command = "help";
        argCollector = new ArgCollector(command);

        Arguments arguments = argCollector.getArguments();

        assertThat(arguments.help()).isTrue();
    }

}