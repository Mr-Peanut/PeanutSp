package com.guan.peanutsp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

/**
 * Created by guans on 2016/12/5.
 */
public class CloseConfirmFragment extends DialogFragment {
    private Button cancle;
    private Button confirm;
//    private DailogController mController;
    private static List<Activity> activities;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        this.setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        System.out.println(getTag());
        View view=getLayoutInflater(savedInstanceState).inflate(R.layout.closeconfirmdailog_layout,null);
//        View view =inflater.inflate(R.layout.closeconfirmdailog_layout,null);
        cancle= (Button)view.findViewById(R.id.concle);
        confirm=(Button)view.findViewById(R.id.confirm);
        DialogListener mListener = new DialogListener();
        cancle.setOnClickListener(mListener);
        confirm.setOnClickListener(mListener);
        return view;
    }

//    public void setmController(DailogController mController) {
//        this.mController = mController;
//    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    class DialogListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.confirm:
                        activities.get(0).finish();
                    break;
                case R.id.concle:
                    CloseConfirmFragment.this.dismiss();
                    break;
            }
        }
    }
    public interface DailogController{
         void confirm();
    }
}
