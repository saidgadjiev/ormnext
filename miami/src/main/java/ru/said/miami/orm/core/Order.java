package ru.said.miami.orm.core;

import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.field.DataType;

public class Order {

    @DBField(columnName = "id", dataType = DataType.INTEGER)
    private Integer id;

    @DBField
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
