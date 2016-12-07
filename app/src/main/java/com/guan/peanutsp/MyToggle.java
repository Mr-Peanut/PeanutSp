package com.guan.peanutsp;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
/**
 * Created by guans on 2016/12/6.
 */
public class MyToggle extends ActionBarDrawerToggle {
    private MDrawerListener mDrawerLayoutListener;
    public MDrawerListener getmDrawerLayoutListener() {
        return mDrawerLayoutListener;
    }

    public void setmDrawerLayoutListener(MDrawerListener mDrawerLayoutListener) {
        this.mDrawerLayoutListener = mDrawerLayoutListener;
    }


    public MyToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
    }
        @Override
        public void onDrawerOpened(View drawerView) {
            mDrawerLayoutListener.OnDrawerOpen();
            super.onDrawerOpened(drawerView);
        }
    }
     interface MDrawerListener{
         void OnDrawerOpen();
    }