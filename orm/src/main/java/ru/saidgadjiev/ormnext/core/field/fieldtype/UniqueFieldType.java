package ru.saidgadjiev.ormnext.core.field.fieldtype;

import java.util.List;

/**
 * Created by said on 30.10.17.
 */
public class UniqueFieldType {

    private List<String> columns;

    public UniqueFieldType(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getColumnNames() {
        return columns;
    }

}
