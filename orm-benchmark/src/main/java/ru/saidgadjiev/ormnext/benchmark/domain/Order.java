package ru.saidgadjiev.ormnext.benchmark.domain;

import ru.saidgadjiev.ormnext.core.field.DataType;
import ru.saidgadjiev.ormnext.core.field.DatabaseColumn;
import ru.saidgadjiev.ormnext.core.field.ForeignColumn;

import java.util.Date;

public class Order {

    @DatabaseColumn(id = true, generated = true, dataType = 8)
    private int id;

    @DatabaseColumn
    private String description;

    @DatabaseColumn(dataType = DataType.DATE)
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
