package com.balance.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by da_20 on 5/6/2017.
 */
@Entity
public class LocationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="location_history_id")
    private Long id;

    private Integer x;
    private Integer y;
    private Integer user;
    private Date date;

    public LocationHistory(Integer x, Integer y, Integer user, Date date) {
        this.x = x;
        this.y = y;
        this.user = user;
        this.date = date;
    }

    public LocationHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
