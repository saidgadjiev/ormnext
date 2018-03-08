package ru.saidgadjiev.orm.next.core.examples.foreign;

import ru.saidgadjiev.orm.next.core.field.DBField;
import ru.saidgadjiev.orm.next.core.field.DataType;
import ru.saidgadjiev.orm.next.core.field.Getter;
import ru.saidgadjiev.orm.next.core.field.Setter;

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

}
