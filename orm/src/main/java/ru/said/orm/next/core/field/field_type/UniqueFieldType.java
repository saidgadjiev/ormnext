package ru.said.orm.next.core.field.field_type;

import java.util.List;

/**
 * Created by said on 30.10.17.
 */
public class UniqueFieldType {

    private List<String> columns;

    public UniqueFieldType(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getDbFieldTypes() {
        return columns;
    }

}
