package in.tombo.ponto;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Properties;

import org.junit.Test;

public class PropertiesServiceTest {

  @Test
  public void useEnvProperty() {
    PropertiesService pService = new PropertiesService("hoge.moge", -1, "dev", "test1.properties");
    assertThat(pService.getProperty("test.key"), is("devString"));
    assertThat(pService.getProperty("test.intkey.valid._int"), is("1234"));
    assertThat(pService.getProperty("test.longkey.valid._long"), is("23456"));
    assertThat(pService.getProperty("test.floatkey.valid._float"), is("1234.1234"));
  }

  @Test
  public void useInvalidEnvProperty() {
    PropertiesService pService = new PropertiesService("hoge.moge", -1, "devo", "test1.properties");
    assertThat(pService.getProperty("test.key"), is("String"));
    assertThat(pService.getProperty("test.intkey.valid._int"), is("1234"));
    assertThat(pService.getProperty("test.longkey.valid._long"), is("12345"));
    assertThat(pService.getProperty("test.floatkey.valid._float"), is("1234.1234"));
  }

  @Test
  public void getProperties() {
    PropertiesService pService = new PropertiesService("hoge.moge", -1, "dev", "test1.properties");
    Properties p = pService.getProperties();
    assertThat(p.getProperty("test.key"), is("devString"));
    assertThat(p.getProperty("test.intkey.valid._int"), is("1234"));
    assertThat(p.getProperty("test.longkey.valid._long"), is("23456"));
    assertThat(p.getProperty("test.floatkey.valid._float"), is("1234.1234"));
  }

  @Test
  public void useScanPeriod1() throws InterruptedException, IllegalArgumentException,
      IllegalAccessException, NoSuchFieldException, SecurityException {
    PropertiesService pService =
        new PropertiesService("hoge.moge", 100, "devo", "test1.properties");

    Field loadTime = pService.getClass().getDeclaredField("loadTime");
    loadTime.setAccessible(true);

    assertThat(pService.getProperty("test.key"), is("String"));
    Long preLoadTime = (Long) loadTime.get(pService);
    assertThat(pService.getProperty("test.key"), is("String"));
    Long postLoadTime = (Long) loadTime.get(pService);
    assertEquals(preLoadTime, postLoadTime);
  }

  @Test
  public void useScanPeriod2() throws InterruptedException, IllegalArgumentException,
      IllegalAccessException, NoSuchFieldException, SecurityException {
    PropertiesService pService =
        new PropertiesService("hoge.moge", 100, "devo", "test1.properties");

    Field loadTime = pService.getClass().getDeclaredField("loadTime");
    loadTime.setAccessible(true);

    assertThat(pService.getProperty("test.key"), is("String"));
    Long preLoadTime = (Long) loadTime.get(pService);
    Thread.sleep(100);
    assertThat(pService.getProperty("test.key"), is("String"));
    Long postLoadTime = (Long) loadTime.get(pService);
    assertNotEquals(preLoadTime, postLoadTime);
  }

  @Test
  public void useScanPeriod3() throws InterruptedException, IllegalArgumentException,
      IllegalAccessException, NoSuchFieldException, SecurityException {
    PropertiesService pService = new PropertiesService("hoge.moge", -1, "devo", "test1.properties");

    Field loadTime = pService.getClass().getDeclaredField("loadTime");
    loadTime.setAccessible(true);

    assertThat(pService.getProperty("test.key"), is("String"));
    Long preLoadTime = (Long) loadTime.get(pService);
    Thread.sleep(100);
    assertThat(pService.getProperty("test.key"), is("String"));
    Long postLoadTime = (Long) loadTime.get(pService);
    assertEquals(preLoadTime, postLoadTime);
  }
}
