package com.sunyie.android.greatsen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import com.sunyie.android.greatsen.R;

/**
 * Created by shaohong on 2017-2-27.
 */

public class CalendarStartFragment extends DialogFragment {
    private CalendarView mCalendarView;
    private ImageView mImageView;
    private Button btnNext;
    private int startYear;
    private int startMonth;
    private int startDay;
    private View mView;

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
        mCalendarView = (CalendarView) mView.findViewById(R.id.cancel_action);
        mImageView = (ImageView) mView.findViewById(R.id.backImageView);
        btnNext = (Button) mView.findViewById(R.id.btn_next);
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
                startYear = year;
                startMonth = month+1;
                startDay = dayOfMonth;
                Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarEndFragment fragment = new CalendarEndFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("startYear",startYear);
                bundle.putInt("startMonth",startMonth);
                bundle.putInt("startDay",startDay);
                fragment.setArguments(bundle);
                fragment.show(getFragmentManager(),"CalendarEndFragment");
            }
        });
        return mView;
    }

}
