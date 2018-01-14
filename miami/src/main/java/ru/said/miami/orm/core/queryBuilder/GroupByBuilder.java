package ru.said.miami.orm.core.queryBuilder;

import ru.said.miami.orm.core.query.core.clause.GroupBy;
import ru.said.miami.orm.core.query.core.clause.GroupByItem;

/**
 * Created by said on 19.11.17.
 */
public class GroupByBuilder {

    private GroupBy groupBy = new GroupBy();

    GroupByBuilder() {
    }

    public GroupByBuilder add(GroupByItem groupByItem) {
        groupBy.add(groupByItem);

        return this;
    }

    public GroupBy build() {
        return groupBy;
    }
}
