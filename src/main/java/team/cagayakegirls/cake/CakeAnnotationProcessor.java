package team.cagayakegirls.cake;

import team.cagayakegirls.cake.api.Entrypoint;
import team.cagayakegirls.cake.api.InterfaceInjection;
import team.cagayakegirls.cake.api.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class CakeAnnotationProcessor extends AbstractProcessor {
    private Writer writerEntrypoints = null, writerMixins = null, writerInterfaceInjections = null;
    private String defaultMixinEnvironment, autoMixinClientPrefix, autoMixinServerPrefix;
    private boolean defaultMixinInterfaceInjections;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        try {
            FileObject file;
            if (processingEnv.getOptions().get("cake.entrypoints").equalsIgnoreCase("true")) {
                file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "cake", "entrypoints.txt");
                file.delete();
                writerEntrypoints = file.openWriter();
            }
            if (processingEnv.getOptions().get("cake.mixins").equalsIgnoreCase("true")) {
                file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "cake", "mixins.txt");
                file.delete();
                writerMixins = file.openWriter();
                file = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "cake", "interfaceinjections.txt");
                file.delete();
                writerInterfaceInjections = file.openWriter();

                defaultMixinEnvironment = processingEnv.getOptions().get("cake.mixins.default");

                autoMixinClientPrefix = processingEnv.getOptions().get("cake.mixins.prefix.client");
                if (autoMixinClientPrefix.equals("null"))
                    autoMixinClientPrefix = null;
                autoMixinServerPrefix = processingEnv.getOptions().get("cake.mixins.prefix.server");
                if (autoMixinServerPrefix.equals("null"))
                    autoMixinServerPrefix = null;

                defaultMixinInterfaceInjections = processingEnv.getOptions().get("cake.mixins.interfaceinjection").equalsIgnoreCase("true");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();

        if (writerEntrypoints != null) {
            types.add("team.cagayakegirls.cake.api.Entrypoint.Repeated");
            types.add("team.cagayakegirls.cake.api.Entrypoint");
        }

        if (writerMixins != null) {
            types.add("org.spongepowered.asm.mixin.Mixin");
            types.add("team.cagayakegirls.cake.api.InterfaceInjection");
        }

        return types;
    }

    @Override
    public Set<String> getSupportedOptions() {
        HashSet<String> options = new HashSet<>();

        options.add("cake.entrypoints");
        options.add("cake.mixins");
        options.add("cake.mixins.default");
        options.add("cake.mixins.prefix.client");
        options.add("cake.mixins.prefix.server");
        options.add("cake.mixins.interfaceinjection");

        return options;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (writerEntrypoints != null) {
                for (Element element : roundEnv.getElementsAnnotatedWith(Entrypoint.class))
                    processEntrypoint(element, element.getAnnotation(Entrypoint.class).value());

                for (Element element : roundEnv.getElementsAnnotatedWith(Entrypoint.Repeated.class))
                    for (Entrypoint entrypoint : element.getAnnotation(Entrypoint.Repeated.class).value())
                        processEntrypoint(element, entrypoint.value());

                if (roundEnv.processingOver()) {
                    writerEntrypoints.close();
                    writerEntrypoints = null;
                } else
                    writerEntrypoints.flush();
            }

            if (writerMixins != null) {
                for (Element element : roundEnv.getElementsAnnotatedWith(Mixin.class))
                    processMixin(element);

                if (roundEnv.processingOver()) {
                    writerMixins.close();
                    writerMixins = null;
                    if (writerInterfaceInjections != null) {
                        writerInterfaceInjections.close();
                        writerInterfaceInjections = null;
                    }
                } else {
                    writerMixins.flush();
                    if (writerInterfaceInjections != null) {
                        writerInterfaceInjections.flush();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void processMixin(Element element) throws IOException {
        if (!element.getKind().isClass() && !element.getKind().isInterface())
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Got annotated @Mixin on unexpected type " + element.getKind() + "!");

        MixinEnvironment environmentOverride = element.getAnnotation(MixinEnvironment.class);
        String environment = environmentOverride == null ? defaultMixinEnvironment : environmentOverride.value().configName;

        if (environment.equals("auto")) {
            environment = "mixins";
            annotations: for (AnnotationMirror mixinMirror : element.getAnnotationMirrors())
                if (mixinMirror.getAnnotationType().toString().equals("org.spongepowered.asm.mixin.Mixin")) {
                    for (ExecutableElement key : mixinMirror.getElementValues().keySet())
                        if (key.toString().equals("value()") || key.toString().equals("targets()"))
                            //noinspection unchecked
                            for (Object targetObj : ((List<Object>) mixinMirror.getElementValues().get(key).getValue())) {
                                String target = targetObj.toString(); // safe for both Attribute.Class and String

                                if (autoMixinClientPrefix != null && target.startsWith(autoMixinClientPrefix)) {
                                    environment = "client";
                                    break annotations;
                                }
                                if (autoMixinServerPrefix != null && target.startsWith(autoMixinServerPrefix)) {
                                    environment = "server";
                                    break annotations;
                                }
                            }
                    break;
                }
        }

        if (!environment.equals("none"))
            writerMixins
                    .append(element.toString())
                    .append(" ")
                    .append(environment)
                    .append("\n");

        if (writerInterfaceInjections != null) {
            Mixin mixin = element.getAnnotation(Mixin.class);
            List<String> targets = new ArrayList<>(Arrays.asList(mixin.targets()));
            try {
                mixin.value();
            } catch (MirroredTypesException mirrored) {
                for (TypeMirror typeMirror : mirrored.getTypeMirrors())
                    targets.add(processingEnv.getElementUtils().getBinaryName((TypeElement) processingEnv.getTypeUtils().asElement(typeMirror)).toString());
            }
            targets.replaceAll(target -> target.replace('.', '/'));

            InterfaceInjection mixinInterfaceInjection = element.getAnnotation(InterfaceInjection.class);
            boolean inject = mixinInterfaceInjection == null ? defaultMixinInterfaceInjections : mixinInterfaceInjection.value();

            for (TypeMirror interfaceMirror : ((TypeElement) element).getInterfaces()) {
                InterfaceInjection interfaceInterfaceInjection = processingEnv.getTypeUtils().asElement(interfaceMirror).getAnnotation(InterfaceInjection.class);
                if (interfaceInterfaceInjection == null ? inject : interfaceInterfaceInjection.value()) {
                    String interfaceName = processingEnv.getElementUtils().getBinaryName((TypeElement) processingEnv.getTypeUtils().asElement(interfaceMirror)).toString().replace('.', '/');

                    for (String target : targets)
                        writerInterfaceInjections
                                .append(interfaceName)
                                .append(" ")
                                .append(target)
                                .append("\n");
                }
            }
        }
    }

    public void processEntrypoint(Element element, String entrypoint) throws IOException {
        switch (element.getKind()) {
            case CLASS:
                writerEntrypoints.append(element.toString());
                break;
            case METHOD:
            case FIELD:
                writerEntrypoints
                        .append(element.getEnclosingElement().toString())
                        .append("::")
                        .append(element.getSimpleName());
                break;
            default:
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Got annotated @Entrypoint on unexpected type " + element.getKind() + "!");
                return;
        }
        writerEntrypoints
                .append(" ")
                .append(entrypoint)
                .append("\n");
    }
}
