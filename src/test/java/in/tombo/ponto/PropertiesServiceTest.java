package in.tombo.ponto;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class PropertiesServiceTest {

  @Test
  public void useEnvProperty() {
    PropertiesService pService = new PropertiesService("hoge.moge", "dev", "test1.properties");
    assertThat(pService.getProperty("test.key"), is("devString"));
    assertThat(pService.getProperty("test.intkey.valid._int"), is("1234"));
    assertThat(pService.getProperty("test.longkey.valid._long"), is("23456"));
    assertThat(pService.getProperty("test.floatkey.valid._float"), is("1234.1234"));
  }

  @Test
  public void useInvalidEnvProperty() {
    PropertiesService pService = new PropertiesService("hoge.moge", "devo", "test1.properties");
    assertThat(pService.getProperty("test.key"), is("String"));
    assertThat(pService.getProperty("test.intkey.valid._int"), is("1234"));
    assertThat(pService.getProperty("test.longkey.valid._long"), is("12345"));
    assertThat(pService.getProperty("test.floatkey.valid._float"), is("1234.1234"));
  }

  @Test
  public void getProperties() {
    PropertiesService pService = new PropertiesService("hoge.moge", "dev", "test1.properties");
    Properties p = pService.getProperties();
    assertThat(p.getProperty("test.key"), is("devString"));
    assertThat(p.getProperty("test.intkey.valid._int"), is("1234"));
    assertThat(p.getProperty("test.longkey.valid._long"), is("23456"));
    assertThat(p.getProperty("test.floatkey.valid._float"), is("1234.1234"));
  }
}
