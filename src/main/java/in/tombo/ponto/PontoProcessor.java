package in.tombo.ponto;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static javax.lang.model.SourceVersion.RELEASE_8;

@SupportedAnnotationTypes("in.tombo.ponto.ConstantResource")
@SupportedSourceVersion(RELEASE_8)
public class PontoProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element elem : roundEnv.getElementsAnnotatedWith(ConstantResource.class)) {
      ConstantResource annotation = elem.getAnnotation(ConstantResource.class);
      Messager messager = processingEnv.getMessager();
      try {
        generateSource(annotation);
      } catch (IOException | IllegalArgumentException e) {
        messager.printMessage(Diagnostic.Kind.ERROR, e.toString());
        return false;
      }
    }
    return true;
  }

  private void generateSource(ConstantResource annotation) throws IOException {
    String[] propFiles = annotation.value();
    String packageName = annotation.packageName();
    String className = annotation.className();
    long scanPeriod = annotation.scanPeriod();
    String encoding = annotation.encoding();
    Filer filer = processingEnv.getFiler();
    Properties properties = loadProperties(propFiles);
    List<String> errors = validation(properties);
    annotation.keyStyle().validateProperties(errors, properties);
    if (!errors.isEmpty()) {
      Messager messager = processingEnv.getMessager();
      for (String err : errors) {
        messager.printMessage(Kind.ERROR, err);
      }
      throw new IllegalArgumentException("invalid properties file.");
    }

    JavaFileObject source = filer.createSourceFile(String.format("%s.%s", packageName, className));
    PrintWriter pw = new PrintWriter(new OutputStreamWriter(source.openOutputStream(), encoding), true);
    if (!packageName.isEmpty()) {
      pw.println(String.format("package %s;", packageName));
    }
    pw.println(String.format("public class %s {", className));

    annotation.keyStyle().writeMethods(pw, properties);

    String envKey = annotation.envKey();
    String envDefault = annotation.envDefault();
    pw.println("  private static String getEnvValue() {");
    pw.println("    String env = System.getProperty(\"" + envKey + "\");");
    pw.println("    if (env != null) {");
    pw.println("      return env;");
    pw.println("    }");
    pw.println("    env = System.getenv(\"" + envKey + "\");");
    pw.println("    if (env != null) {");
    pw.println("      return env;");
    pw.println("    }");
    pw.println("    return \"" + envDefault + "\";");
    pw.println("  }");
    pw.println("  private static String[] propertyFilePaths = new String[] {");
    for (String propFile : propFiles) {
      pw.printf("    \"%s\",\n", propFile);
    }
    pw.println("  };");

    pw.printf(
        "  private static in.tombo.ponto.PropertiesService pService = new in.tombo.ponto.PropertiesService(\"%s.%s\", %dL, getEnvValue(), \"%s\", propertyFilePaths);\n",
        packageName, className, scanPeriod, encoding);

    pw.println("  public static java.util.Properties getProperties() {");
    pw.println("    return pService.getProperties();");
    pw.println("  }");
    pw.println("  private static String getProperty(String key) {");
    pw.println("    return pService.getProperty(key);");
    pw.println("  }");
    pw.println("}");
    pw.flush();
    pw.close();
  }

  private Properties loadProperties(String[] propFiles) throws IOException {
    Properties properties = new Properties();
    Filer filer = processingEnv.getFiler();
    for (String propFile : propFiles) {
      InputStream stream =
          filer.getResource(StandardLocation.CLASS_PATH, "", propFile).openInputStream();
      if (propFile.endsWith(".xml")) {
        properties.loadFromXML(stream);
      } else {
        properties.load(stream);
      }
    }
    return properties;
  }

  private List<String> validation(Properties properties) {
    List<String> errors = new ArrayList<>();
    for (Object key : properties.keySet()) {
      String keyStr = (String) key;
      MethodType methodType = MethodType.findMethodType(keyStr);
      String value = properties.getProperty(keyStr);
      if (!methodType.isValid(value)) {
        errors.add(String.format("invalid key. type[%s], format[%s], value[%s]", methodType,
            keyStr, value));
      }
    }
    return errors;
  }
}
