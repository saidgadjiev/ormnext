package ru.saidgadjiev.ormnext.core.query.criteria.compiler;

import ru.saidgadjiev.ormnext.core.loader.Argument;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.DeleteStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.SelectStatement;
import ru.saidgadjiev.ormnext.core.query.criteria.impl.UpdateStatement;
import ru.saidgadjiev.ormnext.core.query.visitor.element.DeleteQuery;
import ru.saidgadjiev.ormnext.core.query.visitor.element.SelectQuery;
import ru.saidgadjiev.ormnext.core.query.visitor.element.SqlStatement;
import ru.saidgadjiev.ormnext.core.query.visitor.element.UpdateQuery;

import java.util.Map;

/**
 * Statement compiler.
 *
 * @author Said Gadjiev
 */
public interface StatementCompiler {

    /**
     * Compile select statement.
     *
     * @param selectStatement target statement
     * @return compile result
     */
    StatementCompileResult<SelectQuery> compile(SelectStatement<?> selectStatement);

    /**
     * Compile update statement.
     *
     * @param updateStatement target statement
     * @return compile result
     */
    StatementCompileResult<UpdateQuery> compile(UpdateStatement updateStatement);

    /**
     * Compile delete statement.
     *
     * @param deleteStatement target statement
     * @return compile result
     */
    StatementCompileResult<DeleteQuery> compile(DeleteStatement deleteStatement);

    /**
     * Compile result.
     *
     * @param <T> compiled query type
     */
    interface StatementCompileResult<T extends SqlStatement> {

        /**
         * Retrieve compiled query.
         *
         * @return compiled query
         */
        T getQuery();

        /**
         * Retrieve query arguments.
         *
         * @return query arguments
         */
        Map<Integer, Argument> getArguments();

    }
}
