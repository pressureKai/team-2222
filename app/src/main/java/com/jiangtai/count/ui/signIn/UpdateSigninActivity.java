package com.jiangtai.count.ui.signIn;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.blankj.utilcode.util.ToastUtils;
import com.jiangtai.count.R;

import java.util.ArrayList;

public class UpdateSigninActivity extends AppCompatActivity {
    private String spname;
    private Spinner seleteurl;
    private EditText selectdd,seletect;
    private ArrayList isseleteurl;
    private Button id_over;
     private String is_url;
    private SharedPreferences.Editor selecterdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_signin);
        selectdd =  findViewById(R.id.selectdd);
        seletect =  findViewById(R.id.seletect);
        seleteurl = findViewById(R.id.seleteurl);
        id_over = findViewById(R.id.id_over);

        isseleteurl = new ArrayList<String>(){{add("请选择签到类型"); add("集训签到"); add("集训课堂");add("集训参观");add("集训餐厅");}};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, isseleteurl);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
        seleteurl.setAdapter(adapter);
        seleteurl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               int position1 = position;
                spname = parent.getSelectedItem().toString();

                if (position1==0){
                  //  is_url="";
                    is_url="http://192.168.1.13:5080/user/people_insert4";
                }else if (position1==1){
                    is_url="http://192.168.1.13:5080/user/people_insert1";
                }else if (position1==2){
                    is_url="http://192.168.1.13:5080/user/people_insert2";
                }else if (position1==3){
                    is_url="http://192.168.1.13:5080/user/people_insert3";
                }else if (position1==4){
                    is_url="http://192.168.1.13:5080/user/people_insert4";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        id_over.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                if (is_url==""){
                    ToastUtils.showShort("请选择签到类型");
                 }
                else if (selectdd.getText().toString().trim().equals("")){
                    ToastUtils.showShort("请输入地点");
                 }
                else if (seletect.getText().toString().trim().equals("")){
                    ToastUtils.showShort("请输入活动id");
                 }else{

                    selecterdata = getSharedPreferences("isselecterdata", MODE_PRIVATE).edit();
                    selecterdata.clear();
                    selecterdata.putString("is_url", is_url);
                    selecterdata.putString("selectdd", selectdd.getText().toString().trim());
                    selecterdata.putString("seletect", seletect.getText().toString().trim());
                    selecterdata.commit();
                    finish();
                }



            }
        });
    }

}