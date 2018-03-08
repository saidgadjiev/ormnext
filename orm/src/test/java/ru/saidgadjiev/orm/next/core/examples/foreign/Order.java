package ru.saidgadjiev.orm.next.core.examples.foreign;

import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.Getter;
import ru.saidgadjiev.orm.next.core.field.Setter;

/**
 * Created by said on 27.02.2018.
 */
public class Order {

    @Getter(name = "getId")
    @Setter(name = "setId")
    @DBField(id = true, generated = true, dataType = DataType.LONG)
    private int id;

    @Getter(name = "getDescription")
    @Setter(name = "setDescription")
    @DBField(dataType = DataType.STRING)
    private String description;

    @Getter(name = "getAccount")
    @Setter(name = "setAccount")
    @DBField(foreign = true)
    private Account account;

    //For OrmNext
    public Order() {
    }

    public Order(String description) {
        this.description = description;
    }

    public Order(String description, Account account) {
        this.description = description;
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
