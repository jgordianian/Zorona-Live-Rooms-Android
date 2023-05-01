package com.zorona.liverooms.emoji;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zorona.liverooms.modelclass.GiftCategoryRoot;

import java.util.ArrayList;
import java.util.List;

public class EmojiViewPagerAdapter extends FragmentPagerAdapter {


    private List<GiftCategoryRoot.CategoryItem> category = new ArrayList<>();

    public EmojiViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private OnEmojiSelectLister onEmojiSelectLister;

    public OnEmojiSelectLister getOnEmojiSelectLister() {
        return onEmojiSelectLister;
    }

    public void setOnEmojiSelectLister(OnEmojiSelectLister onEmojiSelectLister) {
        this.onEmojiSelectLister = onEmojiSelectLister;
    }

    @Override
    public Fragment getItem(int position) {
        EmojiFragment emojiFragment = new EmojiFragment(category.get(position));
        emojiFragment.setOnEmojiSelectLister((binding, giftRoot) -> onEmojiSelectLister.onEmojiSelect(binding, giftRoot));
        return emojiFragment;
    }

    @Override
    public int getCount() {
        return category.size();
    }

    public void addData(List<GiftCategoryRoot.CategoryItem> category) {

        this.category = category;
        notifyDataSetChanged();
    }
}
