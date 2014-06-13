package in.tombo.ponto;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public enum MethodType {
  STRING {
    @Override
    public String getMethodKey() {
      return null;
    }

    @Override
    public String toMethodString(String modifire, String escapedMethodName, String keyName) {
      return String.format("%s String %s() { return getProperty(\"%s\"); }",
          modifire, escapedMethodName, keyName);
    }

    @Override
    public boolean isValid(String property) {
      return true;
    }
  },
  INTEGER {
    @Override
    public String getMethodKey() {
      return "_int";
    }

    @Override
    public String toMethodString(String modifire, String escapedMethodName, String keyName) {
      return String.format(
          "%s int %s() { return Integer.valueOf(getProperty(\"%s\")); }",
          modifire, escapedMethodName, keyName);
    }

    @Override
    public boolean isValid(String property) {
      try {
        Integer.valueOf(property);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  },
  LONG {
    @Override
    public String getMethodKey() {
      return "_long";
    }

    @Override
    public String toMethodString(String modifire, String escapedMethodName, String keyName) {
      return String.format("%s long %s() { return Long.valueOf(getProperty(\"%s\")); }",
          modifire, escapedMethodName, keyName);
    }

    @Override
    public boolean isValid(String property) {
      try {
        Long.valueOf(property);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  },
  FLOAT {
    @Override
    public String getMethodKey() {
      return "_float";
    }

    @Override
    public String toMethodString(String modifire, String escapedMethodName, String keyName) {
      return String.format(
          "%s float %s() { return Float.valueOf(getProperty(\"%s\")); }",
          modifire, escapedMethodName, keyName);
    }

    @Override
    public boolean isValid(String property) {
      try {
        Float.valueOf(property);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  },
  DOUBLE {
    @Override
    public String getMethodKey() {
      return "_double";
    }

    @Override
    public String toMethodString(String modifire, String escapedMethodName, String keyName) {
      return String.format(
          "%s double %s() { return Double.valueOf(getProperty(\"%s\")); }",
          modifire, escapedMethodName, keyName);
    }

    @Override
    public boolean isValid(String property) {
      try {
        Double.valueOf(property);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  },
  BOOLEAN {
    @Override
    public String getMethodKey() {
      return "_boolean";
    }

    @Override
    public String toMethodString(String modifire, String escapedMethodName, String keyName) {
      StringBuilder buf = new StringBuilder();
      buf.append("%s boolean %s() { ");
      buf.append("String p = (getProperty(\"%s\")); ");
      buf.append("if (\"true\".equalsIgnoreCase(p)) {return true;} ");
      buf.append("if (\"false\".equalsIgnoreCase(p)) {return false;} ");
      buf.append("throw new RuntimeException(\"%s is showuld be true or false.\"); ");
      buf.append("}");
      return String.format(buf.toString(), modifire, escapedMethodName, keyName, keyName);
    }

    @Override
    public boolean isValid(String property) {
      return ("true".equalsIgnoreCase(property) || "false".equalsIgnoreCase(property));
    }
  },
  DATE {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public String getMethodKey() {
      return "_date";
    }

    @Override
    public String toMethodString(String modifire, String escapedMethodName, String keyName) {
      return String
          .format(
              "%s java.util.Date %s() { try { return new java.text.SimpleDateFormat(\"%s\").parse(getProperty(\"%s\")); } catch (Exception e) { throw new RuntimeException(e); } }",
              modifire, escapedMethodName, DATE_FORMAT, keyName);

    }

    @Override
    public boolean isValid(String property) {
      try {
        new SimpleDateFormat(DATE_FORMAT).parse(property);
        return true;
      } catch (ParseException e) {
        return false;
      }
    }
  },
  TIMESTAMP {
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public String getMethodKey() {
      return "_timestamp";
    }

    @Override
    public String toMethodString(String modifire, String escapedMethodName, String keyName) {
      return String
          .format(
              "%s java.util.Date %s() { try { return new java.text.SimpleDateFormat(\"%s\").parse(getProperty(\"%s\")); } catch (Exception e) { throw new RuntimeException(e); } }",
              modifire, escapedMethodName, TIMESTAMP_FORMAT, keyName);
    }

    @Override
    public boolean isValid(String property) {
      try {
        new SimpleDateFormat(TIMESTAMP_FORMAT).parse(property);
        return true;
      } catch (ParseException e) {
        return false;
      }
    }
  };

  public abstract String getMethodKey();

  public abstract String toMethodString(String modifire, String escapedMethodName, String keyName);

  public abstract boolean isValid(String property);

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
