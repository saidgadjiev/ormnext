package ru.saidgadjiev.ormnext.core.query.visitor;

import ru.saidgadjiev.ormnext.core.query.visitor.element.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.from.FromJoinedTables;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.from.FromSubQuery;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.from.FromTable;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.select.SelectAll;
import ru.saidgadjiev.ormnext.core.query.visitor.element.clause.select.SelectColumnsList;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.ColumnSpec;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedColumn;
import ru.saidgadjiev.ormnext.core.query.visitor.element.columnspec.DisplayedOperand;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.TableRef;
import ru.saidgadjiev.ormnext.core.query.visitor.element.common.UpdateValue;
import ru.saidgadjiev.ormnext.core.query.visitor.element.condition.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.attribute.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.table.ForeignKeyConstraint;
import ru.saidgadjiev.ormnext.core.query.visitor.element.constraints.table.UniqueConstraint;
import ru.saidgadjiev.ormnext.core.query.visitor.element.function.*;
import ru.saidgadjiev.ormnext.core.query.visitor.element.join.JoinInfo;
import ru.saidgadjiev.ormnext.core.query.visitor.element.join.LeftJoin;
import ru.saidgadjiev.ormnext.core.query.visitor.element.literals.*;

/**
 * Interface to make use of the Visitor pattern programming style.
 * I.e. a class that implements this interface can traverse the contents of
 * a Java class just by calling the `accept' method which all classes have.
 *
 * @author Said Gadjiev
 * @see QueryElement
 */
public interface QueryVisitor {

    /**
     * Visit {@link CreateQuery} element.
     *
     * @param createQuery taget visitor element
     * @return true if need visit another visitor elements that contained in createQuery else false
     * @see CreateQuery
     */
    boolean visit(CreateQuery createQuery);

    /**
     * Visit {@link UpdateValue} element.
     *
     * @param updateValue taget visitor element
     * @return true if need visit another visitor elements that contained in {@code updateValue} else false
     * @see UpdateValue
     */
    boolean visit(UpdateValue updateValue);

    /**
     * Visit {@link StringLiteral} element.
     *
     * @param stringLiteral target visitor element
     * @see UpdateValue
     */
    void visit(StringLiteral stringLiteral);

    /**
     * Visit {@link UpdateValue} element.
     *
     * @param selectQuery target visitor element
     * @return true if need visit another visitor elements that contained in {@code updateValue} else false
     * @see UpdateValue
     */
    boolean visit(SelectQuery selectQuery);

    /**
     * Visit {@link Expression} element.
     *
     * @param expression target visitor element
     * @return true if need visit another visitor elements that contained in {@code expression} else false
     * @see Expression
     */
    boolean visit(Expression expression);

    /**
     * Visit {@link AndCondition} element.
     *
     * @param andCondition target visitor element
     * @return true if need visit another visitor elements that contained in {@code andCondition} else false
     * @see AndCondition
     */
    boolean visit(AndCondition andCondition);

    /**
     * Visit {@link Equals} element.
     *
     * @param equals target visitor element
     * @return true if need visit another visitor elements that contained in {@code equals} else false
     * @see Equals
     */
    boolean visit(Equals equals);

    /**
     * Visit {@link ColumnSpec} element.
     *
     * @param columnSpec target visitor element
     * @return true if need visit another visitor elements that contained in {@code columnSpec} else false
     * @see ColumnSpec
     */
    boolean visit(ColumnSpec columnSpec);

    /**
     * Visit {@link TableRef} element.
     *
     * @param tableRef target visitor element
     * @return true if need visit another visitor elements that contained in {@code tableRef} else false
     * @see TableRef
     */
    boolean visit(TableRef tableRef);

    /**
     * Visit {@link AttributeDefinition} element.
     *
     * @param attributeDefinition target visitor element
     * @return true if need visit another visitor elements that contained in {@code attributeDefinition} else false
     * @see AttributeDefinition
     */
    boolean visit(AttributeDefinition attributeDefinition);

    /**
     * Visit {@link CreateTableQuery} element.
     *
     * @param createTableQuery target visitor element
     * @return true if need visit another visitor elements that contained in {@code getCreateTableQuery} else false
     * @see CreateTableQuery
     */
    boolean visit(CreateTableQuery createTableQuery);

    /**
     * Visit {@link DeleteQuery}.
     *
     * @param deleteQuery target visitor element
     * @return true if need visit another visitor elements that contained in {@code countExpression} else false
     * @see UpdateValue
     */
    boolean visit(DeleteQuery deleteQuery);

    /**
     * Visit {@link IntLiteral} element.
     *
     * @param intLiteral target visitor element
     * @see IntLiteral
     */
    void visit(IntLiteral intLiteral);

    /**
     * Visit {@link UpdateQuery} element.
     *
     * @param updateQuery target visitor element
     * @return true if need visit another visitor elements that contained in {@code updateQuery} else false
     * @see UpdateQuery
     */
    boolean visit(UpdateQuery updateQuery);

    /**
     * Visit {@link DropTableQuery} element.
     *
     * @param dropTableQuery target visitor element
     * @see DropTableQuery
     */
    void visit(DropTableQuery dropTableQuery);

    /**
     * Visit {@link UniqueConstraint} element.
     *
     * @param uniqueConstraint target visitor element
     * @see UniqueConstraint
     */
    void visit(UniqueConstraint uniqueConstraint);

    /**
     * Visit {@link NotNullConstraint} element.
     *
     * @param notNullConstraint target visitor element
     * @see NotNullConstraint
     */
    void visit(NotNullConstraint notNullConstraint);

    /**
     * Visit {@link ReferencesConstraint} element.
     *
     * @param referencesConstraint target visitor element
     * @see ReferencesConstraint
     */
    void visit(ReferencesConstraint referencesConstraint);

    /**
     * Visit {@link CreateIndexQuery} element.
     *
     * @param createIndexQuery target visitor element
     * @see CreateIndexQuery
     */
    void visit(CreateIndexQuery createIndexQuery);

    /**
     * Visit {@link DropIndexQuery} element.
     *
     * @param dropIndexQuery target visitor element
     * @see DropIndexQuery
     */
    void visit(DropIndexQuery dropIndexQuery);

    /**
     * Visit {@link Param} element.
     *
     * @param param target visitor element
     * @see Param
     */
    void visit(Param param);

    /**
     * Visit {@link SelectAll} element.
     *
     * @param selectAll target visitor element
     * @see SelectAll
     */
    void visit(SelectAll selectAll);

    /**
     * Visit {@link SelectColumnsList} element.
     *
     * @param selectColumnsList target visitor element
     * @return true if need visit another visitor elements that contained in {@code selectColumnsList} else false
     * @see SelectColumnsList
     */
    boolean visit(SelectColumnsList selectColumnsList);

    /**
     * Visit {@link Having} element.
     *
     * @param having target visitor element
     * @return true if need visit another visitor elements that contained in {@code having} else false
     * @see Having
     */
    boolean visit(Having having);

    /**
     * Visit {@link GroupBy} element.
     *
     * @param groupBy target visitor element
     * @return true if need visit another visitor elements that contained in {@code addGroupBy} else false
     * @see GroupBy
     */
    boolean visit(GroupBy groupBy);

    /**
     * Visit {@link FromTable} element.
     *
     * @param fromTable target visitor element
     * @return true if need visit another visitor elements that contained in {@code fromTable} else false
     * @see FromTable
     */
    boolean visit(FromTable fromTable);

    /**
     * Visit {@link LeftJoin} element.
     *
     * @param leftJoin target visitor element
     * @return true if need visit another visitor elements that contained in {@code leftJoin} else false
     * @see LeftJoin
     */
    boolean visit(LeftJoin leftJoin);

    /**
     * Visit {@link BooleanLiteral} element.
     *
     * @param booleanLiteral target visitor element
     * @see BooleanLiteral
     */
    void visit(BooleanLiteral booleanLiteral);

    /**
     * Visit {@link JoinInfo} element.
     *
     * @param joinInfo target visitor element
     * @return true if need visit another visitor elements that contained in {@code joinInfo} else false
     * @see JoinInfo
     */
    boolean visit(JoinInfo joinInfo);

    /**
     * Visit {@link CountAll} element.
     *
     * @param countAll target visitor element
     * @see CountAll
     */
    void visit(CountAll countAll);

    /**
     * Visit {@link FromJoinedTables} element.
     *
     * @param fromJoinedTables target visitor element
     * @return true if need visit another visitor elements that contained in {@code fromJoinedTables} else false
     * @see FromJoinedTables
     */
    boolean visit(FromJoinedTables fromJoinedTables);

    /**
     * Visit {@link DisplayedColumn} element.
     *
     * @param displayedColumn target visitor element
     * @return true if need visit another visitor elements that contained in {@code displayedColumn} else false
     * @see DisplayedColumn
     */
    boolean visit(DisplayedColumn displayedColumn);

    /**
     * Visit {@link AVG} element.
     *
     * @param avg target visitor element
     * @return true if need visit another visitor elements that contained in {@code avg} else false
     * @see AVG
     */
    boolean visit(AVG avg);

    /**
     * Visit {@link CountExpression} element.
     *
     * @param countExpression target visitor element
     * @return true if need visit another visitor elements that contained in {@code countExpression} else false
     * @see CountExpression
     */
    boolean visit(CountExpression countExpression);

    /**
     * Visit {@link MAX} element.
     *
     * @param max target visitor element
     * @return true if need visit another visitor elements that contained in {@code max} else false
     * @see MAX
     */
    boolean visit(MAX max);

    /**
     * Visit {@link MIN} element.
     *
     * @param min target visitor element
     * @return true if need visit another visitor elements that contained in {@code min} else false
     * @see MIN
     */
    boolean visit(MIN min);

    /**
     * Visit {@link Exists} element.
     *
     * @param exists target visitor element
     * @return true if need visit another visitor elements that contained in {@code exists} else false
     * @see Exists
     */
    boolean visit(Exists exists);

    /**
     * Visit {@link InSelect} element.
     *
     * @param inSelect target visitor element
     * @return true if need visit another visitor elements that contained in {@code inSelect} else false
     * @see InSelect
     */
    boolean visit(InSelect inSelect);

    /**
     * Visit {@link NotInSelect} element.
     *
     * @param notInSelect target visitor element
     * @return true if need visit another visitor elements that contained in {@code notInSelect} else false
     * @see NotInSelect
     */
    boolean visit(NotInSelect notInSelect);

    /**
     * Visit {@link GreaterThan} element.
     *
     * @param greaterThan target visitor element
     * @return true if need visit another visitor elements that contained in {@code greaterThan} else false
     * @see GreaterThan
     */
    boolean visit(GreaterThan greaterThan);

    /**
     * Visit {@link GreaterThanOrEquals} element.
     *
     * @param greaterThanOrEquals target visitor element
     * @return true if need visit another visitor elements that contained in {@code greaterThanOrEquals} else false
     * @see GreaterThanOrEquals
     */
    boolean visit(GreaterThanOrEquals greaterThanOrEquals);

    /**
     * Visit {@link LessThan} element.
     *
     * @param lessThan target visitor element
     * @return true if need visit another visitor elements that contained in {@code lessThan} else false
     * @see LessThan
     */
    boolean visit(LessThan lessThan);

    /**
     * Visit {@link LessThanOrEquals} element.
     *
     * @param lessThanOrEquals target visitor element
     * @return true if need visit another visitor elements that contained in {@code lessThanOrEquals} else false
     * @see LessThanOrEquals
     */
    boolean visit(LessThanOrEquals lessThanOrEquals);

    /**
     * Visit {@link SUM} element.
     *
     * @param sum target visitor element
     * @return true if need visit another visitor elements that contained in {@code sum} else false
     * @see SUM
     */
    boolean visit(SUM sum);

    /**
     * Visit {@link OperandCondition} element.
     *
     * @param operandCondition target visitor element
     * @return true if need visit another visitor elements that contained in {@code operandCondition} else false
     * @see OperandCondition
     */
    boolean visit(OperandCondition operandCondition);

    /**
     * Visit {@link Alias} element.
     *
     * @param alias target visitor element
     * @see Alias
     */
    void visit(Alias alias);

    /**
     * Visit {@link DateLiteral} element.
     *
     * @param dateLiteral target visitor element
     * @see DateLiteral
     */
    void visit(DateLiteral dateLiteral);

    /**
     * Visit {@link Default} element.
     *
     * @param aDefault target visitor element
     * @see Default
     */
    void visit(Default aDefault);

    /**
     * Visit {@link DisplayedOperand} element.
     *
     * @param displayedOperand target visitor element
     * @return true if need visit another visitor elements that contained in {@code displayedOperand} else false
     * @see DisplayedOperand
     */
    boolean visit(DisplayedOperand displayedOperand);

    /**
     * Visit {@link FloatLiteral} element.
     *
     * @param floatLiteral target visitor element
     * @see FloatLiteral
     */
    void visit(FloatLiteral floatLiteral);

    /**
     * Visit {@link DoubleLiteral} element.
     *
     * @param doubleLiteral target visitor element
     * @see DoubleLiteral
     */
    void visit(DoubleLiteral doubleLiteral);

    /**
     * Visit {@link OrderBy} element.
     *
     * @param orderBy target visitor element
     * @return true if need visit another visitor elements that contained in {@code orderBy} else false
     * @see OrderBy
     */
    boolean visit(OrderBy orderBy);

    /**
     * Visit {@link Limit} element.
     *
     * @param limit target visitor element
     * @see Limit
     */
    void visit(Limit limit);

    /**
     * Visit {@link Offset} element.
     *
     * @param offset target visitor element
     * @see Offset
     */
    void visit(Offset offset);

    /**
     * Visit {@link NotNull} element.
     *
     * @param notNull target visitor element
     * @return true if need visit another visitor elements that contained in {@code notNull} else false
     * @see NotNull
     */
    boolean visit(NotNull notNull);

    /**
     * Visit {@link IsNull} element.
     *
     * @param isNull target visitor element
     * @return true if need visit another visitor elements that contained in {@code isNull} else false
     * @see IsNull
     */
    boolean visit(IsNull isNull);

    /**
     * Visit {@link NotEquals} element.
     *
     * @param notEquals target visitor element
     * @return true if need visit another visitor elements that contained in {@code notEquals} else false
     * @see NotEquals
     */
    boolean visit(NotEquals notEquals);

    /**
     * Visit {@link Like} element.
     *
     * @param like target visitor element
     * @return true if need visit another visitor elements that contained in {@code like} else false
     * @see Like
     */
    boolean visit(Like like);

    /**
     * Visit {@link Between} element.
     *
     * @param between target visitor element
     * @return true if need visit another visitor elements that contained in {@code between} else false
     * @see Between
     */
    boolean visit(Between between);

    /**
     * Visit {@link Not} element.
     *
     * @param not target visitor element
     * @return true if need visit another visitor elements that contained in {@code not} else false
     * @see Not
     */
    boolean visit(Not not);

    /**
     * Visit {@link NotInValues} element.
     *
     * @param notInValues target visitor element
     * @return true if need visit another visitor elements that contained in {@code notInValues} else false
     * @see NotInValues
     */
    boolean visit(NotInValues notInValues);

    /**
     * Visit {@link InValues} element.
     *
     * @param inValues target visitor element
     * @return true if need visit another visitor elements that contained in {@code inValues} else false
     * @see InValues
     */
    boolean visit(InValues inValues);

    /**
     * Visit {@link ForeignKeyConstraint} element.
     *
     * @param foreignKeyConstraint target visitor element
     * @return true if need visit another visitor elements that contained in {@code foreignKeyConstraint} else false
     * @see ForeignKeyConstraint
     */
    boolean visit(ForeignKeyConstraint foreignKeyConstraint);

    /**
     * Visit {@link FromSubQuery} element.
     *
     * @param fromSubQuery target visitor element
     * @return true if need visit another visitor elements that contained in {@code fromSubQuery} else false
     * @see FromSubQuery
     */
    boolean visit(FromSubQuery fromSubQuery);

    /**
     * Visit {@link GroupByItem} element.
     *
     * @param groupByItem target visitor element
     * @return true if need visit another visitor elements that contained in {@code groupByItem} else false
     * @see GroupByItem
     */
    boolean visit(GroupByItem groupByItem);

    /**
     * Visit {@link OrderByItem} element.
     *
     * @param orderByItem target visitor element
     * @return true if need visit another visitor elements that contained in {@code orderByItem} else false
     * @see OrderByItem
     */
    boolean visit(OrderByItem orderByItem);

    /**
     * Visit {@link CountColumn} element.
     *
     * @param countColumn target visitor element
     * @return true if need visit another visitor elements that contained in {@code countColumn} else false
     * @see CountColumn
     */
    boolean visit(CountColumn countColumn);

    /**
     * Visit {@link SqlLiteral} element.
     *
     * @param sqlLiteral target visitor element
     * @see SqlLiteral
     */
    void visit(SqlLiteral sqlLiteral);

    /**
     * Visit {@link UniqueAttributeConstraint} element.
     *
     * @param uniqueAttributeConstraint target visitor element
     * @see UniqueAttributeConstraint
     */
    void visit(UniqueAttributeConstraint uniqueAttributeConstraint);
}
