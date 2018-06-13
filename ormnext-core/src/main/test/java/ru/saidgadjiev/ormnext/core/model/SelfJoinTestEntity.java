package ru.saidgadjiev.ormnext.core.model;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

import java.util.ArrayList;
import java.util.List;

public class SelfJoinTestEntity {

    @DatabaseColumn(id = true, generated = true)
    private int id;

    @ForeignColumn
    private SelfJoinTestEntity selfJoinTestEntity;

    @ForeignCollectionField
    private List<SelfJoinTestEntity> selfJoinTestEntityList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SelfJoinTestEntity getSelfJoinTestEntity() {
        return selfJoinTestEntity;
    }

    public void setSelfJoinTestEntity(SelfJoinTestEntity selfJoinTestEntity) {
        this.selfJoinTestEntity = selfJoinTestEntity;
    }

    public List<SelfJoinTestEntity> getSelfJoinTestEntityList() {
        return selfJoinTestEntityList;
    }

    public void setSelfJoinTestEntityList(List<SelfJoinTestEntity> selfJoinTestEntityList) {
        this.selfJoinTestEntityList = selfJoinTestEntityList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelfJoinTestEntity that = (SelfJoinTestEntity) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
