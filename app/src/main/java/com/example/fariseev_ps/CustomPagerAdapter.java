package com.example.fariseev_ps;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.Vector;

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private Vector<View> pages;

    public CustomPagerAdapter(Context context, Vector<View> pages) {
        this.mContext=context;
        this.pages=pages;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View page = pages.get(position);
        container.addView(page);
        //Log.d("--","позиция "+position);
        return page;
    }

    @Override
    public int getCount() {

        return pages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Title " + position;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
