package ru.saidgadjiev.orm.next.core.query.core;

import ru.saidgadjiev.orm.next.core.field.field_type.DBFieldType;
import ru.saidgadjiev.orm.next.core.field.field_type.ForeignFieldType;
import ru.saidgadjiev.orm.next.core.query.core.common.UpdateValue;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.orm.next.core.table.TableInfo;

import java.sql.SQLException;
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
    private final List<UpdateValue> updateValues = new ArrayList<>();


    private CreateQuery(String typeName) {
        this.typeName = typeName;
    }

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

    /**
     * Получение имени типа
     *
     * @return
     */
    public String getTypeName() {
        return typeName;
    }

    public static <T> CreateQuery buildQuery(TableInfo<T> tableInfo, T object) throws SQLException {
        CreateQuery createQuery = new CreateQuery(tableInfo.getTableName());

        try {
            for (DBFieldType fieldType : tableInfo.toDBFieldTypes()) {
                if (fieldType.isId() && fieldType.isGenerated()) {
                    continue;
                }
                createQuery.add(new UpdateValue(
                        fieldType.getColumnName(),
                        fieldType.getDataPersister().getLiteral(fieldType, fieldType.access(object)))
                );
            }
            for (ForeignFieldType fieldType : tableInfo.toForeignFieldTypes()) {
                Object foreignObject = fieldType.access(object);
                TableInfo<?> foreignTableInfo = TableInfo.TableInfoCache.build(fieldType.getForeignFieldClass());

                if (foreignObject != null && foreignTableInfo.getPrimaryKey().isPresent()) {
                    createQuery.add(new UpdateValue(
                            fieldType.getColumnName(),
                            fieldType.getDataPersister().getLiteral(fieldType, fieldType.getForeignPrimaryKey().access(foreignObject)))
                    );
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }

        return createQuery;
    }

    public static CreateQuery buildQuery(String typeName) {
        return new CreateQuery(typeName);
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.start(this)) {
            for (UpdateValue updateValue : updateValues) {
                updateValue.accept(visitor);
            }
        }
        visitor.finish(this);
    }
}
