package clause.table;

import dao.visitor.QueryElement;
import dao.visitor.QueryVisitor;
import field.FieldWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by said on 17.06.17.
 */
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
