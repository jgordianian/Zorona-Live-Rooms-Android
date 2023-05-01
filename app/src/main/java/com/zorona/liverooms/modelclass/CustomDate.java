package com.zorona.liverooms.modelclass;

public class CustomDate {

    /**
     * custom class  date is for  two diffrent date types
     * for example  server want   mm/dd/yyyy
     * also human readable is  dd/mm/yyyy
     */


    int day, month, year;

    public CustomDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDateForHuman() {
        return day + " / " + month + " / " + year;
    }

    public String getDateForServer() {
        return month + "/" + +day + "/" + year;
    }
}
