package in.tombo.ponto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesService {
  private final Logger logger;

  private final String[] filePaths;
  private final String envValue;

  private Properties properties;
  private Properties envProperties;
  private final long scanPeriod;
  private long loadTime;

  private final String encoding;

  public PropertiesService(String loggerName, long scanPeriod, String envValue, String encoding,
      String... filePaths) {
    this.logger = LoggerFactory.getLogger(loggerName);
    this.envValue = envValue;
    this.filePaths = filePaths;
    this.scanPeriod = scanPeriod;
    this.loadTime = 0L;
    this.encoding = encoding;
    loadProperties();
    loggingSettings();
  }

  public Properties getProperties() {
    Properties p = new Properties();
    Set<Object> keys = properties.keySet();
    for (Object keyObject : keys) {
      String key = (String) keyObject;
      p.setProperty(key, getProperty(key));
    }
    return p;
  }

  public String getProperty(String keyString) {
    if (scanPeriod > 0 && scanPeriod < System.currentTimeMillis() - loadTime) {
      loadProperties();
      loggingSettings();
    }
    if (envProperties.containsKey(keyString)) {
      logger.debug("get key env:{}, key:{}", envValue, keyString);
      return envProperties.getProperty(keyString);
    }
    logger.debug("get key env:default, key:{}", keyString);
    return properties.getProperty(keyString);
  }

  private synchronized void loadProperties() {
    loadTime = System.currentTimeMillis();
    envProperties = new Properties();
    properties = new Properties();
    for (String filePath : filePaths) {
      loadProperty(filePath);
    }
  }

  private void loggingSettings() {
    if (logger.isInfoEnabled()) {
      logger.info("Available property keys.");
      for (Object keyObj : properties.keySet()) {
        String keyStr = (String) keyObj;
        String useProp;
        Properties p;
        if (envProperties.containsKey(keyStr)) {
          useProp = envValue;
          p = envProperties;
        } else {
          useProp = "default";
          p = properties;
        }
        logger.info("\tenv:{}\tkey:{}\tvalue:{}", useProp, keyStr, p.get(keyStr));
      }
    }
  }

  private void loadProperty(String filePath) {
    boolean isXml = filePath.endsWith(".xml");
    String envFilePath = getEnvFilePath(filePath);
    try {
      if (isXml) {
        try {
          envProperties.loadFromXML(getInputStream(envFilePath));
          logger.info("properties file {} is loaded.", envFilePath);
        } catch (IOException e) {
          logger.info("env properties file {} is not found.", envFilePath);
        }
        properties.loadFromXML(getInputStream(filePath));
      } else {
        try {
          envProperties.load(new InputStreamReader(getInputStream(envFilePath), encoding));
          logger.info("properties file {} is loaded.", envFilePath);
        } catch (IOException e) {
          logger.info("env properties file {} is not found.", envFilePath);
        }
        properties.load(new InputStreamReader(getInputStream(filePath), encoding));
      }
      logger.info("properties file {} is loaded.", filePath);
    } catch (IOException e) {
      logger.error("properties file {} is not found.", filePath);
      throw new RuntimeException(String.format("properties file %s is not found.", filePath));
    }
  }

  private String getEnvFilePath(String filePath) {
    int lastDotIndex = filePath.lastIndexOf(".");
    String pre = filePath.substring(0, lastDotIndex);
    String post = filePath.substring(lastDotIndex);
    return String.format("%s_%s%s", pre, envValue, post);
  }

  private InputStream getInputStream(String filePath) throws IOException {
    InputStream in;
    if (filePath.startsWith("classpath:")) {
      in = getInputStreamFromClassPath(filePath.substring(10));
    } else if (filePath.startsWith("file:")) {
      in = getInputStreamFromFile(filePath.substring(5));
    } else {
      in = getInputStreamFromClassPath(filePath);
    }
    if (in == null) {
      throw new IOException("filePath " + filePath + " is invalid.");
    }
    return in;
  }

  private FileInputStream getInputStreamFromFile(String filePath) throws FileNotFoundException {
    return new FileInputStream(filePath);
  }

  private InputStream getInputStreamFromClassPath(String filePath) throws IOException {
    Enumeration<URL> resources =
        Thread.currentThread().getContextClassLoader().getResources(filePath);
    for (URL resource : Collections.list(resources)) {
      logger.info("Resource URL:{}", resource);
      if ("jar".equals(resource.getProtocol())) {
        return (InputStream) resource.getContent();
      } else {
        return new FileInputStream(resource.getFile());
      }
    }
    return null;
  }
}
