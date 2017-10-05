package ru.said.miami.orm.core.cache.core.utils.test;

import ru.said.miami.orm.core.cache.core.field.TableField;
import ru.said.miami.orm.core.cache.core.field.DataType;
import ru.said.miami.orm.core.cache.core.table.Table;

/**
 * Created by said on 09.05.17.
 */
@Table(name = "test2")
public class Test {

    @TableField(fieldName = "id", id = true, autoGeneratedId = true, canBeNull = false, dataType = DataType.LONG)
    public long id;

    @TableField(fieldName = "name", dataType = DataType.STRING)
    public String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
