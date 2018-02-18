package ru.saidgadjiev.orm.next.core.query.core.clause;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryElement;
import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupBy implements QueryElement {

    private List<GroupByItem> groupByItems = new ArrayList<>();

    public void add(GroupByItem columnSpec) {
        groupByItems.add(columnSpec);
    }

    public GroupBy addAll(Collection<GroupByItem> columnSpecs) {
        groupByItems.addAll(columnSpecs);

        return this;
    }

    public List<GroupByItem> getGroupByItems() {
        return groupByItems;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this, visitor);

    }
}
