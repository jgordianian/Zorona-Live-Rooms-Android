package com.zorona.liverooms.emoji;

import com.zorona.liverooms.databinding.ItemEmojiGridBinding;
import com.zorona.liverooms.modelclass.GiftRoot;

public interface OnEmojiSelectLister {
    void onEmojiSelect(ItemEmojiGridBinding binding, GiftRoot.GiftItem giftRootDummy);
}
