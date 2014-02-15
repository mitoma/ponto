package org.mitoma.ponto;

import java.io.PrintWriter;
import java.util.Properties;

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
        pw.print(indent(depth));
        pw.println(String.format(
            "public static String %s(){ return getProperties(\"%s\"); }",
            methodName, String.format("%s.%s", node.getFullName(), methodName)));
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
