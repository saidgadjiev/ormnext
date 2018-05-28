package ru.saidgadjiev.ormnext.core.field.field_type;

import java.util.List;

/**
 * Holds unique columns.
 *
 * @author said gadjiev
 */
public class UniqueColumns {

    /**
     * Columns.
     */
    private List<String> columns;

    /**
     * Create a new instance.
     * @param columns target columns
     */
    public UniqueColumns(List<String> columns) {
        this.columns = columns;
    }

    /**
     * Return unique columns.
     * @return unique columns
     */
    public List<String> getColumns() {
        return columns;
    }
}
