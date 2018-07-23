package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.FetchType;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;
import ru.saidgadjiev.ormnext.core.table.DatabaseEntity;

import java.util.ArrayList;
import java.util.List;

public class SelfJoinLazyA {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @ForeignColumn
    private SelfJoinLazyA a;

    @ForeignCollectionField(fetchType = FetchType.LAZY)
    private List<SelfJoinLazyA> as = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SelfJoinLazyA getA() {
        return a;
    }

    public void setA(SelfJoinLazyA a) {
        this.a = a;
    }

    public List<SelfJoinLazyA> getAs() {
        return as;
    }

    public void setAs(List<SelfJoinLazyA> as) {
        this.as = as;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelfJoinLazyA selfJoinA = (SelfJoinLazyA) o;

        return id == selfJoinA.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
