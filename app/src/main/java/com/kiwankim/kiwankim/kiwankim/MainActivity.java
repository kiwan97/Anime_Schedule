package com.kiwankim.kiwankim.kiwankim;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    ViewPager mViewpager;
    Context context;
    ArrayList< ArrayList<String> > Msg = new ArrayList<>();
    ArrayList< ArrayList<String> > Msg2 = new ArrayList<>();
    private TextViewPagerAdapter pagerAdapter;
    private String Json;
    String WeekDay2[] = {"SUN","MON","TUE","WED","THR","FRI","SAT"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_left);
        context=this;
        for(int i=0;i<7;i++){
            Msg.add(new ArrayList<String>());
            Msg2.add(new ArrayList<String>());
        }
        try{
            Json = new Task().execute().get();
            getAnimeTitle(Json);
            getAnimeTime(Json);
        }
        catch (InterruptedException e) {
            e.printStackTrace(); }
        catch (ExecutionException e) {
            e.printStackTrace();

        }
        mViewpager = findViewById(R.id.viewPager);
        pagerAdapter = new TextViewPagerAdapter(this,Msg,Msg2,Json);
        mViewpager.setAdapter(pagerAdapter);
        mViewpager.setCurrentItem(35+(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))-1,false);
    }

    public void getAnimeTitle(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray;
            for(int j=0;j<7;j++) {
                jsonArray = jsonObject.getJSONArray(WeekDay2[j]);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject subObject = jsonArray.getJSONObject(i);
                    Msg.get(j).add(subObject.getString("s"));
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getAnimeTime(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray;
            for(int j=0;j<7;j++) {
                jsonArray = jsonObject.getJSONArray(WeekDay2[j]);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject subObject = jsonArray.getJSONObject(i);
                    Msg2.get(j).add(subObject.getString("t"));
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

