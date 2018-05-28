package ru.saidgadjiev.ormnext.benchmark.domain;

import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignCollectionField;

import java.util.ArrayList;
import java.util.List;

/**
 * Benchmark model.
 *
 * @author said gadjiev
 */
public class UserProfile {

    /**
     * Int field.
     */
    @DatabaseColumn(id = true, generated = true)
    private int id;

    /**
     * String field.
     */
    @DatabaseColumn
    private String firstName;

    /**
     * String field.
     */
    @DatabaseColumn
    private String lastName;

    /**
     * String field.
     */
    @DatabaseColumn
    private String middleName;

    /**
     * Foreign collection field.
     */
    @ForeignCollectionField
    private List<Order> orders = new ArrayList<>();

    /**
     * Return int field.
     * @return int field
     */
    public int getId() {
        return id;
    }

    /**
     * Set int.
     * @param id int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return string field.
     * @return string field
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set string.
     * @param firstName string
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Return string field.
     * @return string field
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set string.
     * @param lastName string
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Return string field.
     * @return string field
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Set string.
     * @param middleName string
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Return foreign collection.
     * @return foreign collection
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * Provide foreign collection.
     * @param orders provide foreign collection
     */
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "UserProfile{"
                + "id=" + id
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", middleName='" + middleName + '\''
                + ", orders='" + orders + '\''
                + '}';
    }
}
