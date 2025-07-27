package io.shcm.shsupercm.fabric.fletchingtable;

import net.fabricmc.loom.api.LoomGradleExtensionAPI;
import net.fabricmc.mappingio.MappedElementKind;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.MappingVisitor;
import org.gradle.api.Project;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassNameMapping implements MappingVisitor {
    private final Map<String, String> namedToIntermediary = new HashMap<>();

    private String src = null, named = null, intermediary = null;
    private int namedId, intermediaryId;

    public ClassNameMapping(Path mappingsFile) throws IOException {
        MappingReader.read(mappingsFile, this);
        this.src = null;
        this.named = null;
        this.intermediary = null;
    }

    public ClassNameMapping(Project project) throws IOException {
        this(project.getExtensions().getByType(LoomGradleExtensionAPI.class).getMappingsFile().toPath());
    }

    public String remap(String named) {
        return this.namedToIntermediary.getOrDefault(named, named);
    }

    @Override
    public void visitNamespaces(String srcNamespace, List<String> dstNamespaces) throws IOException {
        this.namedId = dstNamespaces.indexOf("named");
        this.intermediaryId = dstNamespaces.indexOf("intermediary");
    }

    @Override
    public boolean visitClass(String srcName) throws IOException {
        this.src = srcName;
        return true;
    }

    @Override
    public void visitDstName(MappedElementKind targetKind, int namespace, String name) throws IOException {
        if (targetKind == MappedElementKind.CLASS && this.src != null) {
            if (namespace == this.intermediaryId)
                this.intermediary = name;
            else if (namespace == this.namedId)
                this.named = name;

            if (this.intermediary != null && this.named != null) {
                this.namedToIntermediary.put(this.named, this.intermediary);
                this.named = null;
                this.intermediary = null;
            }
        }
    }

    @Override
    public boolean visitField(String srcName, String srcDesc) throws IOException {
        return false;
    }

    @Override
    public boolean visitMethod(String srcName, String srcDesc) throws IOException {
        return false;
    }

    @Override
    public boolean visitMethodArg(int argPosition, int lvIndex, String srcName) throws IOException {
        return false;
    }

    @Override
    public boolean visitMethodVar(int lvtRowIndex, int lvIndex, int startOpIdx, int endOpIdx, @Nullable String srcName) throws IOException {
        return false;
    }

    @Override
    public void visitComment(MappedElementKind targetKind, String comment) throws IOException {

    }
}
