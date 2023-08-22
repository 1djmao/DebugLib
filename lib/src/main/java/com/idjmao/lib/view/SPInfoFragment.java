package com.idjmao.lib.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.idjmao.lib.R;
import com.idjmao.lib.adapter.KeyValueAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SPInfoFragment extends Fragment {
    protected View rootView;
    RecyclerView spValueList;
    String spName;

    public SPInfoFragment(String spName) {
        this.spName=spName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_show_sp, container, false);
        }
        spValueList=rootView.findViewById(R.id.sp_value_list);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showSP();
    }

    private void showSP(){
        List<String> keyList=new ArrayList<>();
        List<String> valueList=new ArrayList<>();
        Map<String,Object> spMap= (Map<String, Object>) getContext().getSharedPreferences(spName, Context.MODE_PRIVATE).getAll();

        for (String key : spMap.keySet()) {
            keyList.add(key);
            valueList.add(spMap.get(key).toString());
        }

        KeyValueAdapter adapter=new KeyValueAdapter(keyList,valueList);
        spValueList.setAdapter(adapter);
        spValueList.setLayoutManager(new LinearLayoutManager(getContext()));


    }

}
