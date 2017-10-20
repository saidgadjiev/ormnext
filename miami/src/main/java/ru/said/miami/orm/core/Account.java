package ru.said.miami.orm.core;

import ru.said.miami.orm.core.field.*;
import ru.said.miami.orm.core.table.DBTable;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

@DBTable(name = "account")
public class Account {

    @Getter(name = "getId")
    @Setter(name = "setId")
    @DBField(id = true, generated = true)
    private Integer id;

    @Getter(name = "getName")
    @Setter(name = "setName")
    @DBField(dataType = DataType.STRING)
    private String name;

    @ForeignCollectionField
    private List<Order> orders = new ArrayList<>();

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
