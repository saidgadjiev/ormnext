package ru.said.miami.orm.core.cache.core.clause.table;

import ru.said.miami.orm.core.cache.core.dao.visitor.QueryElement;
import ru.said.miami.orm.core.cache.core.dao.visitor.QueryVisitor;
import ru.said.miami.orm.core.cache.core.field.FieldWrapper;

import java.util.ArrayList;
import java.util.List;

public class CreateTable implements QueryElement {
    private String name;
    private List<FieldWrapper> fieldWrappers = new ArrayList<>();

    public CreateTable(String name) {
        this.name = name;
    }

    public void addFieldWapper(FieldWrapper fieldWrapper) {
        fieldWrappers.add(fieldWrapper);
    }

    public List<FieldWrapper> getFieldWrappers() {
        return fieldWrappers;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
    }
}
