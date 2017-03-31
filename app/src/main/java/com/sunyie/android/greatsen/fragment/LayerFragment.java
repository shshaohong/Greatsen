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

public class LayerFragment extends DialogFragment {

    private ArrayAdapter<String> mAdapter;
    private ListView mListView;
    private List<String> mList;
    private onListener mActivity1;

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

        SharedPreferences sf = getActivity().getSharedPreferences("level", Context.MODE_PRIVATE);
        SharedPreferences sfSearch = getActivity().getSharedPreferences("search", Context.MODE_PRIVATE);
        SharedPreferences sfNumber = getActivity().getSharedPreferences("number", Context.MODE_PRIVATE);
        String strNumber = sfNumber.getString("strNumber", null);

        Log.e("layerNumber", strNumber);
        int searchSize = sfSearch.getInt("search_size", 0);

        mList.clear();
        mList.add("all");
        for (int i = 0; i < searchSize; i++) {
            String search = sfSearch.getString("search_" + i, null);
            Log.e("search",search);
            int levelSize = sf.getInt("level_size"+i, 0);
            if (strNumber.equals(search)) {
                for (int j = 0; j < levelSize; j++) {
                    mList.add(sf.getString("level_" + i + j, null));
                    Log.e("level", sf.getString("level_" + i + j,null));
                }
            }

        }

        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mList);

        View view = inflater.inflate(R.layout.fragment_layer, container, false);
        mListView = (ListView) view.findViewById(R.id.listView_layer);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mActivity1.onLayerTest(mList.get(i));
                getDialog().cancel();
            }
        });
        ButterKnife.inject(this, view);
        return view;
    }
    @OnClick(R.id.backImageView)
    public void setTvBackLayer(){
        getDialog().cancel();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity1 = (onListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public interface onListener {
        void onLayerTest(String str);

    }
}
