package io.shcm.shsupercm.fabric.fletchingtable;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.compile.JavaCompile;

import java.util.List;

/**
 * Settings for Fletching Table
 */
public abstract class FletchingTableExtension {
    private final Project project;

    /**
     * Controls whether the annotation processor should run. When false, enableEntrypoints and enableMixins are ignored.
     */
    public abstract Property<Boolean> getEnableAnnotationProcessor();

    /**
     * Enables annotation processing for @Entrypoint.
     */
    public abstract Property<Boolean> getEnableEntrypoints();

    /**
     * Enables annotation processing for @Mixin.
     */
    public abstract Property<Boolean> getEnableMixins();

    /**
     * Sets the default mixin environment when not overridden by @MixinEnvironment.<br>
     * "mixins" will process mixins for both sides, "client" will process mixins as client mixins, "server" will process mixins as server mixins.
     * "none"(default) will ignore mixins entirely. "auto" will put everything in "mixins" unless one of the targets matches a client/server prefix.
     */
    public abstract Property<String> getDefaultMixinEnvironment();

    /**
     * Sets the prefix required for the "auto" environment to be replaced with "client".
     */
    public abstract Property<String> getAutoMixinEnvironmentClientPrefix();
    /**
     * Sets the prefix required for the "auto" environment to be replaced with "server".
     */
    public abstract Property<String> getAutoMixinEnvironmentServerPrefix();

    public FletchingTableExtension(Project project) {
        this.project = project;
        getEnableAnnotationProcessor().convention(true);

        getEnableEntrypoints().convention(true);
        getEnableMixins().convention(true);

        getDefaultMixinEnvironment().convention("none");
        getAutoMixinEnvironmentClientPrefix().convention("net.minecraft.client");
        getAutoMixinEnvironmentServerPrefix().convention("null");
    }

    protected void writeAPSettings(JavaCompile compileTask) {
        List<String> compilerArgs = compileTask.getOptions().getCompilerArgs();

        compilerArgs.add("-Afletchingtable.entrypoints=" + getEnableEntrypoints().get());
        compilerArgs.add("-Afletchingtable.mixins=" + getEnableMixins().get());
        compilerArgs.add("-Afletchingtable.mixins.default=" + getDefaultMixinEnvironment().get());
        compilerArgs.add("-Afletchingtable.mixins.prefix.client=" + getAutoMixinEnvironmentClientPrefix().get());
        compilerArgs.add("-Afletchingtable.mixins.prefix.server=" + getAutoMixinEnvironmentServerPrefix().get());
    }
}
