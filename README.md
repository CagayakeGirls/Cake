# Cake

> [!WARNING]
> This project is still experimental, use at your own risk.

Additions and automations for [architectury-loom](https://github.com/architectury/architectury-loom).

A fork of Fletching Table to supports multi-modloader project.

## Features
### Entrypoints

> [!CAUTION]
> This feature is only support Fabric!

Automatically registers entrypoints into the project's mod json.

Simply annotate a static class, method or field with `@Entrypoint` and specify the desired entrypoint to register to in its value!
Cake will automatically process it and add it to existing entrypoints in `fabric.mod.json`.

### Mixins

> [!WARNING]
> This feature is experimental on NeoForge and NOT support on MinecraftForge!

Automatically registers mixins into the project's mixin jsons.

Mixin environments can be overridden by annotating the mixin with `@MixinEnvironment`.
You can set mixins to be registered automatically without MixinEnvironment by changing the `defaultMixinEnvironment` in the settings.

### Interface Injections

> [!WARNING]
> This feature is experimental on NeoForge and MinecraftForge!

Registers interface injections automatically on mixins/interfaces annotated with `@InterfaceInjection`.

By default either the interface or the mixin implementing it have to be annotated with `@InterfaceInjection`.
You can change the default setting to always try to register interface injections in the cake settings block.
The mixin annotation overrides the default setting and the interface's annotation overrides the mixin's setting.
Automatic mixins must be turned on to use this feature.

### Included Jars

> [!WARNING]
> This feature is experimental on NeoForge and MinecraftForge!

Exposes jars that were included in dependencies with Loom's `include` configuration.

To use, first add an `includedJars` closure in your dependencies. 
Add parent dependencies to extract from using the `from` configuration.
Then, use extracted jars in other configurations by setting the group to `includedJars` and the name to the name of the extracted jar(without the `.jar` extension).

As an example, here's how to add Pride Lib inside Lambdynamic Lights from the modrinth maven to the modCompileOnly configuration:
```groovy
dependencies {
    ..
    includedJars {
        from "maven.modrinth:lambdynamiclights:2.1.0+1.17"
    }
    modCompileOnly "includedJars:pridelib-1.1.0+1.17"
}
```

## CurseForgeMaven/ModrinthMaven Mod Dependency

### Settings
Cake's default settings can be changed in an extension named `cake` as follows:
```groovy
cake {
    // Enables the entire annotation processor and adds the annotation api to the project's classpath
    enableAnnotationProcessor = true //default
    // Enables injecting processed entrypoint annotations to the fabric mod json
    enableEntrypoints = true //default
    // Enables injecting mixins to the mixin jsons
    enableMixins = true //default
    // Sets the default mixin environment to register mixins into
    defaultMixinEnvironment = "none" //default, can be either "none", "auto", "mixins", "client", "server"
    // Sets the prefix required for mixin targets to set the "auto" environment to "client"
    autoMixinEnvironmentClientPrefix = "net.minecraft.client" //default
    // Sets the prefix required for mixin targets to set the "auto" environment to "server"
    autoMixinEnvironmentServerPrefix = "null" //default
    // Sets the default behavior for interface injections
    enableAutoInterfaceInjections = false //default
}
```

## CurseForge/Modrinth Mod Dependency
```groovy
dependencies {
    // JEI 23.1.0.4 for NeoForge 1.21.7
    modImplementation modDeps.curseforge("jei", "238222", "6756169") // curse.maven:jei-238222:6756169 
    modImplementation modDeps.modrinth("jei", "23.1.0.4") // maven.modrinth:jei:23.1.0.4
}
```

## Mappings
We supports use yarn & parchment mappings
```groovy
dependencies {
    // Yarn mappings v1 only support fabric.
    mappings cakeMappings.yarn("1.21.1+build.4", false) // net.fabricmc:yarn:1.21.1+build.4:v1
    // Yarn mappings patch only support (Neo)Forge, because (Neo)Forge patches broken with yarn mappings.
    mappings cakeMappings.yarnWithPatch("1.21.1+build.4", "1.21+build.1") // "net.fabricmc:yarn:1.21.1+build.4:v2" layered "dev.architectury:yarn-mappings-patch-neoforge:1.21+build.1"
    // Parchment Mappings only support with mojmap (official mappings)
    mappings cakeMappings.parchment("1.21.1", "2024.11.17") // mojmap layered "org.parchmentmc.data:parchment-1.21.1:2024.11.17@zip"
}
```

## ForgifiedFabricAPI Dependency
```groovy
dependencies {
    modImplementation forgifiedFabricApi.module("fabric-api-base", "0.129.0+2.0.26+1.21.8", false)
}
```

## Setup
Add the plugin by applying it <ins>**after loom**</ins>.
```patch
plugins {
    id 'dev.architectury.loom' ...
+   id "team.cagayakegirls.cake" version "1.6"
}
```

## Changelog
Look at the [commits](https://github.com/CagayakeGirls/Cake/commits) for a changelog.

## Credits
- [SHsuperCM/FletchingTable](https://github.com/SHsuperCM/FletchingTable)
