package com.sunyie.android.greatsen.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sunyie.android.greatsen.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shaohong on 2017-2-13.
 */

public class BatchFragment extends DialogFragment {

    private ArrayAdapter<String> mAdapter;
    private ListView mListView;
    private List<String> mList;
    private onTestListener mActivity1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        mList = new ArrayList<>();

        SharedPreferences sf = getActivity().getSharedPreferences("batch", Context.MODE_PRIVATE);
        SharedPreferences sfSearch = getActivity().getSharedPreferences("search", Context.MODE_PRIVATE);
        SharedPreferences sfNumber = getActivity().getSharedPreferences("number", Context.MODE_PRIVATE);
        String strNumber = sfNumber.getString("strNumber", null);

        Log.e("batchNumber", strNumber);
        int searchSize = sfSearch.getInt("search_size", 0);

        mList.clear();
        mList.add("all");
        for (int i = 0; i < searchSize; i++) {
            String search = sfSearch.getString("search_" + i, null);
            Log.e("search",search);
            int batchSize = sf.getInt("batch_size"+i, 0);
            if (strNumber.equals(search)) {
                for (int j = 0; j < batchSize; j++) {
                    mList.add(sf.getString("batch_" + i + j, null));
                    Log.e("batch_", sf.getString("batch_" + i + j,null));
                }
            }

        }

        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mList);

        View view = inflater.inflate(R.layout.fragment_batch, container, false);
        mListView = (ListView) view.findViewById(R.id.listView_batch);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mActivity1.onTest(mList.get(i));
                getDialog().cancel();
            }
        });
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity1 = (onTestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");

        }
    }

    @OnClick(R.id.backImageView)
    public void setTvBackBatch() {
        getDialog().cancel();
    }

    public interface onTestListener {
        void onTest(String str);

    }

}
