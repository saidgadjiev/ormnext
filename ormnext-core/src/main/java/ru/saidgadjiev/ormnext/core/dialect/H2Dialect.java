package ru.saidgadjiev.ormnext.core.dialect;

import ru.saidgadjiev.ormnext.core.query.visitor.element.AttributeDefinition;

/**
 * Dialect for h2 database.
 */
public class H2Dialect extends BaseDialect {

    @Override
    public String getPrimaryKeyDefinition(AttributeDefinition primaryKeyDefinition) {
        StringBuilder builder = new StringBuilder();

        builder.append(" ").append(getTypeSqlPresent(primaryKeyDefinition));
        if (primaryKeyDefinition.isGenerated()) {
            builder.append(" AUTO_INCREMENT");
        }
        builder.append(" PRIMARY KEY");

        return builder.toString();
    }

    @Override
    public String getNoArgsInsertDefinition() {
        return "() VALUES ()";
    }

    @Override
    public String getGeneratedDefinition(AttributeDefinition attributeDefinition) {
        return "";
    }
}
