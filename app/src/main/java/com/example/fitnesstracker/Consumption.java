package com.example.fitnesstracker;

import java.util.Date;

public class Consumption {
    private Integer consumptionId;
    private Users userId;
    private Food foodId;
    private Date dateConsumed;
    private double servings;

    public Consumption(Integer consumptionId, Users userId, Food foodId, Date dateConsumed, double servings) {
        this.consumptionId = consumptionId;
        this.userId = userId;
        this.foodId = foodId;
        this.dateConsumed = dateConsumed;
        this.servings = servings;
    }

    public Integer getConsumptionId() {
        return consumptionId;
    }

    public void setConsumptionId(Integer consumptionId) {
        this.consumptionId = consumptionId;
    }

    public Date getDateConsumed() {
        return dateConsumed;
    }

    public void setDateConsumed(Date dateConsumed) {
        this.dateConsumed = dateConsumed;
    }

    public double getServings() {
        return servings;
    }

    public void setServings(double servings) {
        this.servings = servings;
    }

    public Food getFoodId() {
        return foodId;
    }

    public void setFoodId(Food foodId) {
        this.foodId = foodId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }
}
