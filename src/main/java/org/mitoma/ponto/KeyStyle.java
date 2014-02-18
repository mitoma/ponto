package org.mitoma.ponto;

import java.io.PrintWriter;
import java.util.Properties;

import javax.lang.model.SourceVersion;

public enum KeyStyle {

  Flat {
    @Override
    public void writeMethods(PrintWriter pw, Properties properties) {
      for (Object key : properties.keySet()) {
        String keyString = key.toString();
        String methodName = keyString.replace('.', '_');
        pw.println(String.format(
            "  public static String %s(){ return getProperties(\"%s\"); }",
            methodName, keyString));
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
      for (String methodName : node.getMethods()) {
        String escapedMethodName = methodName;  
        if(!SourceVersion.isName(methodName)){
          escapedMethodName = methodName + "_escaped";
        }
        pw.print(indent(depth));

        String fullName = node.getFullName();
        String keyName = methodName;
        if (!fullName.isEmpty()) {
          keyName = String.format("%s.%s", fullName, methodName);
        }
        pw.println(String.format(
            "public static String %s(){ return getProperties(\"%s\"); }",
            escapedMethodName, keyName));
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

}
