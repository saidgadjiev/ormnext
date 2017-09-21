package ru.said.miami.orm.core.cache.core.support;

import ru.said.miami.orm.core.cache.core.binding.annotations.Property;
import ru.said.miami.orm.core.cache.core.binding.annotations.Requiered;

/**
 * Created by said on 12.06.17.
 */
public interface MiamiProperties {

    @Property(name = "username")
    @Requiered
    String getUserName();

    @Property(name = "password")
    @Requiered
    String getPassword();

    @Property(name = "driver")
    @Requiered
    String getDriverName();

    @Property(name = "url")
    @Requiered
    String getUrl();

    @Property(name = "trace")
    @Requiered
    boolean trace();
}
