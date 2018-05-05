package ru.saidgadjiev.ormnext.core.query.core;

import ru.saidgadjiev.ormnext.core.query.core.common.UpdateValue;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

public class InsertValues implements QueryElement {

    private final List<UpdateValue> updateValues = new ArrayList<>();

    /**
     * Добавление нового значения
     *
     * @param updateValue добавляемое значение
     */
    public void add(UpdateValue updateValue) {
        updateValues.add(updateValue);
    }

    /**
     * Добавление коллекции значений
     *
     * @param values
     */
    public void addAll(List<UpdateValue> values) {
        updateValues.addAll(values);
    }

    /**
     * Получение списка значений
     *
     * @return
     */
    public List<UpdateValue> getUpdateValues() {
        return updateValues;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }
}
