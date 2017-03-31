package com.sunyie.android.greatsen.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunyie.android.greatsen.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by shaohong on 2017-2-13.
 */

public class SearchFragment extends DialogFragment{

    @InjectView(R.id.tv_search)
    TextView mTvSearch;
    @InjectView(R.id.iv_search)
    ImageView mIvSearch;
    @InjectView(R.id.et_textSearch)
    EditText textSearch;

    private ArrayAdapter<String> mAdapter;
    private ArrayAdapter<String> mAdapter2;
    private ListView mListView;
    private List<String> mList;
    private List<String> mList2;
    private onSearchListener mActivity1;

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
        mList2 = new ArrayList<>();

        SharedPreferences search = getActivity().getSharedPreferences("search", Context.MODE_PRIVATE);
        int size = search.getInt("search_size", 0);
        mList.clear();
        mList.add("all");
        for (int i = 0; i < size; i++) {
            mList.add(search.getString("search_" + i, null));
        }

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, view);
        mListView = (ListView) view.findViewById(R.id.listView_search);
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mList);
        mAdapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mList2);
        mListView.setAdapter(mAdapter);

        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String s = charSequence.toString();
                mList2.clear();
                for (int j = 0; j < mList.size(); j++) {
                    int j1 = mList.get(j).indexOf(s, 0);
                    if (j1 >= 0) {
                        mList2.add(mList.get(j));
                    }
                }
                mAdapter2.notifyDataSetChanged();
                if (s.length() == 0) {
                    mList2.clear();
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    mAdapter2.notifyDataSetChanged();
                }else {
                    mListView.setAdapter(mAdapter2);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (textSearch.getText().toString().length() == 0) {
                    mActivity1.onSearchTest(mList.get(i));
                }else {
                    mActivity1.onSearchTest(mList2.get(i));
                }
                getDialog().cancel();
            }
        });

        return view;
    }
    @OnClick(R.id.iv_search)
    public void setTvSearch(){
        if (textSearch.getText().toString().length() == 0) {
            Toast.makeText(getContext(), "请输入料号", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "没有更多结果", Toast.LENGTH_SHORT).show();
        }
    }
    @OnClick(R.id.backImageView)
    public void setTvBackSearch() {
        getDialog().cancel();
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        try {
            mActivity1 = (onSearchListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public interface onSearchListener {
        void onSearchTest(String str);
    }

}
