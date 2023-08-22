package com.idjmao.lib.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.idjmao.lib.FloatWindowService;
import com.idjmao.lib.R;

public class LibMainActivity extends AppCompatActivity {

    private int FLOAT_WINDOW_REQUEST=1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showLogClick(View view) {
        if (!Settings.canDrawOverlays(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请打开本应用的悬浮窗权限");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), FLOAT_WINDOW_REQUEST);
                }
            });
            builder.create().show();
        }else {
            startService(new Intent(this, FloatWindowService.class));
        }

    }

    public void showSPClick(View view) {
        startActivity(new Intent(this,ShowSPActivity.class));
    }

    public void showDBClick(View view) {
        startActivity(new Intent(this,ShowDbActivity.class));
    }

    public void showFilesClick(View view) {
        startActivity(new Intent(this,DirListActivity.class));
    }
}