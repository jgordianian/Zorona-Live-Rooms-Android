package com.app.liverooms.emoji;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.app.liverooms.R;
import com.app.liverooms.modelclass.GiftCategoryRoot;
import com.app.liverooms.user.wallet.MyWalletActivity;
import com.app.liverooms.viewModel.EmojiSheetViewModel;
import com.app.liverooms.SessionManager;
import com.app.liverooms.databinding.FragmentEmojiBottomsheetBinding;
import com.app.liverooms.databinding.ItemEmojiGridBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class EmojiBottomsheetFragment extends BottomSheetDialogFragment {


    FragmentEmojiBottomsheetBinding binding;
    String[] country = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", " 9 ", " 10"};

    private EmojiViewPagerAdapter emojiViewPagerAdapter;
    private ItemEmojiGridBinding lastBinding = null;

    private int type;
    private EmojiSheetViewModel parentViewModel;
    SessionManager sessionManager;
    public EmojiBottomsheetFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_emoji_bottomsheet, container, false);
        parentViewModel = ViewModelProviders.of(getActivity()).get(EmojiSheetViewModel.class);
        sessionManager = new SessionManager(getActivity());
        // viewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        parentViewModel.localUserCoin.setValue(sessionManager.getUser().getDiamond());
        binding.setViewmodel(parentViewModel);
        initMain();

        initListner();
        Log.d("TAG", "onCreateView:  gift dialog ");
        return binding.getRoot();


    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
        });
    }

    private void initListner() {
        binding.tvRecharge.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyWalletActivity.class)));
        parentViewModel.localUserCoin.observe(getActivity(), integer -> binding.tvCoin.setText(String.valueOf(integer)));
        emojiViewPagerAdapter.setOnEmojiSelectLister((binding1, giftRoot) -> {
            if (lastBinding != null) {
                lastBinding.itememoji.setBackground(null);
            }

            binding1.itememoji.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_selected_5dp));
            lastBinding = binding1;
            parentViewModel.selectedGift.setValue(giftRoot);

        });


        parentViewModel.selectedGift.observe(this, giftItem -> {
            if (giftItem != null) {
                binding.btnSend.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.pink));
            } else {
                binding.btnSend.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.graylight));
                if (lastBinding != null) {
                    lastBinding.itememoji.setBackground(null);
                }
            }
        });
        binding.btnSend.setOnClickListener(v -> {
            if (parentViewModel.selectedGift.getValue() != null) {
                parentViewModel.selectedGift.getValue().setCount(Integer.parseInt(binding.tvGiftCount.getText().toString().trim()));
                // onEmojiSelectLister.onEmojiSelect(null, parentViewModel.selectedGift.getValue());
                parentViewModel.finelGift.setValue(parentViewModel.selectedGift.getValue());
            }
            parentViewModel.selectedGift.setValue(null);
            parentViewModel.finelGift.setValue(null);

        });

        ArrayAdapter<String> aa = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, country);
        binding.spinner.setAdapter(aa);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.tvGiftCount.setText(String.valueOf(country[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.tvGiftCount.setText(String.valueOf(1));
            }
        });
        binding.lytGiftCount.setOnClickListener(v -> binding.spinner.performClick());
    }

    @Override
    public void setStyle(int style, int theme) {
        super.setStyle(style, theme);

    }


    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }

    private void initMain() {
        // binding.tvCoin.setText(String.valueOf(sessionManager.getUser().getDiamond()));


        emojiViewPagerAdapter = new EmojiViewPagerAdapter(getChildFragmentManager());
        emojiViewPagerAdapter.addData(parentViewModel.categoryItemMutableLiveData.getValue());
        binding.viewPager.setAdapter(emojiViewPagerAdapter);
        binding.tablayout1.setupWithViewPager(binding.viewPager);
        settab(parentViewModel.categoryItemMutableLiveData.getValue());
        binding.tablayout1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //ll

                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    tv.setTextSize(16);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Typeface typeface = getResources().getFont(R.font.abold);
                        tv.setTypeface(typeface);
                    }
                    View indicator = (View) v.findViewById(R.id.indicator);
                    indicator.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //ll
                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.graylight));
                    tv.setTextSize(14);
                    View indicator = (View) v.findViewById(R.id.indicator);
                    indicator.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//ll
            }
        });


    }

    private void settab(List<GiftCategoryRoot.CategoryItem> contry) {
        binding.tablayout1.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tablayout1.removeAllTabs();

        if (getActivity() == null) return;
        for (int i = 0; i < contry.size(); i++) {
            binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView(i, contry.get(i).getName())));
        }

    }

    private View createCustomView(int i, String s) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabhorizontol2, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTab);
        tv.setText(s);
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.graylight));
        tv.setTextSize(14);
        View indicator = (View) v.findViewById(R.id.indicator);
        if (i == 0) {
            indicator.setVisibility(View.VISIBLE);
        } else {
            indicator.setVisibility(View.GONE);
        }
        return v;

    }
}