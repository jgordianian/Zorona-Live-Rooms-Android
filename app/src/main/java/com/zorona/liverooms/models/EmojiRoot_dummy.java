package com.zorona.liverooms.models;

import android.graphics.drawable.Drawable;

import java.util.UUID;

public class EmojiRoot_dummy {
    UUID id;
    Drawable drawable;
    int coin;

    public EmojiRoot_dummy(String id, Drawable drawable, int coin) {
        this.id = UUID.randomUUID();
        ;
        this.drawable = drawable;
        this.coin = coin;
    }
}
