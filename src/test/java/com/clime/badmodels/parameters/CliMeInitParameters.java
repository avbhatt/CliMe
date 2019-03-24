package com.clime.badmodels.parameters;

import com.clime.annotations.CliMeCommand;
import com.clime.annotations.CliMeInit;

import java.util.Arrays;
import java.util.List;

@CliMeCommand
public class CliMeInitParameters {

	private String customName;

	public CliMeInitParameters(String customName) {
		this.customName = customName;
	}

	public CliMeInitParameters() {
		this.customName = "Default Constructor Name :(";
	}

	@CliMeInit
	public static CliMeInitParameters initializer(String name) {
		return new CliMeInitParameters("CorrectName :)");
	}

	public List<String> listNames() {
		return Arrays.asList("Name1", "Name2", "NameX", customName);
	}
}
