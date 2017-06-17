package clause.element;

/**
 * Created by said on 17.06.17.
 */
public abstract class Literal<T> implements Value {

    abstract T getValue();
}
