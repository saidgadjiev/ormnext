package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.table.DatabaseEntity;

import java.util.ArrayList;
import java.util.List;

@DatabaseEntity(name = "a")
public class SelfJoinA {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @ForeignColumn
    private SelfJoinA a;

    @ForeignCollectionField
    private List<SelfJoinA> as = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SelfJoinA getA() {
        return a;
    }

    public void setA(SelfJoinA a) {
        this.a = a;
    }

    public List<SelfJoinA> getAs() {
        return as;
    }

    public void setAs(List<SelfJoinA> as) {
        this.as = as;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelfJoinA selfJoinA = (SelfJoinA) o;

        return id == selfJoinA.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
