package com.app.liverooms.liveStreamming;

import java.util.Objects;

public class SeatKey {
    private String channel;
    private int seatIndex;
    private int agoraUID;

    public SeatKey(String channel, int seatIndex, int agoraUID) {
        this.channel = channel;
        this.seatIndex = seatIndex;
        this.agoraUID = agoraUID;
    }

    public String getChannel() {
        return channel;
    }

    public int getSeatIndex() {
        return seatIndex;
    }

    public int getagoraUID() {
        return agoraUID;
    }

    // Implement equals and hashCode methods for proper HashMap functioning
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatKey seatKey = (SeatKey) o;
        return channel == seatKey.channel && seatIndex == seatKey.seatIndex && agoraUID == seatKey.agoraUID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, seatIndex, agoraUID);
    }

}