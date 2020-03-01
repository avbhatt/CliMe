package clime.badmodels.multimethod;

import clime.annotations.CliMeCommand;
import clime.annotations.CliMeInit;
import java.util.Arrays;
import java.util.List;

@CliMeCommand
public class SameMethodName {

    private String customName;

    public SameMethodName(String customName) {
        this.customName = customName;
    }

    public SameMethodName() {
        this.customName = "Default Constructor Name :(";
    }

    @CliMeInit
    public static SameMethodName initializer() {
        return new SameMethodName("CorrectName :)");
    }

    public List<String> listNames() {
        return Arrays.asList("Name1", "Name2", "NameX", customName);
    }

    public List<String> listNames(String name) {
        return Arrays.asList("Name1", "Name2", name, customName);
    }
}
