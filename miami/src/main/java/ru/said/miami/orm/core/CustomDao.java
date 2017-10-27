package ru.said.miami.orm.core;

import ru.said.miami.orm.core.dao.BaseDaoImpl;
import ru.said.miami.orm.core.table.TableInfo;

import javax.sql.DataSource;

public class CustomDao extends BaseDaoImpl<Account, Integer> {

    public CustomDao(DataSource dataSource, TableInfo<Account> tableInfo) {
        super(dataSource, tableInfo);
    }
}
