package ru.said.miami.orm.core;

import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.table.DBTable;

@DBTable(name = "test")
public class Test {

    @DBField(fieldName = "id", dataType = DataType.INTEGER, id = true, generated = true)
    private Integer id;

    @DBField(fieldName = "name", dataType = DataType.STRING, length = 16)
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
