package ru.saidgadjiev.ormnext.benchmark.domain;

import ru.saidgadjiev.ormnext.core.field.*;

import java.util.Date;

/**
 * Benchmark model.
 *
 * @author said gadjiev
 */
public class Order {

    /**
     * Int field.
     */
    @DatabaseColumn(id = true, generated = true)
    private int id;

    /**
     * String field.
     */
    @DatabaseColumn
    private String description;

    /**
     * Date field.
     */
    @ConverterGroup(converters = {
            @Converter(TimestampToDate.class)
    })
    @DatabaseColumn(dataType = DataType.TIMESTAMP)
    private Date date = new Date();

    /**
     * Foreign field.
     */
    @ForeignColumn
    private UserProfile userProfile;

    /**
     * Return int field.
     *
     * @return int field
     */
    public int getId() {
        return id;
    }

    /**
     * Set int.
     *
     * @param id int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return string field.
     *
     * @return string field
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set string.
     *
     * @param description string
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return date field.
     *
     * @return date field
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set date.
     *
     * @param date date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Return foreign field.
     *
     * @return foreign field
     */
    public UserProfile getUserProfile() {
        return userProfile;
    }

    /**
     * Set foreign field.
     *
     * @param userProfile foreign field
     */
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public String toString() {
        return "Order{"
                + "id=" + id
                + ", description='" + description + '\''
                + ", date=" + date
                + ", userProfile=" + userProfile.getId()
                + '}';
    }
}
