package com.balance.model;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;

/**
 * Created by da_20 on 31/5/2017.
 */
@Entity
public class Band {
    private Long id;

    private Integer steps;
    private Integer bpm;
    private Long distance;
    private Date fecha_registro;
    private Integer x;
    private Integer y;
    private Integer calories;
    private Integer user;

    public Integer getBpm() {
        return bpm;
    }

    public void setBpm(Integer bpm) {
        this.bpm = bpm;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public Date getFecha_registro() {
        if (fecha_registro == null) {
            return new Date();
        }
        return fecha_registro;
    }

    public void setFecha_registro(Date fecha_registro) {
        if (fecha_registro != null) {
            this.fecha_registro = fecha_registro;
        }
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

    public void asignar(Long id, Integer steps, Integer bpm, Long distance, Date fecha_registro, Integer x, Integer y, Integer calories, Integer user) {
        this.id = id;
        this.steps = steps;
        this.bpm = bpm;
        this.distance = distance;
        this.fecha_registro = fecha_registro;
        this.x = x;
        this.y = y;
        this.calories = calories;
        this.user = user;
    }
}
