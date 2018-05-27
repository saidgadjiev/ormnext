package ru.saidgadjiev.ormnext.benchmark.domain;

import ru.saidgadjiev.ormnext.core.field.*;

import java.util.Date;

public class Order {

    @DatabaseColumn(id = true, generated = true, dataType = 12)
    private int id;

    @DatabaseColumn
    private String description;

    @ConverterGroup(converters = {
            @Converter(TimestampToDate.class)
    })
    @DatabaseColumn(dataType = DataType.TIMESTAMP)
    private Date date = new Date();

    @ForeignColumn
    private UserProfile userProfile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", userProfile=" + userProfile.getId() +
                '}';
    }
}
