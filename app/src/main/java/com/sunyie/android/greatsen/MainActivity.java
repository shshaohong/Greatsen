package com.sunyie.android.greatsen;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sunyie.android.greatsen.adapter.MenuAdapter;
import com.sunyie.android.greatsen.adapter.MenuLeftAdapter;
import com.sunyie.android.greatsen.entity.AllDateEntity;
import com.sunyie.android.greatsen.entity.GetAllDataEntity;
import com.sunyie.android.greatsen.entity.StatisticalDetails;
import com.sunyie.android.greatsen.network.GreatsenApi;
import com.sunyie.android.greatsen.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TYPE_SEARCH = "MainActivity";
    @InjectView(R.id.pieChartView)
    PieChartView mPieChartView;
    @InjectView(R.id.clccv_main)
    ComboLineColumnChartView mComboView;
    @InjectView(R.id.select)
    ImageView select;
    @InjectView(R.id.oneDay)
    TextView oneDay;
    @InjectView(R.id.threeDay)
    TextView threeDay;
    @InjectView(R.id.week)
    TextView week;
    @InjectView(R.id.month)
    TextView month;
    @InjectView(R.id.quarter)
    TextView quarter;
    @InjectView(R.id.filter)
    ImageView filter;
    @InjectView(R.id.tv_dateTitle)
    TextView mTvDateTitle;
    @InjectView(R.id.tv_qualified)
    TextView mTvQualified;
    @InjectView(R.id.tv_noQualified)
    TextView mTvNoQualified;
    @InjectView(R.id.tv_search_main)
    TextView mTvSearchMain;
    @InjectView(R.id.tv_layer_main)
    TextView mTvLayerMain;
    @InjectView(R.id.tv_batch_main)
    TextView mTvBatchMain;
    private PopupWindow popupWindow;
    private PopupWindow PopupWindow2;
    private float f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17,
            other;
    private int[] mColors = {R.color.colorKailu, R.color.colorDuanlu, R.color.colorQuekou, R
            .color.colorZhenkong, R.color.colorTongzha,
            R.color.colorTuqi, R.color.colorAoxian, R.color.colorXianxi, R.color.colorXiancu, R
            .color.colorXianju,
            R.color.colorQita};

    /*========= 控件相关 =========*/
//    private PieChartView mPieChartView;                 //饼状图控件

    /*========= 状态相关 =========*/
    private boolean isExploded = false;                 //每块之间是否分离
    private boolean isHasLabelsInside = false;          //标签在内部
    private boolean isHasLabelsOutside = false;         //标签在外部
    private boolean isHasCenterCircle = true;          //空心圆环
    private boolean isPiesHasSelected = false;          //块选中标签样式
    private boolean isHasCenterSingleText = false;      //圆环中心单行文字
    private boolean isHasCenterDoubleText = false;      //圆环中心双行文字

    /*========= 数据相关 =========*/
    private PieChartData mPieChartData;                 //饼状图数据               //饼状图数据


    /*========== 状态相关 ==========*/
    private boolean isHasAxes = true;                   //是否有坐标轴
    private boolean isHasAxesName = false;               //坐标轴是否有名称
    private boolean isHasPoints = true;                 //是否有节点
    private boolean isHasLines = true;                  //是否有线
    private boolean isCubic = false;                    //是否是曲线
    private boolean isHasLabels = true;                //是否有标签

    /*========== 数据相关 ==========*/
    private ComboLineColumnChartData mComboData;        //联合图表数据
    private int numberOfLines = 1;                      //初始线条数
    private int maxNumberOfLines = 11;                   //最大线数
    private int numberOfPoints = 11;                    //最大点数
    //线与点的数据数组
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
    private boolean pieVisible = true;
    private int mYear;
    private int mMonth;
    private int mDay;
    private Calendar mCalendar;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String dateTitle = intent.getStringExtra("dateTitle");
            String batch = intent.getStringExtra("batch");
            String layer = intent.getStringExtra("layer");
            String search = intent.getStringExtra("search");
            SharedPreferences sf = getSharedPreferences("FilterActivity", Context.MODE_PRIVATE);
            String startDateM = sf.getString("startDateM", null);
            String endDateM = sf.getString("endDateM", null);
            Log.e("broad", startDateM + endDateM + search + layer + batch);
            mTvBatchMain.setText(batch);
            mTvDateTitle.setText(dateTitle);
            mTvLayerMain.setText(layer);
            mTvSearchMain.setText(search);
            oneDay.setSelected(false);
            threeDay.setSelected(false);
            month.setSelected(false);
            week.setSelected(false);
            quarter.setSelected(false);
            final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("正在获取数据...");
            dialog.show();
            GreatsenApi.Api api = GreatsenApi.createApi();
            api.getAllData(startDateM, endDateM, search, layer, batch, 10).enqueue(
                    new Callback<GetAllDataEntity>() {
                        @Override
                        public void onResponse(Call<GetAllDataEntity> call, Response<GetAllDataEntity> response) {
                            dialog.cancel();
                            if (response.body().getStatus().equals("1")) {
                                AllDateEntity allData = response.body().getData();
                                String pass = allData.getPass();
                                String reject = allData.getReject();
                                mTvQualified.setText(pass);
                                mTvNoQualified.setText(reject);
                                StatisticalDetails details = allData.getRes();
                                Log.e("aaa", details.toString());
                                f0 = details.getF0();
                                f1 = details.getF1();
                                f2 = details.getF2();
                                f3 = details.getF3();
                                f4 = details.getF4();
                                f5 = details.getF5();
                                f6 = details.getF6();
                                f7 = details.getF7();
                                f8 = details.getF8();
                                f9 = details.getF9();
                                f10 = details.getF10();
                                f11 = details.getF11();
                                f12 = details.getF12();
                                f13 = details.getF13();
                                f14 = details.getF14();
                                f15 = details.getF15();
                                f16 = details.getF16();
                                f17 = details.getF17();
                                other = f0 + f11 + f12 + f13 + f14 + f15 + f16;
                                setPieDatas();
                                setPointsDatas();
                                setComboDatas();
                            } else {
                                Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GetAllDataEntity> call, Throwable t) {
                            dialog.cancel();
                            Toast.makeText(MainActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    };
    private long mTime = 0;
    private String mStartDate;
    private String mEndDate;
    private String mQualified;
    private String mNoQualified;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        IntentFilter filter = new IntentFilter(TYPE_SEARCH);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);


        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        //设置时间段
        mStartDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
        mEndDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
        mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
        init();
        getDayData(mStartDate, mEndDate, 1);
        //设置时间段内显示所有
        setPercentOfPass();
        showOrHideLabelsOutside();
    }

    private void setPercentOfPass() {
        mTvBatchMain.setText("所有");
        mTvLayerMain.setText("所有");
        mTvSearchMain.setText("所有");
    }

    private void init() {
        oneDay.setOnClickListener(this);
        threeDay.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);
        quarter.setOnClickListener(this);
        oneDay.setSelected(true);
//        mPieChartView.setOnValueTouchListener(new ValueTouchListener());
    }

    private void getDayData(String startDate, String endDate, int isDate) {
        //获取某段时间的所有数据
        f0 = f1 = f2 = f3 = f4 = f5 = f6 = f7 = f8 = f9 = f10 = f11
                = f12 = f13 = f14 = f15 = f16 = f17 = 0;
        if (isDate == 1) {
            SharedPreferences sf = getSharedPreferences("oneDate", Context.MODE_PRIVATE);
            int date = sf.getInt("oneDay", 0);
            mQualified = sf.getString("qualified", "");
            mNoQualified = sf.getString("noQualified", "");
            Log.e("date1", date + "");
            if (CommonUtils.isNetConnect(this)) {
                getData(startDate, endDate, 1);
            } else {
                getDataM(sf);
                mTvQualified.setText(mQualified);
                mTvNoQualified.setText(mNoQualified);
                setPieDatas();
                setPointsDatas();
                setComboDatas();
            }
        }
        if (isDate == 2) {
            SharedPreferences sf = getSharedPreferences("threeDate", Context.MODE_PRIVATE);
            int date = sf.getInt("threeDay", 0);
            mQualified = sf.getString("qualified", "");
            mNoQualified = sf.getString("noQualified", "");
            Log.e("date2", date + "");
            if (CommonUtils.isNetConnect(this)) {
                getData(startDate, endDate, 2);
            } else {
                getDataM(sf);
                mTvQualified.setText(mQualified);
                mTvNoQualified.setText(mNoQualified);
                setPieDatas();
                setPointsDatas();
                setComboDatas();
                Log.e("isff", f0 + f1 + f2 + f3 + f4 + f5 + f6 + f7 + f12 + f13 + "");
            }
        }
        if (isDate == 3) {
            SharedPreferences sf = getSharedPreferences("weekDate", Context.MODE_PRIVATE);
            int date = sf.getInt("weekDay", 0);
            mQualified = sf.getString("qualified", "");
            mNoQualified = sf.getString("noQualified", "");
            Log.e("date3", date + "");
            if (CommonUtils.isNetConnect(this)) {
                getData(startDate, endDate, 3);
            } else {
                getDataM(sf);
                mTvQualified.setText(mQualified);
                mTvNoQualified.setText(mNoQualified);
                setPieDatas();
                setPointsDatas();
                setComboDatas();
                Log.e("isff", f0 + f1 + f2 + f3 + f4 + f5 + f6 + f7 + f12 + f13 + "");
            }
        }
        if (isDate == 4) {
            SharedPreferences sf = getSharedPreferences("monthDate", Context.MODE_PRIVATE);
            int date = sf.getInt("monthDay", 0);
            mQualified = sf.getString("qualified", "");
            mNoQualified = sf.getString("noQualified", "");
            Log.e("date3", date + "");
            if (CommonUtils.isNetConnect(this)) {
                getData(startDate, endDate, 4);
            } else {
                getDataM(sf);
                mTvQualified.setText(mQualified);
                mTvNoQualified.setText(mNoQualified);
                setPieDatas();
                setPointsDatas();
                setComboDatas();
                Log.e("isff", f0 + f1 + f2 + f3 + f4 + f5 + f6 + f7 + f12 + f13 + "");
            }
        }
        if (isDate == 5) {
            SharedPreferences sf = getSharedPreferences("quarterDate", Context.MODE_PRIVATE);
            int date = sf.getInt("quarterDay", 0);
            mQualified = sf.getString("qualified", "");
            mNoQualified = sf.getString("noQualified", "");
            Log.e("date3", date + "");
            if (CommonUtils.isNetConnect(this)) {
                getData(startDate, endDate, 5);
            } else {
                getDataM(sf);
                mTvQualified.setText(mQualified);
                mTvNoQualified.setText(mNoQualified);
                setPieDatas();
                setPointsDatas();
                setComboDatas();
            }
        }

    }

    private void getData(String startDate, String endDate, final int isDay) {
//        f0 = f1 = f2 = f3 = f4 = f5 = f6 = f7 = f8 = f9 = f10 = f11 = f12 = f13 = f14 = f15 = f16 = f17 = 0;
        GreatsenApi.Api api = GreatsenApi.createApi();
        api.getAllData(startDate, endDate, "all", "all", "all", 10)
                .enqueue(new Callback<GetAllDataEntity>() {
                    @Override
                    public void onResponse(Call<GetAllDataEntity> call,
                                           final Response<GetAllDataEntity> response) {
                        GetAllDataEntity body = response.body();
                        if (body.getStatus().equals("1")) {
                            //设置数据
                            AllDateEntity allData = body.getData();
                            String pass = allData.getPass();
                            String reject = allData.getReject();
                            mTvQualified.setText(pass);
                            mTvNoQualified.setText(reject);
                            StatisticalDetails details = allData.getRes();
                            Log.e("aaa", details.toString());
                            f0 = details.getF0();
                            f1 = details.getF1();
                            f2 = details.getF2();
                            f3 = details.getF3();
                            f4 = details.getF4();
                            f5 = details.getF5();
                            f6 = details.getF6();
                            f7 = details.getF7();
                            f8 = details.getF8();
                            f9 = details.getF9();
                            f10 = details.getF10();
                            f11 = details.getF11();
                            f12 = details.getF12();
                            f13 = details.getF13();
                            f14 = details.getF14();
                            f15 = details.getF15();
                            f16 = details.getF16();
                            f17 = details.getF17();
                            other = f0 + f11 + f12 + f13 + f14 + f15 + f16;
                            setPieDatas();
                            setPointsDatas();
                            setComboDatas();
                            if (isDay == 1) {
                                SharedPreferences sfDate = getSharedPreferences("oneDate", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editorsfDate = sfDate.edit();
                                editorsfDate.putInt("oneDay", mDay);
                                editorsfDate.putString("qualified", pass);
                                editorsfDate.putString("noQualified", reject);
                                putData(editorsfDate);
                                editorsfDate.commit();
                            }
                            if (isDay == 2) {
                                SharedPreferences sfDate = getSharedPreferences("threeDate", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editorsfDate = sfDate.edit();
                                editorsfDate.putInt("threeDay", mDay);
                                editorsfDate.putString("qualified", pass);
                                editorsfDate.putString("noQualified", reject);
                                putData(editorsfDate);
                                editorsfDate.commit();
                            }
                            if (isDay == 3) {
                                SharedPreferences sfDate = getSharedPreferences("weekDate", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editorsfDate = sfDate.edit();
                                editorsfDate.putInt("weekDay", mDay);
                                editorsfDate.putString("qualified", pass);
                                editorsfDate.putString("noQualified", reject);
                                putData(editorsfDate);
                                editorsfDate.commit();
                            }
                            if (isDay == 4) {
                                SharedPreferences sfDate = getSharedPreferences("monthDate", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editorsfDate = sfDate.edit();
                                editorsfDate.putInt("monthDay", mDay);
                                editorsfDate.putString("qualified", pass);
                                editorsfDate.putString("noQualified", reject);
                                putData(editorsfDate);
                                editorsfDate.commit();
                            }
                            if (isDay == 5) {
                                SharedPreferences sfDate = getSharedPreferences("quarterDate", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editorsfDate = sfDate.edit();
                                editorsfDate.putInt("quarterDay", mDay);
                                editorsfDate.putString("qualified", pass);
                                editorsfDate.putString("noQualified", reject);
                                putData(editorsfDate);
                                editorsfDate.commit();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "获取数据失败", Toast
                                    .LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetAllDataEntity> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void putData(SharedPreferences.Editor editor) {
        editor.putFloat("f0", f0);
        editor.putFloat("f1", f1);
        editor.putFloat("f2", f2);
        editor.putFloat("f3", f3);
        editor.putFloat("f4", f4);
        editor.putFloat("f5", f5);
        editor.putFloat("f6", f6);
        editor.putFloat("f7", f7);
        editor.putFloat("f8", f8);
        editor.putFloat("f9", f9);
        editor.putFloat("f10", f10);
        editor.putFloat("f11", f11);
        editor.putFloat("f12", f12);
        editor.putFloat("f13", f13);
        editor.putFloat("f14", f14);
        editor.putFloat("f15", f15);
        editor.putFloat("f16", f16);
    }

    private void getDataM(SharedPreferences sf) {
        f0 = sf.getFloat("f0", 0);
        f1 = sf.getFloat("f1", 0);
        f2 = sf.getFloat("f2", 0);
        f3 = sf.getFloat("f3", 0);
        f4 = sf.getFloat("f4", 0);
        f5 = sf.getFloat("f5", 0);
        f6 = sf.getFloat("f6", 0);
        f7 = sf.getFloat("f7", 0);
        f8 = sf.getFloat("f8", 0);
        f9 = sf.getFloat("f9", 0);
        f10 = sf.getFloat("f10", 0);
        f11 = sf.getFloat("f11", 0);
        f12 = sf.getFloat("f12", 0);
        f13 = sf.getFloat("f13", 0);
        f14 = sf.getFloat("f14", 0);
        f15 = sf.getFloat("f15", 0);
        f16 = sf.getFloat("f16", 0);
        other = f0 + f11 + f12 + f13 + f14 + f15 + f16;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    /**
     * 设置饼图相关数据
     */

    private void setPieDatas() {
        int numValues = 11;                //把一张饼切成6块
        /*===== 随机设置每块的颜色和数据 =====*/
        List<SliceValue> values = new ArrayList<>();
        Log.e("a1", f0 + "");

        if (f1 != 0.0) {
            values.add(new SliceValue(f1, getResources().getColor(mColors[0])).setLabel(f1 + "% " +
                    "开路"));
        }
        if (f2 != 0.0) {
            values.add(new SliceValue(f2, getResources().getColor(mColors[1])).setLabel(f2 + "% " +
                    "短路"));
        }
        if (f3 != 0.0) {
            values.add(new SliceValue(f3, getResources().getColor(mColors[2])).setLabel(f3 + "% " +
                    "缺口"));
        }
        if (f4 != 0.0) {
            values.add(new SliceValue(f4, getResources().getColor(mColors[3])).setLabel(f4 + "% " +
                    "针孔"));
        }
        if (f5 != 0.0) {
            values.add(new SliceValue(f5, getResources().getColor(mColors[4])).setLabel(f5 + "% " +
                    "铜渣"));
        }
        if (f6 != 0.0) {
            values.add(new SliceValue(f6, getResources().getColor(mColors[5])).setLabel(f6 + "% " +
                    "凸起"));
        }
        if (f7 != 0.0) {
            values.add(new SliceValue(f7, getResources().getColor(mColors[6])).setLabel(f7 + "% " +
                    "凹陷"));
        }
        if (f8 != 0.0) {
            values.add(new SliceValue(f8, getResources().getColor(mColors[7])).setLabel(f8 + "% " +
                    "线细"));
        }
        if (f9 != 0.0) {
            values.add(new SliceValue(f9, getResources().getColor(mColors[8])).setLabel(f9 + "% " +
                    "线粗"));
        }
        if (f10 != 0.0) {
            values.add(new SliceValue(f10, getResources().getColor(mColors[9])).setLabel(f10 + "%" +
                    " 线距"));
        }
        if (other != 0.0) {
            values.add(new SliceValue(other, getResources().getColor(mColors[10])).setLabel(other
                    + "% 其他"));
        }


        /*===== 设置相关属性 类似Line Chart =====*/
        for (int i = 0; i < values.size(); i++) {
            Log.e("aaa", values.get(i) + "数值");
        }
        mPieChartData = new PieChartData(values);
        mPieChartData.setHasLabels(isHasLabelsInside);
        mPieChartData.setHasLabelsOnlyForSelected(isPiesHasSelected);
        mPieChartData.setHasLabelsOutside(isHasLabelsOutside);
        mPieChartData.setHasCenterCircle(isHasCenterCircle);
        mPieChartData.setCenterCircleScale(0.4f);
        mPieChartData.setValueLabelBackgroundEnabled(true);
        //是否分离
        if (isExploded) {
            mPieChartData.setSlicesSpacing(0);                 //分离间距为18
        }

        //是否显示单行文本
        if (isHasCenterSingleText) {
            mPieChartData.setCenterText1("Hello");             //文本内容
        }

        //是否显示双行文本
        if (isHasCenterDoubleText) {
            mPieChartData.setCenterText2("World");             //文本内容
 }
        mPieChartView.setPieChartData(mPieChartData);         //设置控件
    }

    /**
     * 在外部显示标签
     */
    private void showOrHideLabelsOutside() {
        isHasLabelsOutside = !isHasLabelsOutside;       //取反即可
        if (isHasLabelsOutside) {
            isHasLabelsInside = true;                   //如果外面不显示 就在内部显示
            isPiesHasSelected = false;                  //点击不显示标签
            //设置点击不显示标签
            mPieChartView.setValueSelectionEnabled(isPiesHasSelected);
        }
        //已经在外部的话 适当变化形状
        if (isHasLabelsOutside) {
            mPieChartView.setCircleFillRatio(0.7f);
        } else {
            mPieChartView.setCircleFillRatio(1.0f);
        }
        setPieDatas();                                  //重新设置
    }


    /**
     * 随机生成每条线上对应的点的数据
     */
    private void setPointsDatas() {
        Log.e("每条线上对应的点的数据", f0 + f1 + f2 + f3 + f4 + f5 + f6 + f7 + f12 + f13 + "");
        float[] floats1 = new float[]{f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, other};
        float[] floats = new float[]{f1, f1 + f2, f1 + f2 + f3,
                f1 + f2 + f3 + f4, f1
                + f2 + f3 + f4 + f5,
                f1 + f2 + f3 + f4 + f5 + f6, f1 + f2 + f3 + f4 +
                f5 + f6 + f7, f1 +
                f2 + f3 + f4 + f5 + f6 + f7 + f8,
                f1 + f2 + f3 + f4 + f5 + f6 + f7 + f8 + f9, f1 +
                f2 + f3 + f4 + f5 +
                f6 + f7 + f8 + f9 + f10,
                f1 + f2 + f3 + f4 + f5 + f6 + f7 + f8 + f9 + f10 + other};
        for (int i = 0; i < maxNumberOfLines; i++) {
//            if (floats1[i] != 0) {
            for (int j = 0; j < numberOfPoints; j++) {
                randomNumbersTab[i][j] = floats[j];
            }
//            }
        }
    }

    /**
     * 设置结合起来的数据
     */
    private void setComboDatas() {
        //需要同时设置柱状数据和线性数据
        mComboData = new ComboLineColumnChartData(setColumnData(), setLineData());
        //坐标轴的相关设置 类似 Line Chart
//        float[] floats1 = new float[]{f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, other};
        if (isHasAxes) {
            List<AxisValue> axisValueList = new ArrayList<>();
            AxisValue axisValue1 = new AxisValue(0f);
            axisValue1.setLabel("开路");
            axisValueList.add(axisValue1);
            AxisValue axisValue2 = new AxisValue(1.0f);
            axisValue2.setLabel("短路");
            axisValueList.add(axisValue2);
            AxisValue axisValue3 = new AxisValue(2.0f);
            axisValue3.setLabel("缺口");
            axisValueList.add(axisValue3);
            AxisValue axisValue4 = new AxisValue(3.0f);
            axisValue4.setLabel("针孔");
            axisValueList.add(axisValue4);
            AxisValue axisValue5 = new AxisValue(4.0f);
            axisValue5.setLabel("铜渣");
            axisValueList.add(axisValue5);
            AxisValue axisValue6 = new AxisValue(5.0f);
            axisValue6.setLabel("凸起");
            axisValueList.add(axisValue6);
            AxisValue axisValue7 = new AxisValue(6.0f);
            axisValue7.setLabel("凹陷");
            axisValueList.add(axisValue7);
            AxisValue axisValue8 = new AxisValue(7.0f);
            axisValue8.setLabel("线细");
            axisValueList.add(axisValue8);
            AxisValue axisValue9 = new AxisValue(8.0f);
            axisValue9.setLabel("线粗");
            axisValueList.add(axisValue9);
            AxisValue axisValue10 = new AxisValue(9.0f);
            axisValue10.setLabel("线距");
            axisValueList.add(axisValue10);
            AxisValue axisValue11 = new AxisValue(10.0f);
            axisValue11.setLabel("其他");
            axisValueList.add(axisValue11);


            Axis axisX = new Axis().setAutoGenerated(false)
                    .setValues(axisValueList);
            Axis axisY = new Axis().setHasLines(false);
            if (isHasAxesName) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            mComboData.setAxisXBottom(axisX);
//            mComboData.setAxisYLeft(axisY);
            mComboData.setAxisYRight(axisY);
        } else {
            mComboData.setAxisXBottom(null);
            mComboData.setAxisYLeft(null);
        }
        mComboData.setValueLabelBackgroundEnabled(false);
        //设置数据
        mComboView.setComboLineColumnChartData(mComboData);
        mComboView.setZoomType(ZoomType.HORIZONTAL);
        mComboView.setZoomLevel(0, 0, 1.5f);
        mComboView.setZoomEnabled(true);
    }

    /**
     * 设置线性图数据 类似Line Chart
     *
     * @return 线性图数据
     */
    private LineChartData setLineData() {
        List<Line> lines = new ArrayList<>();
        float[] floats = new float[]{f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, other};
        for (int i = 0; i < numberOfLines; ++i) {
//            if (floats[i] != 0) {
            int num = 0;
            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < numberOfPoints; ++j) {
                if (randomNumbersTab[i][j] >= 100) {
                    num++;
                }
                if (num < 2 && randomNumbersTab[i][j] != 0) {
                    if (j == 0) {
                        values.add(new PointValue(j, randomNumbersTab[i][j]));
                    } else {
                        if (randomNumbersTab[i][j] != 0 && randomNumbersTab[i][j] != randomNumbersTab[i][j - 1]) {
                            values.add(new PointValue(j, randomNumbersTab[i][j]));
                        }
                    }

                }

            }
            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setCubic(isCubic);
            line.setHasLabels(isHasLabels);
            line.setHasLines(isHasLines);
            line.setHasPoints(isHasPoints);
            line.setStrokeWidth(Utils.dip2px(this, 0.5f));
            line.setColor(Color.WHITE);
            line.setPointRadius(Utils.dip2px(this, 1f));
            lines.add(line);
//            }

        }
        return new LineChartData(lines);
    }

    /**
     * 设置柱状图数据 类似 Column Chart
     *
     * @return 柱状图数据
     */
    private ColumnChartData setColumnData() {
        int numSubcolumns = 1;
        int numColumns = 11;

        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        float[] floats = new float[]{f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, other};
        for (int i = 0; i < numColumns; i++) {
            values = new ArrayList<>();
            for (int j = 0; j < numSubcolumns; j++) {
                values.add(new SubcolumnValue(floats[i], getResources().getColor(mColors[i])));
            }
            columns.add(new Column(values));
        }
        return new ColumnChartData(columns);
    }

    //右边点击菜单
    private void showPopupWindow(int xoff, int yoff) {
        View view = LayoutInflater.from(this).inflate(R.layout.menu, null);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        MenuAdapter adapter = new MenuAdapter(this);
        listView.setAdapter(adapter);
        popupWindow = new PopupWindow(this);
        popupWindow.setHeight(Utils.dip2px(this, 83));
        popupWindow.setWidth(Utils.dip2px(this, 100));
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setContentView(view);
        popupWindow.showAsDropDown(select, xoff, yoff);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mComboView.setVisibility(View.VISIBLE);
                    mPieChartView.setVisibility(View.GONE);
                    pieVisible = false;
                } else {
                    mComboView.setVisibility(View.GONE);
                    mPieChartView.setVisibility(View.VISIBLE);
                    pieVisible = true;
                }
                popupWindow.dismiss();
            }
        });

    }

    @OnClick(R.id.select)
    void setSelect() {
        showPopupWindow(-Utils.dip2px(this, 50), 0);
    }

    //设置三天日期
    private void setThreeDayTitle() {
        //不是闰年
        if (mYear % 4 != 0) {
            if (mMonth == 4 || mMonth == 6 || mMonth == 7
                    || mMonth == 9 || mMonth == 11) {
                if (mDay - 3 == 0) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "31" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "31";
                } else if (mDay - 3 == -1) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "30" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "30";
                } else if (mDay - 3 == -2) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "29";
                } else {
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 3) + "日" +
                            "~" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 3);
                }
            } else if (mMonth == 2) {
                if (mDay - 3 == 0) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "28";
                } else if (mDay - 3 == -1) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "27" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "27";
                } else if (mDay - 3 == -2) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "26" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "26";
                } else {
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 3) + "日" +
                            "~" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 3);
                }
            } else if (mMonth == 0) {
                if (mDay - 3 == 0) {
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "31" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = (mYear - 1) + "-" + "12" + "-" + "31";
                } else if (mDay - 3 == -1) {
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "30" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = (mYear - 1) + "-" + "12" + "-" + "30";
                } else if (mDay - 3 == -2) {
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "29" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = (mYear - 1) + "-" + "12" + "-" + "29";
                } else {
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 3) + "日" +
                            "~" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 3);
                }
            } else {
                if (mDay - 3 == 0) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "30" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + mMonth + "-" + "30";
                } else if (mDay - 3 == -1) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + mMonth + "-" + "29";
                } else if (mDay - 3 == -2) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + mMonth + "-" + "28";
                } else {
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 3) + "日" +
                            "~" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 3);
                }
            }
        } else {
            if (mMonth == 4 || mMonth == 6 || mMonth == 7
                    || mMonth == 9 || mMonth == 11) {
                if (mDay - 3 == 0) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "31" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "31";
                } else if (mDay - 3 == -1) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "30" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "30";
                } else if (mDay - 3 == -2) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "29";
                } else {
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 3) + "日" +
                            "~" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 3);
                }
            } else if (mMonth == 0) {
                if (mDay - 3 == 0) {
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "31" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + "12" + "-" + "31";
                } else if (mDay - 3 == -1) {
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "30" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + "12" + "-" + "30";
                } else if (mDay - 3 == -2) {
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "29" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + "12" + "-" + "29";
                } else {
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 3) + "日" +
                            "~" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 3);
                }
            } else if (mMonth == 2) {
                if (mDay - 3 == 0) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "29";
                } else if (mDay - 3 == -1) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "28";
                } else if (mDay - 3 == -2) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "27" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "27";
                } else {
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 3) + "日" +
                            "~" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 3);
                }
            } else {
                if (mDay - 3 == 0) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "30" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "30";
                } else if (mDay - 3 == -1) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "29";
                } else if (mDay - 3 == -2) {
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth) + "-" + "28";
                } else {
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 3) + "日" +
                            "~" + mDay + "日");
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 3);
                }
            }
        }
    }

    //设置七天日期
    private void setWeekDayTitle() {
        if (mYear % 4 != 0) {
            if (mMonth == 4 || mMonth == 6 || mMonth == 7
                    || mMonth == 9 || mMonth == 11) {
                if (mDay - 7 == 0) {
                    mStartDate = mYear + "-" + mMonth + "-" + "31";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "31" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -1) {
                    mStartDate = mYear + "-" + mMonth + "-" + "30";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "30" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -2) {
                    mStartDate = mYear + "-" + mMonth + "-" + "29";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -3) {
                    mStartDate = mYear + "-" + mMonth + "-" + "28";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -4) {
                    mStartDate = mYear + "-" + mMonth + "-" + "27";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "27" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -5) {
                    mStartDate = mYear + "-" + mMonth + "-" + "26";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "26" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -6) {
                    mStartDate = mYear + "-" + mMonth + "-" + "25";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "25" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else {
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 7);
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 7) + "日" +
                            "~" + mDay + "日");
                }
            } else if (mMonth == 0) {
                if (mDay - 7 == 0) {
                    mStartDate = (mYear - 1) + "-" + "12" + "-" + "31";
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "31" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -1) {
                    mStartDate = (mYear - 1) + "-" + "12" + "-" + "30";
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "30" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -2) {
                    mStartDate = (mYear - 1) + "-" + "12" + "-" + "29";
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "29" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -3) {
                    mStartDate = (mYear - 1) + "-" + "12" + "-" + "28";
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "28" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -4) {
                    mStartDate = (mYear - 1) + "-" + "12" + "-" + "27";
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "27" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -5) {
                    mStartDate = (mYear - 1) + "-" + "12" + "-" + "26";
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "26" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -6) {
                    mStartDate = (mYear - 1) + "-" + "12" + "-" + "25";
                    mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + "25" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                } else {
                    mStartDate = (mYear) + "-" + (mMonth + 1) + "-" + (mDay - 7);
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 7) + "日" +
                            "~" + mDay + "日");
                }
            } else if (mMonth == 2) {
                if (mDay - 7 == 0) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "28";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -1) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "27";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "27" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -2) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "26";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "26" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -3) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "25";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "25" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -4) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "24";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "24" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -5) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "23";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "23" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -6) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "22";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "22" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else {
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 7);
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 7) + "日" +
                            "~" + mDay + "日");
                }
            } else {
                if (mDay - 7 == 0) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "30";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "30" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -1) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "29";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -2) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "28";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else {
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 7);
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 7) + "日" +
                            "~" + mDay + "日");
                }
            }
        } else {
            if (mMonth == 0 || mMonth == 4 || mMonth == 6 || mMonth == 7
                    || mMonth == 9 || mMonth == 11) {
                if (mDay - 7 == 0) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "31";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "31" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -1) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "30";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "30" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -2) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "29";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -3) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "28";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -4) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "27";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "27" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -5) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "26";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "26" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -6) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "25";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "25" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else {
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 7);
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 7) + "日" +
                            "~" + mDay + "日");
                }
            } else if (mMonth == 2) {
                if (mDay - 7 == 0) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "29";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -1) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "28";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -2) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "27";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "27" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -3) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "26";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "26" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -4) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "25";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "25" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -5) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "24";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "24" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -6) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "23";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "23" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else {
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 7);
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 7) + "日" +
                            "~" + mDay + "日");
                }
            } else {
                if (mDay - 7 == 0) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "30";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "30" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -1) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "29";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -2) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "28";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -3) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "27";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "27" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -4) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "26";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "26" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -5) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "25";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "25" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else if (mDay - 7 == -6) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "24";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "24" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else {
                    mStartDate = mYear + "-" + (mMonth + 1) + "-" + (mDay - 7);
                    mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + (mDay - 7) + "日" +
                            "~" + mDay + "日");
                }
            }
        }
    }

    //设置一个月日期
    private void setMonthDayTitle() {
        //如果是大月31日的
        if (mMonth == 2 || mMonth == 4 || mMonth == 6 || mMonth == 7
                || mMonth == 9 || mMonth == 11) {
            if (mMonth == 2) {
                if (mYear % 4 != 0) {
                    if (mDay == 31 || mDay == 30 || mDay == 29) {
                        mStartDate = mYear + "-" + (mMonth) + "-" + "28";
                        mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "28" + "日" + "~" +
                                (mMonth + 1) + "月" + mDay + "日");
                    } else {
                        mStartDate = mYear + "-" + (mMonth) + "-" + mDay;
                        mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + mDay + "日" + "~" +
                                (mMonth + 1) + "月" + mDay + "日");
                    }
                } else {
                    if (mDay == 31 || mDay == 30) {
                        mStartDate = mYear + "-" + (mMonth) + "-" + "29";
                        mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "29" + "日" + "~" +
                                (mMonth + 1) + "月" + mDay + "日");
                    } else {
                        mStartDate = mYear + "-" + (mMonth) + "-" + mDay;
                        mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + mDay + "日" + "~" +
                                (mMonth + 1) + "月" + mDay + "日");
                    }
                }
            } else {
                if (mDay == 31) {
                    mStartDate = mYear + "-" + (mMonth) + "-" + "30";
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + "30" + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                } else {
                    mStartDate = mYear + "-" + (mMonth) + "-" + mDay;
                    mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + mDay + "日" + "~" +
                            (mMonth + 1) + "月" + mDay + "日");
                }
            }
        } else if (mMonth == 0) {
            mStartDate = (mYear - 1) + "-" + "12" + "-" + mDay;
            mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + mDay + "日" + "~" + mYear + "年"
                    + (mMonth + 1) + "月" + mDay + "日");
        } else {
            mStartDate = mYear + "-" + (mMonth) + "-" + mDay;
            mTvDateTitle.setText(mYear + "年" + (mMonth) + "月" + mDay + "日" + "~" + (mMonth + 1) +
                    "月" + mDay + "日");
        }
    }

    //设置一个季度日期
    private void setQuarterDayTitle() {
        if (mMonth == 5 || mMonth == 7 || mMonth == 8) {
            mStartDate = mYear + "-" + (mMonth - 2) + "-" + mDay;
            mTvDateTitle.setText(mYear + "年" + (mMonth - 2) + "月" + mDay + "日" + "~" + (mMonth +
                    1) + "月" + mDay + "日");
        } else if (mMonth - 2 == 2) {
            if (mYear % 4 != 0) {
                mStartDate = mYear + "-" + (mMonth - 2) + "-" + "28";
                mTvDateTitle.setText(mYear + "年" + (mMonth - 2) + "月" + "28" + "日" + "~" +
                        (mMonth + 1) + "月" + mDay + "日");
            } else {
                mStartDate = mYear + "-" + (mMonth - 2) + "-" + "29";
                mTvDateTitle.setText(mYear + "年" + (mMonth - 2) + "月" + "29" + "日" + "~" +
                        (mMonth + 1) + "月" + mDay + "日");
            }

        } else if (mMonth == 6 || mMonth == 11) {
            if (mDay == 31) {
                mStartDate = mYear + "-" + (mMonth - 2) + "-" + "30";
                mTvDateTitle.setText(mYear + "年" + (mMonth - 2) + "月" + "30" + "日" + "~" +
                        (mMonth + 1) + "月" + mDay + "日");
            } else {
                mStartDate = mYear + "-" + (mMonth - 2) + "-" + mDay;
                mTvDateTitle.setText(mYear + "年" + (mMonth - 2) + "月" + mDay + "日" + "~" +
                        (mMonth + 1) + "月" + mDay + "日");
            }
        } else {
            if (mMonth - 2 == 0) {
                mStartDate = (mYear - 1) + "-" + "12" + "-" + mDay;
                mTvDateTitle.setText((mYear - 1) + "年" + "12" + "月" + mDay + "日" + "~" + mYear +
                        "年" + (mMonth + 1) + "月" + mDay + "日");
            } else if (mMonth - 2 == -1) {
                mStartDate = (mYear - 1) + "-" + "11" + "-" + mDay;
                mTvDateTitle.setText((mYear - 1) + "年" + "11" + "月" + mDay + "日" + "~" + mYear +
                        "年" + (mMonth + 1) + "月" + mDay + "日");
            } else if (mMonth - 2 == -2) {
                if (mDay == 31) {
                    mStartDate = (mYear - 1) + "-" + "10" + "-" + "30";
                    mTvDateTitle.setText((mYear - 1) + "年" + "10" + "月" + "30" + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                } else {
                    mStartDate = (mYear - 1) + "-" + "10" + "-" + mDay;
                    mTvDateTitle.setText((mYear - 1) + "年" + "10" + "月" + mDay + "日" + "~" +
                            mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.oneDay:
                setPercentOfPass();
                mTvDateTitle.setText(mYear + "年" + (mMonth + 1) + "月" + mDay + "日");
                oneDay.setSelected(true);
                threeDay.setSelected(false);
                week.setSelected(false);
                this.month.setSelected(false);
                quarter.setSelected(false);
                mStartDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
                mEndDate = mYear + "-" + (mMonth + 1) + "-" + mDay;
                getDayData(mStartDate, mEndDate, 1);
                break;
            case R.id.threeDay:
                setPercentOfPass();
                oneDay.setSelected(false);
                threeDay.setSelected(true);
                week.setSelected(false);
                this.month.setSelected(false);
                quarter.setSelected(false);
                setThreeDayTitle();
                getDayData(mStartDate, mEndDate, 2);
                break;
            case R.id.week:
                setPercentOfPass();
                oneDay.setSelected(false);
                threeDay.setSelected(false);
                week.setSelected(true);
                this.month.setSelected(false);
                quarter.setSelected(false);
                setWeekDayTitle();
                getDayData(mStartDate, mEndDate, 3);
                break;
            case R.id.month:
                setPercentOfPass();
                oneDay.setSelected(false);
                threeDay.setSelected(false);
                week.setSelected(false);
                month.setSelected(true);
                quarter.setSelected(false);
                setMonthDayTitle();
                getDayData(mStartDate, mEndDate, 4);
                break;
            case R.id.quarter:
                setPercentOfPass();
                oneDay.setSelected(false);
                threeDay.setSelected(false);
                week.setSelected(false);
                this.month.setSelected(false);
                quarter.setSelected(true);
                setQuarterDayTitle();
                getDayData(mStartDate, mEndDate, 5);
                break;

        }
    }

    /**
     * 柏拉图数据改变动画
     */
    private void changeDatasAnimate(final float[] floatLines, final float[] floatColumns) {
        List<Line> line = mComboData.getLineChartData().getLines();
        for (int i = 0; i < line.size(); i++) {
            List<PointValue> values = line.get(i).getValues();
            for (int j = 0; j < values.size(); j++) {
                values.get(j).setTarget(values.get(j).getX(), floatLines[j]);
            }
        }
        List<Column> columns = mComboData.getColumnChartData().getColumns();
        for (int i = 0; i < columns.size(); i++) {
            List<SubcolumnValue> values = columns.get(i).getValues();
            for (int j = 0; j < values.size(); j++) {
                values.get(j).setTarget(floatColumns[i]);
            }
        }
        mComboView.startDataAnimation();

    }

    /**
     * 饼状改变数据时的动画
     */
    private void changePiesAnimate(float[] floatValue) {
        String[] strings = new String[]{"开路", "短路", "缺口", "针孔", "铜渣", "凸起", "凹陷", "线细", "线粗",
                "线距", "其他"};
        for (int i = 0; i < mPieChartData.getValues().size(); i++) {
            mPieChartData.getValues().get(i).setTarget(floatValue[i]).setLabel(floatValue[i] +
                    "%" + " " + strings[i]);
        }
        mPieChartView.startDataAnimation();         //设置动画
    }

    @OnClick(R.id.filter)
    void setFilter() {
        showLeftPopupWindow(-Utils.dip2px(this, 50), 0);

    }

    //左菜单选项
    private void showLeftPopupWindow(int xoff, int yoff) {
        View view = LayoutInflater.from(this).inflate(R.layout.menu, null);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        MenuLeftAdapter adapter = new MenuLeftAdapter(this);
        listView.setAdapter(adapter);
        PopupWindow2 = new PopupWindow(this);
        PopupWindow2.setHeight(Utils.dip2px(this, 83));
        PopupWindow2.setWidth(Utils.dip2px(this, 100));
        PopupWindow2.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        PopupWindow2.setOutsideTouchable(true);
        PopupWindow2.setFocusable(true);
        PopupWindow2.setContentView(view);
        PopupWindow2.showAsDropDown(filter, xoff, yoff);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(MainActivity.this, FilterActivity.class));
                } else {
                    //删除登录标识
                    SharedPreferences sf = getSharedPreferences("Greatsen", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sf.edit();
                    edit.remove("account");
                    edit.remove("password");
                    edit.commit();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
                PopupWindow2.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis - mTime > 1500) {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mTime = timeMillis;
        } else {
            mTime = timeMillis;
            finish();
            System.exit(0);
        }
    }

}
