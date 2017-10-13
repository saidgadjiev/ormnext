package ru.said.miami.orm.core;

import ru.said.miami.orm.core.field.DBField;
import ru.said.miami.orm.core.field.DataType;
import ru.said.miami.orm.core.field.ForeignCollectionField;
import ru.said.miami.orm.core.table.DBTable;

import javax.xml.crypto.Data;
import java.util.List;

@DBTable(name = "account")
public class Account {

    @DBField(id = true, generated = true)
    private Integer id;

    @DBField(dataType = DataType.STRING)
    private String name;

    @ForeignCollectionField(foreignFieldName = "account")
    private List<Order> orders;

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

    public List<Order> getOrders() {
        return orders;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", orders=" + orders +
                '}';
    }
}
