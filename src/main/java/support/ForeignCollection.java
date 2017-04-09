package support;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 07.04.17.
 */
public class ForeignCollection<T> {
    List<T> list = new ArrayList<>();

    public boolean add(T elem) {
        return list.add(elem);
    }
}
