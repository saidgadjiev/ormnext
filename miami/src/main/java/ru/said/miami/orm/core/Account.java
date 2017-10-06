package ru.said.miami.orm.core;

import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.table.DBTable;

import javax.xml.crypto.Data;

@DBTable(name = "account")
public class Account {

    @DBField(id = true, generated = true)
    private Integer id;

    @DBField(dataType = DataType.STRING)
    private String name;

    @DBField(foreign = true)
    private Order order;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
