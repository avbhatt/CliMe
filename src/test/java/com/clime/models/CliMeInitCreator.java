package com.clime.models;

import com.clime.annotations.CliMeCommand;
import com.clime.annotations.CliMeInit;

import java.util.Arrays;
import java.util.List;

@CliMeCommand
public class CliMeInitCreator {

	private String customName;

	public CliMeInitCreator(String customName) {
		this.customName = customName;
	}

	public CliMeInitCreator() {
		this.customName = "Default Constructor Name :(";
	}

	@CliMeInit
	public static CliMeInitCreator initializer() {
		return new CliMeInitCreator("CorrectName :)");
	}

	public List<String> listNames() {
		return Arrays.asList("Name1", "Name2", "NameX", customName);
	}
}
