package com.zorona.liverooms;

import java.util.HashMap;
import java.util.Map;

public class SeatManager {
    private boolean[] seatLocks;

    public SeatManager(int numberOfSeats) {
        seatLocks = new boolean[numberOfSeats];
    }

    public void lockSeat(int seatIndex) {
        if (isValidSeatIndex(seatIndex)) {
            seatLocks[seatIndex] = true;
            System.out.println("Seat " + seatIndex + " locked. Others cannot take this seat.");
        } else {
            System.out.println("Invalid seat index.");
        }
    }

    public void unlockSeat(int seatIndex) {
        if (isValidSeatIndex(seatIndex)) {
            seatLocks[seatIndex] = false;
            System.out.println("Seat " + seatIndex + " unlocked. Others can now take this seat.");
        } else {
            System.out.println("Invalid seat index.");
        }
    }

    private boolean isValidSeatIndex(int seatIndex) {
        return seatIndex >= 0 && seatIndex < seatLocks.length;
    }

    public static void main(String[] args) {
        SeatManager seatManager = new SeatManager(10);

        seatManager.lockSeat(3);
        seatManager.unlockSeat(5);
    }
}

