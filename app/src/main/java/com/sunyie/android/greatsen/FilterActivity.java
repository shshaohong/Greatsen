package com.sunyie.android.greatsen;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunyie.android.greatsen.entity.GetDataEntity;
import com.sunyie.android.greatsen.entity.SearchEntity;
import com.sunyie.android.greatsen.fragment.BatchFragment;
import com.sunyie.android.greatsen.fragment.CalendarStartFragment;
import com.sunyie.android.greatsen.fragment.LayerFragment;
import com.sunyie.android.greatsen.fragment.SearchFragment;
import com.sunyie.android.greatsen.network.GreatsenApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterActivity extends FragmentActivity implements BatchFragment.onTestListener,
        LayerFragment.onListener, SearchFragment.onSearchListener {

    public static final String DATA_RESULT = "FilterActivity";
    @InjectView(R.id.filter_title_date)
    TextView mFilterTitleDate;
    @InjectView(R.id.tv_start_date)
    TextView mTvStartDate;
    @InjectView(R.id.tv_end_date)
    TextView mTvEndDate;
    @InjectView(R.id.tv_material_number)
    TextView mTvMaterialNumber;
    @InjectView(R.id.tv_layer)
    TextView mTvLayer;
    @InjectView(R.id.tv_batch)
    TextView mTvBatch;
    @InjectView(R.id.tv_start_date)
    TextView startDate;
    @InjectView(R.id.tv_end_date)
    TextView endDate;
    int mYear, mMonth, mDay, mYear2, mMonth2, mDay2;
    private DatePickerDialog mMDatePickerDialog2;
    private SharedPreferences.Editor mEditor;
    private List<String> mLevel;
    private List<String> mBatch;
    private String startDateM, endDateM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.inject(this);

        final Calendar ca = Calendar.getInstance();
        SharedPreferences sf = getSharedPreferences("startdate", Context.MODE_PRIVATE);
        mYear = sf.getInt("startyear", ca.get(Calendar.YEAR));
        mMonth = sf.getInt("startmonth",ca.get(Calendar.MONTH));
        mDay = sf.getInt("startday",ca.get(Calendar.DAY_OF_MONTH));

        SharedPreferences ff = getSharedPreferences("enddate", Context.MODE_PRIVATE);
        mYear2 = ff.getInt("endyear", ca.get(Calendar.YEAR));
        mMonth2 = ff.getInt("endmonth",ca.get(Calendar.MONTH));
        mDay2 = ff.getInt("endday",ca.get(Calendar.DAY_OF_MONTH));
        //设置日期标题
        setDateTitle();
        SharedPreferences ss = getSharedPreferences("FilterActivity", Context.MODE_PRIVATE);
        startDateM = ss.getString("startDateM", null);
        endDateM = ss.getString("endDateM", null);
    }

    private void setDateTitle() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String format = sdf.format(date);
        Log.e("date", format);
        SharedPreferences sf = getSharedPreferences("FilterActivity", Context.MODE_PRIVATE);
        String dateTitle = sf.getString("dateTitle", format);
        String batch = sf.getString("batch", "");
        String layer = sf.getString("layer", "");
        String search = sf.getString("search", "");
        String startDate = sf.getString("startDate", format);
        String endDate = sf.getString("endDate", format);

        mTvEndDate.setText(endDate);
        mTvStartDate.setText(startDate);
        mTvLayer.setText(layer);
        mTvBatch.setText(batch);
        mFilterTitleDate.setText(dateTitle);
        mTvMaterialNumber.setText(search);
        setNumber();
    }

    private void setNumber() {
        SharedPreferences str = getSharedPreferences("number", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = str.edit();
        editor.remove("strNumber");
        String number = mTvMaterialNumber.getText().toString();
        editor.putString("strNumber", number);
        Log.e("number", number);
        editor.commit();
    }

    @OnClick({R.id.tv_start_date, R.id.tv_end_date, R.id.ll_search,
            R.id.ll_layer, R.id.ll_batch, R.id.backImageView,
            R.id.btn_confirm
    })
    public void filterClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start_date:
                //开始
                setStartDate();
//                startDataFragment();
                break;
            case R.id.tv_end_date:
                //结束
                setStartDate();
                break;
            case R.id.ll_search:
                //料号
                if (mYear > mYear2) {
                    Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mYear == mYear2 && mMonth > mMonth2) {
                    Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mYear == mYear2 && mMonth == mMonth2 && mDay > mDay2) {
                    Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                DialogFragment dialogFragment = new SearchFragment();
                dialogFragment.show(getSupportFragmentManager(), "SearchFragment");
                break;
            case R.id.ll_layer:
                //层别
                if (mYear > mYear2) {
                    Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mYear == mYear2 && mMonth > mMonth2) {
                    Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mYear == mYear2 && mMonth == mMonth2 && mDay > mDay2) {
                    Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                new LayerFragment().show(getSupportFragmentManager(), "LayerFragment");
                break;
            case R.id.ll_batch:
                //批次
                if (mYear > mYear2) {
                    Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mYear == mYear2 && mMonth > mMonth2) {
                    Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mYear == mYear2 && mMonth == mMonth2 && mDay > mDay2) {
                    Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                new BatchFragment().show(getSupportFragmentManager(), "BatchFragment");
                break;
            case R.id.btn_confirm:
                //确认
                setBtnConfirm();
                break;
            case R.id.backImageView:
                finish();
                break;
        }
    }

    private void startDataFragment() {
        CalendarStartFragment activity = new CalendarStartFragment();
        activity.show(getSupportFragmentManager(), "MyCalendarActivity");
    }

    private void setStartDate() {
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(FilterActivity.this,
                AlertDialog.BUTTON_NEGATIVE, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mYear = i;
                mMonth = i1;
                mDay = i2;
                setEndDate();
                displayStart();
                SharedPreferences sf = getSharedPreferences("startdate", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.putInt("startyear", mYear);
                editor.putInt("startmonth", mMonth);
                editor.putInt("startday", mDay);
                editor.commit();

            }
        }, mYear, mMonth, mDay);
        mDatePickerDialog.setTitle("开始日期");
        mDatePickerDialog.show();

    }

    private void setEndDate() {
        mMDatePickerDialog2 = new DatePickerDialog(FilterActivity.this,
                AlertDialog.BUTTON_NEGATIVE, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mYear2 = i;
                mMonth2 = i1;
                mDay2 = i2;
                displayEnd();
                setDateOrDate();
                setFilterTitleDate();
                SharedPreferences sf = getSharedPreferences("enddate", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.putInt("endyear", mYear2);
                editor.putInt("endmonth", mMonth2);
                editor.putInt("endday", mDay2);
                editor.commit();
            }
        }, mYear2, mMonth2, mDay2);
        mMDatePickerDialog2.setTitle("结束日期");
        mMDatePickerDialog2.show();
    }

    /**
     * 设置日期
     */
    //设置开始日期
    public void displayStart() {
        startDate.setText(new StringBuffer().append(mYear)
                .append(" 年 ")
                .append(mMonth + 1)
                .append(" 月 ")
                .append(mDay)
                .append(" 日")
        );
        startDateM = mYear + "-" + (mMonth+1) + "-" + mDay;
    }

    //设置结束日期
    public void displayEnd() {
        endDate.setText(new StringBuffer().append(mYear2)
                .append(" 年 ")
                .append(mMonth2 + 1)
                .append(" 月 ")
                .append(mDay2)
                .append(" 日")
        );
        endDateM = mYear2 + "-" + (mMonth2+1) + "-" + mDay2;
    }

    //判断是否符合开始时间不能大于开始时间
    private void setDateOrDate() {
        if (mYear > mYear2) {
            Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mYear == mYear2 && mMonth > mMonth2) {
            Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mYear == mYear2 && mMonth == mMonth2 && mDay > mDay2) {
            Toast.makeText(FilterActivity.this, "开始时间不能大于开始时间", Toast.LENGTH_SHORT).show();
            return;
        }
        //发送网络请求数据
        String startDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
        String endDate = mYear2 + "-" + (mMonth2 + 1) + "-" + mDay2;

        GreatsenApi.Api api = GreatsenApi.createApi();
        api.getSearch(startDate, endDate).enqueue(new Callback<GetDataEntity>() {

            @Override
            public void onResponse(Call<GetDataEntity> call, Response<GetDataEntity> response) {
                GetDataEntity body = response.body();
                List<SearchEntity> data = body.getData();
                if (body.getStatus().equals("1")) {
                    SharedPreferences sf = getSharedPreferences("search", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sf.edit();
                    SharedPreferences spf = getSharedPreferences("level", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorLevel = spf.edit();
                    SharedPreferences sfBatch = getSharedPreferences("batch", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorBatch = sfBatch.edit();

                    editor.putInt("search_size", data.size());

                    for (int i = 0; i < data.size(); i++) {
                        editor.remove("search_" + i);
                        editor.putString("search_" + i, data.get(i).getSearch());

                        mLevel = data.get(i).getLevel();
                        mBatch = data.get(i).getBatch();
                        editorLevel.remove("level_size" + i);
                        editorBatch.remove("batch_size" + i);
                        editorLevel.putInt("level_size" + i, mLevel.size());
                        editorBatch.putInt("batch_size" + i, mBatch.size());
                        for (int j = 0; j < mLevel.size(); j++) {
                            editorLevel.remove("level_" + i + j);
                            editorLevel.putString("level_" + i + j, mLevel.get(j));
                            Log.e("level", mLevel.get(j));
                        }
                        for (int j = 0; j < mBatch.size(); j++) {
                            editorBatch.remove("batch_" + i + j);
                            editorBatch.putString("batch_" + i + j, mBatch.get(j));
                            Log.e("batch", mBatch.get(j));
                        }
                    }
                    editor.commit();
                    editorLevel.commit();
                    editorBatch.commit();
                } else {
                    Toast.makeText(FilterActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetDataEntity> call, Throwable t) {
                Toast.makeText(FilterActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //设置title的年月日
    private void setFilterTitleDate() {
        if (mYear == mYear2) {
            if (mMonth == mMonth2) {
                mFilterTitleDate.setText(mYear + "年" + (mMonth + 1) + "月" + mDay + "日"
                        + "~" + mDay2 + "日"
                );
                if (mDay == mDay2) {
                    mFilterTitleDate.setText(mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                } else {
                    mFilterTitleDate.setText(mYear + "年" + (mMonth + 1) + "月" + mDay + "日" + "~" + mDay2 + "日");
                }
            } else {
                mFilterTitleDate.setText(mYear + "年" + (mMonth + 1) + "月" + mDay + "日"
                        + "~" + (mMonth2 + 1) + "月" + mDay2 + "日"
                );
            }

        } else {
            mFilterTitleDate.setText(mYear + "年" + (mMonth + 1) + "月" + mDay + "日"
                    + "~" + mYear2 + "年" + (mMonth2 + 1) + "月" + mDay2 + "日"
            );
        }
    }

    //点击确认的条件
    private void setBtnConfirm() {
        if (mYear > mYear2) {
            Toast.makeText(FilterActivity.this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mYear == mYear2 && mMonth > mMonth2) {
            Toast.makeText(FilterActivity.this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mYear == mYear2 && mMonth == mMonth2 && mDay > mDay2) {
            Toast.makeText(FilterActivity.this, "开始时间不能大于结束时间", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateTitle = mFilterTitleDate.getText().toString();
        String batch = mTvBatch.getText().toString();
        String layer = mTvLayer.getText().toString();
        String search = mTvMaterialNumber.getText().toString();
        String startDate = mTvStartDate.getText().toString();
        String endDate = mTvEndDate.getText().toString();
        //保存数据
        SharedPreferences sf = getSharedPreferences("FilterActivity", Context.MODE_PRIVATE);
        mEditor = sf.edit();
        mEditor.putString("dateTitle", dateTitle);
        mEditor.putString("batch", batch);
        mEditor.putString("layer", layer);
        mEditor.putString("search", search);
        mEditor.putString("startDate", startDate);
        mEditor.putString("endDate", endDate);
        mEditor.putString("startDateM", startDateM);
        mEditor.putString("endDateM", endDateM);
        mEditor.commit();

        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent(MainActivity.TYPE_SEARCH)
                        .putExtra("dateTitle", dateTitle)
                        .putExtra("batch", batch)
                        .putExtra("layer", layer)
                        .putExtra("search", search)
        );
        finish();
    }

    @Override
    public void onTest(String str) {
        mTvBatch.setText(str);
    }

    @Override
    public void onLayerTest(String str) {
        mTvLayer.setText(str);
    }

    @Override
    public void onSearchTest(String str) {
        mTvMaterialNumber.setText(str);
        setNumber();
    }

    static class ViewHolder {
        @InjectView(R.id.iv_layer_xiala)
        ImageView mIvLayerXiala;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
