package com.idjmao.lib.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.idjmao.lib.FloatWindowService;
import com.idjmao.lib.R;

import java.util.List;

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
        }else if (!isServiceRunning(this,FloatWindowService.class)){
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

    /**
     * 判断Service是否正在运行
     *
     * @param context     上下文
     * @param serviceClass Service 类
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isServiceRunning(Context context, Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfoList = manager.getRunningServices(200);
        if (serviceInfoList.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo info : serviceInfoList) {
            if (info.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }

}