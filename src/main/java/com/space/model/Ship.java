package com.space.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ship")
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "name")
    public String name;

    @Column(name = "planet")
    public String planet;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipType")
    public ShipType shipType;

    @Column(name = "prodDate")
    public Date prodDate;


    @Column(name = "isUsed")
    public Boolean isUsed;

    @Column(name = "speed")
    public Double speed;

    @Column(name = "crewSize")
    public Integer crewSize;

    @Column(name = "rating")
    public Double rating;

    public Long getProdDate() {
        return prodDate != null ? prodDate.getTime() : null;
    }

    public void setProdDate(Long prodDate) {
        this.prodDate = new Date(prodDate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public void setCrewSize(Integer crewSize) {
        this.crewSize = crewSize;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}