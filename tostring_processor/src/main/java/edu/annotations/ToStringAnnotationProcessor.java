package edu.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.beans.Introspector;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("edu.annotations.ToString")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ToStringAnnotationProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Hello");
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.size() == 0) return true;
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile("edu.annotations.ToStringGenerator");
            try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
                out.println("package edu.annotations;");
                out.println("public class ToStringGenerator {");
                for (Element element : roundEnv.getElementsAnnotatedWith(ToString.class)) {
                    if (element instanceof TypeElement) {
                        TypeElement te = (TypeElement) element;
                        writeToStringMethod(out, te);
                    }
                }
                out.println("    public static String toString(Object obj) {");
                out.println("        return java.util.Objects.toString(obj);");
                out.println("    }");
                out.println("}");
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        return true;
    }

    private void writeToStringMethod(PrintWriter out, TypeElement te) {
        String className = te.getQualifiedName().toString();
        out.printf("    public static String toString(%s obj) {\n", className);
        out.println("        StringBuilder result = new StringBuilder();");
        ToString ann = te.getAnnotation(ToString.class);
        if (ann.includeName())
            out.printf("        result.append(\"%s[\");\n", className);
        else
            out.println("        result.append(\"[\");");
        boolean first = true;
        for (Element element : te.getEnclosedElements()) {
            String methodName = element.getSimpleName().toString();
            ann = element.getAnnotation(ToString.class);
            if (ann != null) {
                if (first)
                    first = false;
                else
                    out.println("        result.append(\", \");");
                if (ann.includeName()) {
                    String fieldName = Introspector.decapitalize(methodName.replaceAll("^(get|is)", ""));
                    out.printf("        result.append(\"%s=\");\n", fieldName);
                }
                out.printf("        result.append(toString(obj.%s()));\n", methodName);
            }
        }
        out.println("        result.append(\"]\");");
        out.println("        return result.toString();");
        out.println("    }");
    }
}
