package com.zorona.liverooms;

public class Seat {
        private boolean isOccupied;
        private String userName;

        public Seat() {
            isOccupied = false;
            userName = "";
        }

        public boolean isOccupied() {
            return isOccupied;
        }

        public void occupy(String userName) {
            isOccupied = true;
            this.userName = userName;
        }

        public void vacate() {
            isOccupied = false;
            userName = "";
        }

        public String getUserName() {
            return userName;
        }


    private boolean isLocked = false;

    public boolean isLocked() {
        return isLocked;
    }

    public void lock() {
        isLocked = true;
        System.out.println("Seat locked.");
    }

    public void unlock() {
        isLocked = false;
        System.out.println("Seat unlocked.");
    }
}



