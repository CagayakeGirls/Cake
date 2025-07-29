package team.cagayakegirls.cake.extension;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;

import javax.inject.Inject;

public abstract class ModDependencyExtension {
    @Inject
    protected abstract Project getProject();

    /**
     * Get Dependency Mod from CurseForge Maven
     * @param modSlug Dependency Mod Slug on CurseForge
     * @param modProjectId Dependency Mod Project ID on CurseForge
     * @param modVersionId Dependency Mod Version ID on CurseForge
     * @return CurseForge Dependency Mod
     */
    public Dependency curseforge(String modSlug, String modProjectId, String modVersionId) {
        Project project = this.getProject();
        DependencyHandler dependencies = project.getDependencies();

        return dependencies.create("curse.maven:" + modSlug + "-" + modProjectId + ":" + modVersionId);
    }

    /**
     * Get Dependency Mod from Modrinth Maven
     * @param modSlug Dependency Mod Slug/Project ID on CurseForge
     * @param modVersion Dependency Mod Version/Version ID on CurseForge
     * @return Modrinth Dependency Mod
     */
    public Dependency modrinth(String modSlug, String modVersion) {
        Project project = this.getProject();
        DependencyHandler dependencies = project.getDependencies();

        return dependencies.create("maven.modrinth:" + modSlug + ":" + modVersion);
    }
}
