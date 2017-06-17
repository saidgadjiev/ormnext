package dao.visitor;

/**
 * Created by said on 17.06.17.
 */
public interface QueryElement {
    void accept(QueryVisitor visitor);
}
