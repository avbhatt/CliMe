# CliMe

## Description

CliMe is a framework to easily create Java command-line-interface (CLI) applications.

CliMe handles all the pre-requisites around building CLIs and allows developers to focus on providing value quickly.

## Usage

CliMe is powered by two annotations: `CliMeCommand` and the optional `CliMeInit`.

`@CliMeCommand` should be placed on any public class that should be accessible via the CLI.

`@CliMeInit` should be placed on a static initializer in the annotated class to tell CliMe how to create that object.

- If `@CliMeInit` is not present, the default constructor is used to create the object

That's it! Now CliMe provides a way to run every **public** method in an annotated class, as long as it follow a few simple rules:

- Input parameters to the method must be of type `String`
  - This means it is on the developer to convert from a String (e.g. `Integer.valueOf(stringNumber)`)
- No duplicate method names for the same class (no method overloading)
- No duplicate class names
- Default constructor must be present *if* `@CliMeInit` is not present
- `@CliMeInit` must only be used once per class
- `@CliMeInit` must only be used on a static method

CliMe structures commands like so: `<CLASS_NAME> <METHOD_NAME> <PARAMETERS>`.
The class is treated as the *command* and the method is the *sub-command*.

There are 3 ways to use the CliMe framework:

1. [Stateless](#stateless)
1. [Interactive](#interactive)
1. [Stateful through RMI](#stateful-through-rmi)

## Stateless

`new CliMe(<PACKAGE_TO_SCAN>).run(<ARGUMENTS>)`

## Interactive

`new CliMe(<PROMPT>, <PACKAGE_TO_SCAN>).interactive()`

## Stateful through RMI

`new CliMe(<PROMPT>, <PACKAGE_TO_SCAN>).rmi()`

## Package Repository

Add library via [JitPack](https://jitpack.io/#avbhatt/CliMe)
