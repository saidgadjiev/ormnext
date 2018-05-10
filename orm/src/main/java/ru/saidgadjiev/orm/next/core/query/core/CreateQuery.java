package ru.saidgadjiev.orm.next.core.query.core;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс INSERT запроса
 */
public class CreateQuery implements QueryElement {

    /**
     * Название типа
     */
    private final String typeName;

    /**
     * Вставляемые значения
     */
    private final List<InsertValues> updateValues = new ArrayList<>();

    private List<String> columnNames = new ArrayList<>();

    public CreateQuery(String typeName) {
        this.typeName = typeName;
    }

    public void add(InsertValues updateValues) {
        this.updateValues.add(updateValues);
    }

    public List<InsertValues> getInsertValues() {
        return updateValues;
    }

    public void addColumnName(String columnName) {
        columnNames.add(columnName);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Получение имени типа
     *
     * @return
     */
    public String getTypeName() {
        return typeName;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            updateValues.forEach(updateValue -> updateValue.accept(visitor));
        }
    }
}
