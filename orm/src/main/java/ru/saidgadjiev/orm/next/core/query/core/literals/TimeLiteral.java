package ru.saidgadjiev.orm.next.core.query.core.literals;

import ru.saidgadjiev.orm.next.core.query.visitor.QueryVisitor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeLiteral implements Literal<Date> {

    private final Date time;

    private final String original;

    public TimeLiteral(Date time) {
        this.time = time;
        this.original = "DATE('" + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(time) + "', 'yyyy.MM.dd G 'at' HH:mm:ss z')";
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
        visitor.start(this);
        visitor.finish(this);
    }
}
