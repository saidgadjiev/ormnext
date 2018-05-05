package ru.saidgadjiev.ormnext.core.query.core.common;

import ru.saidgadjiev.ormnext.core.query.core.literals.RValue;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;
import ru.saidgadjiev.ormnext.core.query.visitor.QueryElement;

/**
 * Класс для обновляемого значения
 */
public class UpdateValue implements QueryElement {

    /**
     * Название в таблице обновляемого поля
     */
    private final String name;

    /**
     * Значение обновляемого поля
     */
    private RValue value;

    public UpdateValue(String name, RValue value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Метод возвращает имя поля
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Метод возвращает значение переменной
     * @return value
     */
    public RValue getValue() {
        return value;
    }

    /**
     * Устанавливает значение
     * @param value значение
     */
    public void setValue(RValue value) {
        this.value = value;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            value.accept(visitor);
        }
    }
}
