package clime.badmodels.multi;

import clime.annotations.CliMeCommand;
import clime.annotations.CliMeInit;

import java.util.Arrays;
import java.util.List;

@CliMeCommand
public class CliMeInitMultipleInitializers {

	private String customName;

	public CliMeInitMultipleInitializers(String customName) {
		this.customName = customName;
	}

	public CliMeInitMultipleInitializers() {
		this.customName = "Default Constructor Name :(";
	}

	@CliMeInit
	public static CliMeInitMultipleInitializers initializer() {
		return new CliMeInitMultipleInitializers("CorrectName :)");
	}

	@CliMeInit
	public static CliMeInitMultipleInitializers initializer2() {
		return new CliMeInitMultipleInitializers("Second init :(");
	}

	public List<String> listNames() {
		return Arrays.asList("Name1", "Name2", "NameX", customName);
	}
}
