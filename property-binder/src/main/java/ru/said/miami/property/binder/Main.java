package ru.said.miami.property.binder;

import ru.said.miami.property.binder.binder.PropertyBinder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by said on 28.10.17.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Properties properties;

        try (InputStream inputStream = new FileInputStream("/Users/said/Desktop/miami_beach/property-binder/src/main/java/ru/said/miami/property/binder/test.properties")) {
            properties = new Properties();
            properties.load(inputStream);
        }

        System.out.println(properties.getProperty("password"));
    }
}
