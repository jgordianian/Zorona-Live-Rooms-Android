package com.app.liverooms.emoji;

import com.app.liverooms.databinding.ItemEmojiGridBinding;
import com.app.liverooms.modelclass.GiftRoot;

public interface OnEmojiSelectLister {
    void onEmojiSelect(ItemEmojiGridBinding binding, GiftRoot.GiftItem giftRootDummy);
}
