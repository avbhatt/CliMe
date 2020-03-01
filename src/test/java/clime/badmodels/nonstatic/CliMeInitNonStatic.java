package clime.badmodels.nonstatic;

import clime.annotations.CliMeCommand;
import clime.annotations.CliMeInit;

import java.util.Arrays;
import java.util.List;

@CliMeCommand
public class CliMeInitNonStatic {

	private String customName;

	public CliMeInitNonStatic(String customName) {
		this.customName = customName;
	}

	public CliMeInitNonStatic() {
		this.customName = "Default Constructor Name :(";
	}

	@CliMeInit
	public CliMeInitNonStatic initializer() {
		return new CliMeInitNonStatic("CorrectName :)");
	}

	public List<String> listNames() {
		return Arrays.asList("Name1", "Name2", "NameX", customName);
	}
}
