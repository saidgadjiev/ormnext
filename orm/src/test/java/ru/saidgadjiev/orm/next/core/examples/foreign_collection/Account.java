package ru.saidgadjiev.orm.next.core.examples.foreign_collection;

import ru.saidgadjiev.orm.next.core.field.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 27.02.2018.
 */
public class Account {

    @Getter(name = "getId")
    @Setter(name = "setId")
    @DBField(id = true, generated = true, dataType = DataType.LONG)
    private int id;

    @Getter(name = "getName")
    @Setter(name = "setName")
    @DBField
    private String name;

    @ForeignCollectionField
    private List<Order> orders = new ArrayList<>();

    //For OrmNext
    public Account() {
    }

    public Account(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
