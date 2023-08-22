package com.idjmao.lib.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.idjmao.lib.R;
import com.idjmao.lib.TestClient;
import com.idjmao.lib.adapter.FileLineAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListActivity extends AppCompatActivity {
    TextView folderPathTv;
    RecyclerView fileListRv;

    public static final String ROOT_FILE_PATH_KEY="rootFilePath";
    File folder;
    File rootFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        folderPathTv=findViewById(R.id.file_path_tv);
        fileListRv=findViewById(R.id.file_list_rv);

        String rootPath=getIntent().getStringExtra(ROOT_FILE_PATH_KEY);
        if (TextUtils.isEmpty(rootPath)){
            Toast.makeText(this, "目录为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        rootFile=new File(rootPath);
        showFileList(rootFile);
    }

    private void showFileList(File folder){
        this.folder=folder;
        folderPathTv.setText(folder.getPath().replace(rootFile.getPath(),""));
        List<File> fileList=Arrays.asList(folder.listFiles());

        FileLineAdapter adapter=new FileLineAdapter(fileList);
        fileListRv.setAdapter(adapter);
        fileListRv.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new FileLineAdapter.OnItemClickListener() {
            @Override
            public void onFileClick(File file) {
                Log.i("TAG", "onClick: "+file.getPath());
                if (file.isFile()){

                }else if (file.listFiles()==null||file.listFiles().length==0){
                    Toast.makeText(FileListActivity.this, "此文件夹为空", Toast.LENGTH_SHORT).show();
                }else {
                    showFileList(file);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (!folder.getPath().equals(rootFile.getPath())){
                showFileList(folder.getParentFile());
                return true;
            }else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}