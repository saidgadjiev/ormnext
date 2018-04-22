package ru.saidgadjiev.orm.next.core.test;

import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.saidgadjiev.orm.next.core.test.model.hibernate.A;
import ru.saidgadjiev.orm.next.core.test.model.hibernate.B;
import ru.saidgadjiev.orm.next.core.test.model.hibernate.C;
import ru.saidgadjiev.orm.next.core.test.model.hibernate.D;

public class TestHibernate {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(A.class);
        configuration.addAnnotatedClass(B.class);
        configuration.addAnnotatedClass(C.class);
        configuration.addAnnotatedClass(D.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL94Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://127.0.0.1:5432/ormtest");
        configuration.setProperty("hibernate.connection.username", "postgres");
        configuration.setProperty("hibernate.connection.password", "postgres");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbmdl.auto", "update");

        SessionFactory sessionFactory = createSessionFactory(configuration);
        Session session = sessionFactory.openSession();

        session.getCriteriaBuilder().createQuery();
        System.out.println( session.createCriteria(B.class).list());
        session.close();
        sessionFactory.close();
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();

        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();

        return configuration.buildSessionFactory(serviceRegistry);
    }
}
