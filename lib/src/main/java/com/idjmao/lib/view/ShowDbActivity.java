package com.idjmao.lib.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.idjmao.lib.R;
import com.idjmao.lib.TestClient;
import com.idjmao.lib.utils.DbHelper;
import com.idjmao.library.TableView;

import java.util.ArrayList;
import java.util.List;

public class ShowDbActivity extends AppCompatActivity {
    TextView dbNameTv;
    Spinner tableSp;
    TableView tableView;
    DbHelper dataHelper;
    List<String> tableNames;
    String selectTableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_db);
        dbNameTv=findViewById(R.id.db_name_tv);
        tableSp=findViewById(R.id.table_name_sp);
        tableView=findViewById(R.id.table_view);
        if (TextUtils.isEmpty(TestClient.getDbName())){
            Toast.makeText(this, "未设置数据库", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getTables();
    }

    private void getTables() {
        dbNameTv.setText(TestClient.getDbName());

        dataHelper=new DbHelper(this, TestClient.getDbName(),null,1);
        if (dataHelper==null||dataHelper.getReadableDatabase()==null){
            Toast.makeText(this, "获取数据库失败", Toast.LENGTH_SHORT).show();
            return;
        }
        tableNames=dataHelper.getAllTableNames();
        if (tableNames==null||tableNames.size()==0){
            Toast.makeText(this, "未查询到 table", Toast.LENGTH_SHORT).show();
            return;
        }

        tableSp.setAdapter(new ArrayAdapter<String>(this,R.layout.item_text,R.id.item_text,tableNames));
        tableSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!selectTableName.equals(tableNames.get(position))){
                    selectTableName=tableNames.get(position);
                    getTableInfo();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectTableName =tableNames.get(0);
        getTableInfo();

    }

    private void getTableInfo(){
        List<String> headList=dataHelper.getTableMaster(selectTableName);
        List<List<String>> valueList=dataHelper.getValue(selectTableName);

        tableView.headers= headList.toArray(new String[headList.size()]);
        Log.i("TAG", "getTableInfo: "+tableView.headers);

        tableView.data=new ArrayList<>();
        if (valueList==null||valueList.size()==0){

        }else {
            for (int i = 0; i < valueList.size(); i++) {
                tableView.data.add(valueList.get(i).toArray(new String[valueList.get(i).size()]));
            }
        }
        tableView.refreshTable();

    }

}