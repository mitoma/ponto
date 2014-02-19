package org.mitoma.ponto;


public enum MethodType {
  STRING {
    @Override
    public String getMethodKey() {
      return null;
    }

    @Override
    public String toMethodString(String escapedMethodName, String keyName) {
      return String.format(
          "public static String %s(){ return getProperties(\"%s\"); }",
          escapedMethodName, keyName);
    }
  },
  INTEGER {
    @Override
    public String getMethodKey() {
      return "_int";
    }

    @Override
    public String toMethodString(String escapedMethodName, String keyName) {
      return String
          .format(
              "public static int %s(){ return Integer.valueOf(getProperties(\"%s\")); }",
              escapedMethodName, keyName);
    }
  },
  LONG {
    @Override
    public String getMethodKey() {
      return "_long";
    }

    @Override
    public String toMethodString(String escapedMethodName, String keyName) {
      return String
          .format(
              "public static long %s(){ return Long.valueOf(getProperties(\"%s\")); }",
              escapedMethodName, keyName);
    }
  },
  FLOAT {
    @Override
    public String getMethodKey() {
      return "_float";
    }

    @Override
    public String toMethodString(String escapedMethodName, String keyName) {
      return String
          .format(
              "public static float %s(){ return Float.valueOf(getProperties(\"%s\")); }",
              escapedMethodName, keyName);
    }
  },
  DOUBLE {
    @Override
    public String getMethodKey() {
      return "_double";
    }

    @Override
    public String toMethodString(String escapedMethodName, String keyName) {
      return String
          .format(
              "public static double %s(){ return Double.valueOf(getProperties(\"%s\")); }",
              escapedMethodName, keyName);
    }
  },
  DATE {
    @Override
    public String getMethodKey() {
      return "_date";
    }

    @Override
    public String toMethodString(String escapedMethodName, String keyName) {
      return String
          .format(
              "public static java.util.Date %s(){ try { return new java.text.SimpleDateFormat(\"%s\").parse(getProperties(\"%s\")); } catch (Exception e) { throw new RuntimeException(e); } }",
              escapedMethodName, "yyyy-MM-dd", keyName);

    }
  },
  TIMESTAMP {
    @Override
    public String getMethodKey() {
      return "_timestamp";
    }

    @Override
    public String toMethodString(String escapedMethodName, String keyName) {
      return String
          .format(
              "public static java.util.Date %s(){ try { return new java.text.SimpleDateFormat(\"%s\").parse(getProperties(\"%s\")); } catch (Exception e) { throw new RuntimeException(e); } }",
              escapedMethodName, "yyyy-MM-dd HH:mm:ss", keyName);
    }
  };

  public abstract String getMethodKey();

  public abstract String toMethodString(String escapedMethodName, String keyName);

  public static MethodType findMethodType(String key) {
    String[] split = key.split("\\.");
    String lastKey = split[split.length - 1];

    for (MethodType type : values()) {
      if (lastKey.equals(type.getMethodKey())) {
        return type;
      }
    }
    return STRING;
  }

}