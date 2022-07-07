package com.ViewPagerCards;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.ridetogo.user.R;
import com.dialogs.OpenListView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.utils.Logger;
import com.view.MTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RideDeliveryCardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    ArrayList<HashMap<String, String>> mData;
    private float mBaseElevation;
    Context mContext;
    OnItemClickList onItemClickList;

    public RideDeliveryCardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(HashMap<String, String> bannerObj, Context context, OnItemClickList onItemClickList) {
        mViews.add(null);
        mData.add(bannerObj);
        this.mContext = context;
        this.onItemClickList = onItemClickList;
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.ridedelivery_adapter, container, false);
        container.addView(view);
        bind(mData.get(position).get("vImage"), view);


        ImageView  imageView = view.findViewById(R.id.imagView);
        imageView.setTag(position);
        MTextView titleTxt = view.findViewById(R.id.titleTxt);
        MTextView subTitleTxt = view.findViewById(R.id.subTitleTxt);
        MTextView btnTxt = view.findViewById(R.id.btnTxt);

        titleTxt.setText(mData.get(position).get("vTitle"));
        subTitleTxt.setText(mData.get(position).get("vSubtitle"));
        btnTxt.setText(mData.get(position).get("vBtnTtitle"));
        titleTxt.setTextColor(Color.parseColor(mData.get(position).get("vTextColor")));
        subTitleTxt.setTextColor(Color.parseColor(mData.get(position).get("vTextColor")));
        btnTxt.setTextColor(Color.parseColor(mData.get(position).get("vBtnTextColor")));
        btnTxt.getBackground().setTint(Color.parseColor(mData.get(position).get("vBtnBgColor")));
        btnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickList.onBannerItemClick(position);

            }
        });


        // position = position % mViews.size();
        Picasso.get()
                .load(mData.get(position).get("vImage"))
                .fit()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        return view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(String item, View view) {

    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public interface OnItemClickList {
        void onBannerItemClick(int position);
    }
}
