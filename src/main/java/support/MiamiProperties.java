package support;

import binding.annotations.Property;
import binding.annotations.Requiered;

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
