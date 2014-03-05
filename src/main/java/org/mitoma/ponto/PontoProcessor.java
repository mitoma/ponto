package org.mitoma.ponto;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("org.mitoma.ponto.ConstantResource")
public class PontoProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {
    for (Element elem : roundEnv
        .getElementsAnnotatedWith(ConstantResource.class)) {
      ConstantResource annotation = elem.getAnnotation(ConstantResource.class);

      String[] propFiles = annotation.value();
      String packageName = annotation.packageName();
      String className = annotation.className();
      KeyStyle keyStyle = annotation.keyStyle();

      Messager messager = processingEnv.getMessager();
      try {
        Properties properties = new Properties();
        Filer filer = processingEnv.getFiler();
        for (String propFile : propFiles) {
          InputStream stream = filer.getResource(StandardLocation.CLASS_PATH,
              "", propFile).openInputStream();
          if (propFile.endsWith(".xml")) {
            properties.loadFromXML(stream);
          } else {
            properties.load(stream);
          }
        }

        generateSource(packageName, className, keyStyle, properties, propFiles);
      } catch (IOException e1) {
        messager.printMessage(Diagnostic.Kind.ERROR, e1.toString());
      }
    }
    return true;
  }

  private void generateSource(String packageName, String className,
      KeyStyle keyStyle, Properties properties, String[] propFiles)
      throws IOException {
    Filer filer = processingEnv.getFiler();

    JavaFileObject source = filer.createSourceFile(String.format("%s.%s",
        packageName, className));
    PrintWriter pw = new PrintWriter(source.openOutputStream(), true);
    if (!packageName.isEmpty()) {
      pw.println(String.format("package %s;", packageName));
    }
    pw.println(String.format("public class %s {", className));

    keyStyle.writeMethods(pw, properties);

    pw.println("  private static java.util.Properties properties = new java.util.Properties();");
    pw.println("  static {");
    pw.println("    ClassLoader loader = ClassLoader.getSystemClassLoader();");
    pw.println("    try{");

    for (String propFile : propFiles) {
      if (propFile.endsWith(".xml")) {
        pw.println(String
            .format(
                "      properties.loadFromXML(loader.getResourceAsStream(\"%s\"));",
                propFile));
      } else {
        pw.println(String.format(
            "      properties.load(loader.getResourceAsStream(\"%s\"));",
            propFile));
      }
    }

    pw.println("    } catch (java.io.IOException e) {");
    pw.println("        throw new RuntimeException(\"Ponto initialize error.\", e);");
    pw.println("    }");
    pw.println("  }");
    pw.println("  private static String getProperties(String key){");
    pw.println("    return properties.getProperty(key);");
    pw.println("  }");
    pw.println("}");
    pw.flush();
    pw.close();
  }
}
