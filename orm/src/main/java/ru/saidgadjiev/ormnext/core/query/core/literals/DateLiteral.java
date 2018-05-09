package ru.saidgadjiev.ormnext.core.query.core.literals;

import ru.saidgadjiev.ormnext.core.query.visitor.QueryVisitor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateLiteral implements Literal<Date> {

    private final Date time;

    private final String original;

    public DateLiteral(Date time) {
        this.time = time;
        this.original = "DATE('" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(time) + "', 'yyyy.MM.dd G 'at' HH:mm:ss z')";
    }

    public DateLiteral(Date time, SimpleDateFormat format) {
        this.time = time;
        this.original = "DATE('" + format.format(time) + "', 'yyyy.MM.dd G 'at' HH:mm:ss z')";
    }

    @Override
    public String getOriginal() {
        return original;
    }

    public Date get() {
        return this.time;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);

    }
}
