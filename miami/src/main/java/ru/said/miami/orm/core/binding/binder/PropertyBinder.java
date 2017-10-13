package ru.said.miami.orm.core.binding.binder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * Created by said on 12.06.17.
 */
public class PropertyBinder<T> {
    private Properties properties;

    public PropertyBinder(String propertiesFilePath) throws IOException {
        InputStream inputStream = new FileInputStream(propertiesFilePath);
        this.properties = new Properties();
        this.properties.load(inputStream);
    }

    public T getProperties(Class<T> iProperty) {
        return (T) Proxy.newProxyInstance(iProperty.getClassLoader(), new Class[] { iProperty }, new PropertyInvokationHandler(properties));
    }
}
