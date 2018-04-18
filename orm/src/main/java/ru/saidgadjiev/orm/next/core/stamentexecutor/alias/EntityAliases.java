package ru.saidgadjiev.orm.next.core.stamentexecutor.alias;

import java.util.Collection;
import java.util.List;

public interface EntityAliases {

    String getTableAlias();

    List<String> getColumnAliases();

    String getKeyAlias();
}
