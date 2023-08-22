package com.idjmao.lib.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.idjmao.lib.adapter.FragmentViewPageAdapter;
import com.idjmao.lib.R;
import com.idjmao.lib.TestClient;

import java.util.ArrayList;
import java.util.List;

public class ShowSPActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    List<Fragment> fragmentList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sp);
        tabLayout=findViewById(R.id.sp_tab);
        viewPager=findViewById(R.id.sp_fragment_vp);
        if (TestClient.getSpName().size()==0){
            Toast.makeText(this, "未设置 SP ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        for (int i = 0; i < TestClient.getSpName().size(); i++) {
            fragmentList.add(new SPInfoFragment(TestClient.getSpName().get(i)));
        }

        FragmentViewPageAdapter pageAdapter=new FragmentViewPageAdapter(getSupportFragmentManager(),fragmentList,TestClient.getSpName());
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}