package in.tombo.ponto;

import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.Properties;

import javax.lang.model.SourceVersion;

public enum KeyStyle {

  Flat {
    @Override
    public void writeMethods(PrintWriter pw, Properties properties) {
      for (Object key : properties.keySet()) {
        pw.println();
        String keyString = key.toString();
        MethodType type = MethodType.findMethodType(keyString);
        String methodName =
            keyString.replace('.', '_').replaceFirst(String.format("_%s\\Z", type.getMethodKey()),
                "");

        pw.println(commentString(keyString, properties.getProperty(keyString), 1));
        pw.print(indent(1));
        pw.println(type.toMethodString(escapedMethodName(methodName), keyString));
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

    private void writeNode(PrintWriter pw, Node node, int depth, Properties properties) {
      for (Entry<String, MethodType> method : node.getMethods().entrySet()) {

        String fullName = node.getFullName();
        String keyName = method.getKey();
        if (!fullName.isEmpty()) {
          keyName = String.format("%s.%s", fullName, method.getKey());
        }
        if (method.getValue() != MethodType.STRING) {
          keyName = String.format("%s.%s", keyName, method.getValue().getMethodKey());
        }

        pw.println();
        pw.println(commentString(keyName, properties.getProperty(keyName), depth));
        pw.print(indent(depth));
        pw.println(method.getValue().toMethodString(escapedMethodName(method.getKey()), keyName));
      }
      for (Node child : node.getChilds()) {
        pw.println();
        pw.print(indent(depth));
        pw.println(String.format("public static class %s {", child.getName()));
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
    return String.format(commentTemplate.replaceAll("----indent----", indent(depth)), key, value);
  }

  public abstract void writeMethods(PrintWriter pw, Properties properties);

  /**
   * 
   * @param methodName
   * @return
   */
  private static String escapedMethodName(String methodName) {
    if (!SourceVersion.isName(methodName)) {
      return "_" + methodName;
    }
    return methodName;
  }

  private static String indent(int depth) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      buf.append("  ");
    }
    return buf.toString();
  }
}
