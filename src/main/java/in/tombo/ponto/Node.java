package in.tombo.ponto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {

  private Node parent = null;
  private final List<Node> children = new ArrayList<>();
  private final Map<String, MethodType> methods = new HashMap<>();
  private String name;

  public Node(Node parent, String name) {
    if (parent == null) {
      return;
    }
    this.name = name;
    this.parent = parent;
    this.parent.children.add(this);
  }

  public Node getParent() {
    return parent;
  }

  public List<Node> getChildren() {
    return children;
  }

  public Map<String, MethodType> getMethods() {
    return methods;
  }

  public void addKeyString(String keyString) {
    String[] elements = keyString.split("\\.");
    addKeys(elements);
  }

  private void addKeys(String[] elems) {
    if (elems.length == 1) {
      methods.put(elems[0], MethodType.STRING);
      return;
    }
    if (elems.length == 2) {
      MethodType type = MethodType.findMethodType(elems[1]);
      if (type != MethodType.STRING) {
        methods.put(elems[0], type);
        return;
      }
    }
    Node child = getChildrenOrCreate(elems[0]);
    child.addKeys(Arrays.copyOfRange(elems, 1, elems.length));
  }

  private Node getChildrenOrCreate(String name) {
    for (Node child : children) {
      if (child.name.equals(name)) {
        return child;
      }
    }
    return new Node(this, name);
  }

  public String getFullName() {
    if (parent == null) {
      return "";
    }
    String parentFullName = parent.getFullName();
    if (parentFullName.isEmpty()) {
      return name;
    }
    return String.format("%s.%s", parentFullName, name);
  }

  public String getName() {
    return name;
  }
}
