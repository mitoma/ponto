package in.tombo.ponto;

public @interface ConstantResource {
  public String[] value() default {"ponto.properties"};

  public String packageName() default "in.tombo.ponto";

  public String className() default "PontoResource";

  public KeyStyle keyStyle() default KeyStyle.Hierarchical;

  public String envKey() default "PONTO_ENV";

  public String envDefault() default "development";

  public String encoding() default "UTF-8";

  public long scanPeriod() default -1L;
}
