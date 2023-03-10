package com.jiangtai.team.ui.signIn;



import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jiangtai.team.R;
import com.jiangtai.team.bean.Person;
import com.jiangtai.team.bean.Project;
import com.jiangtai.team.bean.TaskBean;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class SignlnFragmentUtil {
    public static List<Person> find1;
    private static List<String> objects,objects1,objects2,isobjects,isobjects1,isobjects2;
    private static List<Project> projects,isprojects;
    //public static String GetSignln = "http://192.168.1.13:5080/user/people_insert4";
    public static String projectstringid,isprojectstringid;
    public static int position;
    private static AlertDialog alertDialog;
    private static Window window;
    public static ArrayList<Person> personList1,wdkpersonList;
    public static  ArrayList<String> ydkpersonList;

    public static class HttpUrlConnectionUtils2 {

        public static String doPost(String url, String params) {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            PrintWriter writer = null;
            String content = null;
            StringBuffer sbf = new StringBuffer();
            try {
                URL u = new URL(url);
                conn = (HttpURLConnection) u.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setReadTimeout(50000);
                conn.setConnectTimeout(60000);
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("content-Type", "application/json");

                writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.print(params);
                writer.flush();
                InputStream inputStream;

                int status = conn.getResponseCode();
                if(status!=200){
                    inputStream = conn.getErrorStream();
                }else {
                    inputStream = conn.getInputStream();
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((content = reader.readLine()) != null) {
                    sbf.append(content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null)
                    conn.disconnect();
            }
            return sbf.toString();

        }

    }

    public static void people_insert(String name,String is_url,String selectdd,String seletect,ArrayList<Person> personList){
        new Thread(new Runnable() {


            private String string;
            private String isname;
            private String itcode;

            @Override
            public void run() {

                for (int i = 0; i< personList.size(); i++){
                    if (name.equals(personList.get(i).getPersonId())){
                        personList.get(i).setSex("1");
                        ydkpersonList.add(personList.get(i).getName());
                        wdkpersonList.remove(personList.get(i));
                    }
                }
                personList1 = personList;
                    if(name.equals("33373039383331393938313130363238")){
                        isname = "李翔霏";
                        itcode="370983199811062817";
                    }else if(name.equals("32313037383231393832313130363032")){
                        isname ="孙长成";
                        itcode="210782198211060213";
                    }
                    else if(name.equals("31353236333431393937303532343432")){
                        isname ="张圆";
                        itcode="152634199705244219";
                    }
                    else if(name.equals("35303031303131393937303831373434")){
                        isname ="田鑫宇";
                        itcode="500101199708174411";
                    }
                    else if(name.equals("31343232323331393934313031333333")){
                        isname ="张旭";
                        itcode="142223199410133316";
                    }
                    else if(name.equals("33343131303231393937303832323038")){
                        isname ="杜凡";
                        itcode="341102199708220817";
                    }else{
                        isname ="田鑫宇";
                        itcode="500101199708174411";
                    }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name",isname);
                    jsonObject.put("job", "士兵");
                    jsonObject.put("pic_url", "");
                    jsonObject.put("people_code","12");
                    jsonObject.put("site",selectdd);
                    jsonObject.put("id_card",itcode);
                    jsonObject.put("meetName",seletect);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                string = HttpUrlConnectionUtils2.doPost(is_url, jsonObject.toString());
            }
        }).start();

    }

    public static void SignInIent(Context context){


        Intent intent = new Intent(context,UpdateSigninActivity.class);
        context.startActivity(intent);
    }

    public static void setDetails(Context context, List<Person> find){
        find1 = find;
        Intent intent = new Intent(context, SigninDetailsAdapterActivity.class);
        context.startActivity(intent);
    }

    public static SharedPreferences getsp(Context context){
     return context.getSharedPreferences("isselecterdata", MODE_PRIVATE);
    }

    public static void SpinnerUtil(Context context, List<TaskBean> lsit, Spinner spinner){
        objects = new ArrayList<String>(){};
        objects1 = new ArrayList<String>(){};
        objects2 = new ArrayList<String>(){};
        for (int i =0;i<lsit.size();i++){
            projects = LitePal.where("taskId = ?", lsit.get(i).getTaskId()).find(Project.class);
          for (int j=0;j<projects.size();j++){
              objects.add(projects.get(j).getProjectName());
              objects1.add(projects.get(j).getProjectId());

          }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, objects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
//        SharedPreferences pre = context.getSharedPreferences("kqposition", MODE_PRIVATE);
//        int jgposition= pre.getInt("position",0);
//        spinner.setSelection(jgposition,true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {



            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                SharedPreferences.Editor   editor = context.getSharedPreferences("kqposition", MODE_PRIVATE).edit();
//                editor.clear();
//                editor.putInt("position", position);
//                editor.commit();
                projectstringid = objects1.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

public  static void popwindow(Context context){
    AlertDialog.Builder   builder = new AlertDialog.Builder(context);
    alertDialog = builder.create();
    alertDialog.setCanceledOnTouchOutside(false);
    View popupView = View.inflate(context, R.layout.dialog_sign_in_window, null);
    Spinner rzglsp  =popupView.findViewById(R.id.rzglsp);
    TextView is_cancel=popupView.findViewById(R.id.is_cancel);
    TextView is_confirm  =popupView.findViewById(R.id.is_confirm);
    List<TaskBean>  find =  LitePal.findAll(TaskBean.class);
    if(find.size() > 0){
        isobjects = new ArrayList<String>(){};
        isobjects1 = new ArrayList<String>(){};
        isobjects2 = new ArrayList<String>(){};
        for (int i =0;i<find.size();i++){
            isprojects = LitePal.where("taskId = ?", find.get(i).getTaskId()).find(Project.class);
            for (int j=0;j<isprojects.size();j++){
                    isobjects.add(isprojects.get(j).getProjectName());
                    isobjects1.add(isprojects.get(j).getProjectId());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, isobjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rzglsp.setAdapter(adapter);

//        SharedPreferences pre = context.getSharedPreferences("jgposition", MODE_PRIVATE);
//        int jgposition= pre.getInt("position",0);
//        rzglsp.setSelection(jgposition,true);
        rzglsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {



            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                SharedPreferences.Editor   editor = context.getSharedPreferences("jgposition", MODE_PRIVATE).edit();
//                editor.clear();
//                editor.putInt("position", position);
//                editor.commit();
                isprojectstringid = isobjects1.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    } else {

    }
    is_cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            alertDialog.dismiss();
        }
    });
    is_confirm.setOnClickListener(new View.OnClickListener() {

        private SharedPreferences.Editor editor;

        @Override
        public void onClick(View v) {
            editor = context.getSharedPreferences("isnotproject", MODE_PRIVATE).edit();
            editor.putString("current_major_project", isprojectstringid);
            editor.commit();
            alertDialog.dismiss();
        }
    });
    alertDialog.show();
    alertDialog.setContentView(popupView);
    window = alertDialog.getWindow();
    window.setBackgroundDrawableResource(android.R.color.transparent);
    window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    window.setGravity(Gravity.CENTER);

}

    public static String sharSignlnfragment(Context context){
        String current_major_project;
        SharedPreferences pre = context.getSharedPreferences("isnotproject", MODE_PRIVATE);
        if (pre.equals(null)){
            current_major_project=null;
        }else{
            current_major_project= pre.getString("current_major_project","");
        }
        return current_major_project;
    }


    public static void oncreatArralist(){
        ydkpersonList = new ArrayList<>();
        wdkpersonList = new ArrayList<>();
        personList1 = new ArrayList<>();

    }



}
