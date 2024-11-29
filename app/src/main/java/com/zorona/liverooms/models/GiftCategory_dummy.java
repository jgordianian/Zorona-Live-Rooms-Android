package com.app.liverooms.models;

import java.util.List;

public class GiftCategory_dummy {
    String name;
    List<GiftRoot_dummy> giftRootDummy;

    public GiftCategory_dummy(String name, List<GiftRoot_dummy> giftRootDummy) {
        this.name = name;
        this.giftRootDummy = giftRootDummy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GiftRoot_dummy> getGiftRoot() {
        return giftRootDummy;
    }

    public void setGiftRoot(List<GiftRoot_dummy> giftRootDummy) {
        this.giftRootDummy = giftRootDummy;
    }
}
