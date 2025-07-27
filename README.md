# Fletching Table
Additions and automations for [architectury-loom](https://github.com/architectury/architectury-loom)

## This project is still experimental, use at your own risk.

## Features
### Entrypoints
Automatically registers entrypoints into the project's mod json.

Simply annotate a static class, method or field with `@Entrypoint` and specify the desired entrypoint to register to in its value!
Fletching Table will automatically process it and add it to existing entrypoints in `fabric.mod.json`.

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
You can change the default setting to always try to register interface injections in the fletchingTable settings block.
The mixin annotation overrides the default setting and the interface's annotation overrides the mixin's setting.
Automatic mixins must be turned on to use this feature.

### Included Jars
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

### Fungible
Adds a shortcut to add [Fungible by magistermaks](https://github.com/magistermaks/mod-fungible) to the development environment.

To use, add `fletchingTable.fungible "<RELEASE TAG>"` in the dependencies block with the release tag of the version of fungible you want to add.<br>
Example for [fungible v1.2 for minecraft 1.19.2](https://github.com/magistermaks/mod-fungible/releases/tag/1.2%2Bmc1.19.2):
```groovy
dependencies {
    ..
    fletchingTable.fungible "1.2+mc1.19.2"
}
```

[List of available fungible version tags](https://github.com/magistermaks/mod-fungible/tags)

### Settings
Fletching Table's default settings can be changed in an extension named `fletchingTable` as follows:
```groovy
fletchingTable {
    // Enables the entire annotation processor and adds the annotation api to the project's classpath
    enableAnnotationProcessor = true //default
    // Enables injecting processed entrypoint annotations to the mod json
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


## Setup
Add the plugin by applying it <ins>**after loom**</ins>.
```patch
plugins {
    id 'dev.architectury.loom' ...
+   id "io.shcm.shsupercm.fabric.fletchingtable" version "1.6"
}
```

## Changelog
Look at the [commits](https://github.com/SHsuperCM/FletchingTable/commits) for a changelog.

## Planned
 - ~~Kotlin support~~ (only if I somehow find the time, kotlin is cursed af)

## About Fletching Table
I originally suggested automatic entrypoints to loom but it was denied as it was out of scope. I was told it would make a good library and while I still believe Fabric should have automatic entrypoints by default, I agree that it needs to be a thing regardless.

Someone then told me on the Fabric discord that [Fudge](https://github.com/natanfudge) already made [a library for entrypoint annotations](https://github.com/natanfudge/AutoFabric) but it turned out to be buggy and hard to fix(why kotlin..).
Still, Fudge did a good amount of work that in the end helped me set up Fletching Table so thanks for that.

I also had some more things I wanted out of loom so I figured I might as well make my own plugin.

Why "Fletching Table"?
I- Idk.. just wanted something Loom-adjacent and didnt want to call it Smithing Table :p
