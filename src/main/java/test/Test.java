package test;

import field.DBField;
import table.DBTable;

/**
 * Created by said on 25.02.17.
 */
@DBTable(name = "test")
public class Test {

    @DBField(fieldName = "test_name")
    private String name;
}
