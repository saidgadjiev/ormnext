package ru.saidgadjiev.ormnext.core.query.visitor.element.literals;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date literal.
 */
public class DateLiteral implements Literal<Date> {

    /**
     * Current date.
     */
    private final Date time;

    /**
     * Date sql present.
     */
    private final String original;

    /**
     * Create a new instance.
     * @param time target time
     */
    public DateLiteral(Date time) {
        this.time = new Date(time.getTime());
        this.original = "DATE('" + new SimpleDateFormat(
                "dd.MM.yyyy HH:mm:ss"
        ).format(time) + "', 'yyyy.MM.dd G 'at' HH:mm:ss z')";
    }

    /**
     * Create a new instance.
     * @param time target time
     * @param format target date format {@link SimpleDateFormat}
     */
    public DateLiteral(Date time, SimpleDateFormat format) {
        this.time = new Date(time.getTime());
        this.original = "DATE('" + format.format(time) + "', 'yyyy.MM.dd G 'at' HH:mm:ss z')";
    }

    @Override
    public String getOriginal() {
        return original;
    }

    @Override
    public Date get() {
        return new Date(time.getTime());
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);

    }
}
