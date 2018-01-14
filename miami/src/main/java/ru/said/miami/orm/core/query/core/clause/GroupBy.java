package ru.said.miami.orm.core.query.core.clause;

import ru.said.miami.orm.core.query.visitor.QueryElement;
import ru.said.miami.orm.core.query.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupBy implements QueryElement {

    private List<GroupByItem> groupByItems = new ArrayList<>();

    public void add(GroupByItem columnSpec) {
        groupByItems.add(columnSpec);
    }

    public void addAll(Collection<GroupByItem> columnSpecs) {
        groupByItems.addAll(columnSpecs);
    }

    public List<GroupByItem> getGroupByItems() {
        return groupByItems;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.start(this);
        visitor.finish(this);
    }
}
