package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.field.DataPersisterManager;
import ru.said.miami.orm.core.query.core.Operand;
import ru.said.miami.orm.core.query.core.columnSpec.DisplayedOperand;
import ru.said.miami.orm.core.query.core.function.Function;

/**
 * Created by said on 19.11.17.
 */
public class SelectOperandBuilder {

    private Operand operand;

    public SelectOperandBuilder function(Function function) {
        this.operand = function;

        return this;
    }

    public SelectOperandBuilder value(Object value) {
        this.operand = DataPersisterManager.lookup(value.getClass()).getAssociatedOperand(value);

        return this;
    }

    public DisplayedOperand build() {
        return new DisplayedOperand(operand);
    }

}
