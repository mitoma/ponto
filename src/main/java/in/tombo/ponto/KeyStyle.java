package in.tombo.ponto;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.lang.model.SourceVersion;

public enum KeyStyle {

  Flat {
    @Override
    public void writeMethods(PrintWriter pw, Properties properties) {
      for (Object key : properties.keySet()) {
        pw.println();
        String keyString = key.toString();
        MethodType type = MethodType.findMethodType(keyString);
        String methodName = keyString.replace('.', '_').replaceFirst(
            String.format("_%s\\Z", type.getMethodKey()), "");

        pw.println(commentString(keyString, properties.getProperty(keyString),
            1));
        pw.print(indent(1));
        pw.println(type.toMethodString("public static",
            escapedMethodName(methodName), keyString));
      }
    }
  },
  Hierarchical {
    @Override
    public void writeMethods(PrintWriter pw, Properties properties) {
      Node root = new Node(null, "");
      for (Object key : properties.keySet()) {
        String keyString = key.toString();
        root.addKeyString(keyString);
      }
      writeNode(pw, root, 1, properties);
    }

    private void writeNode(PrintWriter pw, Node node, int depth,
        Properties properties) {
      for (Entry<String, MethodType> method : node.getMethods().entrySet()) {

        String fullName = node.getFullName();
        String keyName = method.getKey();
        if (!fullName.isEmpty()) {
          keyName = String.format("%s.%s", fullName, method.getKey());
        }
        if (method.getValue() != MethodType.STRING) {
          keyName = String.format("%s.%s", keyName, method.getValue()
              .getMethodKey());
        }

        pw.println();
        pw.println(commentString(keyName, properties.getProperty(keyName),
            depth));
        pw.print(indent(depth));
        pw.println(method.getValue().toMethodString("public static",
            escapedMethodName(method.getKey()), keyName));
      }
      for (Node child : node.getChilds()) {
        pw.println();
        pw.print(indent(depth));
        pw.println(String.format("public static class %s {",
            escapedClassName(child.getName())));
        writeNode(pw, child, depth + 1, properties);
        pw.print(indent(depth));
        pw.println("}");
      }
    }

  },
  Bean {
    @Override
    public void writeMethods(PrintWriter pw, Properties properties) {
      Node root = new Node(null, "");
      for (Object key : properties.keySet()) {
        String keyString = key.toString();
        root.addKeyString(keyString);
      }
      writeNode(pw, root, 1, properties);
    }

    private void writeNode(PrintWriter pw, Node node, int depth,
        Properties properties) {
      for (Entry<String, MethodType> method : node.getMethods().entrySet()) {

        String fullName = node.getFullName();
        String keyName = method.getKey();
        if (!fullName.isEmpty()) {
          keyName = String.format("%s.%s", fullName, method.getKey());
        }
        if (method.getValue() != MethodType.STRING) {
          keyName = String.format("%s.%s", keyName, method.getValue()
              .getMethodKey());
        }

        pw.println();
        pw.println(commentString(keyName, properties.getProperty(keyName),
            depth));
        pw.print(indent(depth));
        pw.println(method.getValue().toMethodString("public",
            beanGetterName(method.getKey(), method.getValue()), keyName));
      }
      for (Node child : node.getChilds()) {
        String childName = child.getName();
        pw.println();
        pw.print(indent(depth));
        pw.println(String.format("public %s %s() { return new %s(); }",
            escapedClassName(childName), beanGetterName(childName),
            escapedClassName(childName)));
        pw.println();
        pw.print(indent(depth));
        pw.println(String.format("private static class %s {",
            escapedClassName(childName)));
        writeNode(pw, child, depth + 1, properties);
        pw.print(indent(depth));
        pw.println("}");
      }
    }

  };

  public static final String commentTemplate = ""//
      + "----indent----/**\n"//
      + "----indent---- * Key<br/>\n"//
      + "----indent---- * <pre>%s</pre><br/>\n"//
      + "----indent---- * Value<br/>\n"//
      + "----indent---- * <pre>%s</pre>\n"//
      + "----indent---- */";

  public static String commentString(String key, String value, int depth) {
    return String
        .format(commentTemplate.replaceAll("----indent----", indent(depth)),
            key, value);
  }

  public abstract void writeMethods(PrintWriter pw, Properties properties);

  /**
   * @param methodName
   * @return
   */
  private static String escapedMethodName(String methodName) {
    return escapedName(methodName);
  }

  /**
   * @param className
   * @return
   */
  private static String escapedClassName(String className) {
    return escapedName(className);
  }

  private static String escapedName(String name) {
    if (!SourceVersion.isName(name)) {
      return "_" + name;
    }
    return name;
  }

  private static final Pattern upperCamelConvertionPattern = Pattern
      .compile("\\A.[a-z].*");

  /**
   * @param methodName
   * @return
   */
  private static String beanGetterName(String propertyName) {
    return beanGetterName(propertyName, null);
  }

  /**
   * @param methodName
   * @param methodType
   * @return
   */
  private static String beanGetterName(String propertyName,
      MethodType methodType) {
    if (upperCamelConvertionPattern.matcher(propertyName).matches()) {
      propertyName = propertyName.substring(0, 1).toUpperCase()
          + propertyName.substring(1);
    }
    String prefix = methodType == MethodType.BOOLEAN ? "is" : "get";
    return prefix + propertyName;
  }

  private static String indent(int depth) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      buf.append("  ");
    }
    return buf.toString();
  }
}
