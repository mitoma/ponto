package org.mitoma.ponto;

public @interface ConstantResource {
  public String[] value() default { "ponto.properties" };

  public String packageName() default "org.mitoma.ponto";

  public String className() default "PontoResource";

  public KeyStyle keyStyle() default KeyStyle.Hierarchical;

}
