package ru.said.miami.property.binder;

import ru.said.miami.property.binder.annotations.Property;
import ru.said.miami.property.binder.annotations.Requiered;

import java.net.URL;

/**
 * Created by said on 28.10.17.
 */
public interface Test {

    @Property(name = "test")
    @Requiered
    URL getTest();

    @Property(name = "password")
    @Requiered
    String getPassword();
}
