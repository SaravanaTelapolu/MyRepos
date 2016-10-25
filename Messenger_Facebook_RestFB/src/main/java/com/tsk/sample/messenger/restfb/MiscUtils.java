package com.tsk.sample.messenger.restfb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiscUtils {
  private static final Logger logger = Logger.getLogger(MiscUtils.class.getName());

  public static InputStream getInputStream(ClassLoader classLoader, String propertiesFName) throws IOException {
    InputStream inputStream = null;

    URL url = classLoader.getResource(propertiesFName);

    if (url != null) {
      logger.log(Level.INFO, "Loading Property file: " + url.getFile());
      inputStream = url.openStream();
    }

    if (inputStream == null) {
      logger.fine("");
      File tmpFile = new File(propertiesFName);
      if (tmpFile.exists()) {
        logger.log(Level.INFO, "Loading Property file: " + propertiesFName);
        inputStream = new FileInputStream(propertiesFName);
      } else {
        throw new IOException("Unable to load property file: " + propertiesFName);
      }
    }

    return inputStream;
  }
}
