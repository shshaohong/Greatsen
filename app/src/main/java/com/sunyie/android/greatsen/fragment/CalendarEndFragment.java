package com.sunyie.android.greatsen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunyie.android.greatsen.FilterActivity;
import com.sunyie.android.greatsen.R;

/**
 * Created by shaohong on 2017-2-27.
 */

public class CalendarEndFragment extends DialogFragment {
    private CalendarView mCalendarView;
    private ImageView mImageView;
    private Button btnNext;
    private TextView mTextView;
    private int endYear;
    private int endMonth;
    private int endDay;
    private View mView;
    private int mStartYear1;
    private String mStartYear;
    private String mStartMonth;
    private String mStartDay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mView == null) {
            mView = inflater.inflate(R.layout.mycalendar, container, false);
        }
        Bundle bundle = getArguments();
        mStartYear = bundle.getInt("startYear") + "";
        mStartMonth = bundle.getInt("startMonth") + "";
        mStartDay = bundle.getInt("startDay") + "";

        mCalendarView = (CalendarView) mView.findViewById(R.id.cancel_action);
        mImageView = (ImageView) mView.findViewById(R.id.backImageView);
        btnNext = (Button) mView.findViewById(R.id.btn_next);
        mTextView = (TextView) mView.findViewById(R.id.tv_search);
        mTextView.setText("选择结束日期");
        btnNext.setText("完成");

        setOnClickLiener();
        return mView;
    }

    private void setOnClickLiener(){
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                String date = year + "年" + (month+1) + "月" + dayOfMonth + "日";
                endYear = year;
                endMonth = month+1;
                endDay = dayOfMonth;
                Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalBroadcastManager.getInstance(getContext())
                        .sendBroadcast(new Intent(FilterActivity.DATA_RESULT)
                                .putExtra("mStartYear", mStartYear)
                                .putExtra("mStartMonth", mStartMonth)
                                .putExtra("mStartDay",mStartDay)
                                .putExtra("endYear",endYear)
                                .putExtra("endMonth",endMonth)
                                .putExtra("endDay",endDay)
                        );
                getDialog().cancel();
            }
        });
    }
}
