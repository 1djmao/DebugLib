package com.idjmao.lib.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.idjmao.lib.R;
import com.idjmao.lib.TestClient;
import com.idjmao.lib.adapter.AdapterOnClickListener;
import com.idjmao.lib.adapter.TextAdapter;

import java.io.File;

public class DirListActivity extends AppCompatActivity {
    RecyclerView dirListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dir_list);

        dirListView=findViewById(R.id.dir_list);

        TextAdapter adapter=new TextAdapter(TestClient.getDirNameList());
        dirListView.setAdapter(adapter);
        dirListView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnClickListener(new AdapterOnClickListener<String>() {
            @Override
            public void onClick(int pos, String data) {
                File dir=TestClient.getDirList().get(pos);
                if (dir.listFiles()==null||dir.listFiles().length==0){
                    Toast.makeText(DirListActivity.this, "此目录为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(DirListActivity.this,FileListActivity.class);
                intent.putExtra(FileListActivity.ROOT_FILE_PATH_KEY,dir.getPath());
                startActivity(intent);
            }
        });

    }
}