package ru.said.miami.orm.core;

import ru.said.miami.orm.core.query.core.Alias;

public class Test {

    public static void main(String[] args) {
        Alias alias = new Alias("a");
        test(alias);
        System.out.println(alias.getAlias());
    }

    private static void test(Alias a) {
        a.setAlias("test");
    }
}
