package org.mitoma.ponto;

import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.Properties;

import javax.lang.model.SourceVersion;

public enum KeyStyle {

  Flat {
    @Override
    public void writeMethods(PrintWriter pw, Properties properties) {
      for (Object key : properties.keySet()) {
        String keyString = key.toString();
        MethodType type = MethodType.findMethodType(keyString);
        String methodName = keyString.replace('.', '_').replaceFirst(
            String.format("_%s\\Z", type.getMethodKey()), "");
        pw.println(type
            .toMethodString(escapedMethodName(methodName), keyString));
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
      writeNode(pw, root, 1);
    }

    private void writeNode(PrintWriter pw, Node node, int depth) {
      for (Entry<String, MethodType> method : node.getMethods().entrySet()) {
        pw.print(indent(depth));

        String fullName = node.getFullName();
        String keyName = method.getKey();
        if (!fullName.isEmpty()) {
          keyName = String.format("%s.%s", fullName, method.getKey());
        }
        if (method.getValue() != MethodType.STRING) {
          keyName = String.format("%s.%s", keyName, method.getValue()
              .getMethodKey());
        }

        pw.println(method.getValue().toMethodString(
            escapedMethodName(method.getKey()), keyName));
      }
      for (Node child : node.getChilds()) {
        pw.print(indent(depth));
        pw.println(String.format("public static class %s {", child.getName()));
        writeNode(pw, child, depth + 1);
        pw.print(indent(depth));
        pw.println("}");
      }
    }

    private String indent(int depth) {
      StringBuilder buf = new StringBuilder();
      for (int i = 0; i < depth; i++) {
        buf.append("  ");
      }
      return buf.toString();
    }

  };

  public abstract void writeMethods(PrintWriter pw, Properties properties);

  private static String escapedMethodName(String methodName) {
    if (!SourceVersion.isName(methodName)) {
      return "_" + methodName;
    }
    return methodName;
  }
}
