package ru.saidgadjiev.ormnext.core.query_element.clause;

import ru.saidgadjiev.ormnext.core.loader.visitor.QueryElement;
import ru.saidgadjiev.ormnext.core.loader.visitor.QueryVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represent group by clause.
 */
public class GroupBy implements QueryElement {

    /**
     * Group by items.
     *
     * @see GroupByItem
     */
    private final List<GroupByItem> groupByItems = new ArrayList<>();

    /**
     * Add new group by item.
     *
     * @param groupByItem target group by item
     */
    public void add(GroupByItem groupByItem) {
        groupByItems.add(groupByItem);
    }

    /**
     * Add all group by items.
     *
     * @param columnSpecs target group by items
     * @return this instance for chain
     */
    public GroupBy addAll(Collection<GroupByItem> columnSpecs) {
        groupByItems.addAll(columnSpecs);

        return this;
    }

    /**
     * Return all group by items.
     *
     * @return groupByItems
     */
    public List<GroupByItem> getGroupByItems() {
        return groupByItems;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        if (visitor.visit(this)) {
            groupByItems.forEach(groupByItem -> groupByItem.accept(visitor));
        }
    }
}
