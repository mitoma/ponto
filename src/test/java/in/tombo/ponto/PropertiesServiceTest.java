package in.tombo.ponto;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class PropertiesServiceTest {

  @Test
  public void testPropertiesService() {
    PropertiesService pService = new PropertiesService("hoge.moge", "dev",
        "test1.properties");
    assertThat(pService.getProperties("test.key"), is("devString"));
    assertThat(pService.getProperties("test.intkey.valid._int"), is("1234"));
    assertThat(pService.getProperties("test.longkey.valid._long"), is("23456"));
    assertThat(pService.getProperties("test.floatkey.valid._float"),
        is("1234.1234"));
  }

}
