package clime.badmodels.badreturn;

import clime.annotations.CliMeCommand;
import clime.annotations.CliMeInit;

import java.util.Arrays;
import java.util.List;

@CliMeCommand
public class CliMeInitBadReturn {

	private String customName;

	public CliMeInitBadReturn(String customName) {
		this.customName = customName;
	}

	public CliMeInitBadReturn() {
		this.customName = "Default Constructor Name :(";
	}

	@CliMeInit
	public static Object initializer() {
		return new CliMeInitBadReturn("CorrectName :)");
	}

	public List<String> listNames() {
		return Arrays.asList("Name1", "Name2", "NameX", customName);
	}
}
