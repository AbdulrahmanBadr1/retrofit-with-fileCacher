package com.example.android.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.HeterogeneousExpandableList;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.caching.FileCacher;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
String content="";
TextView tv;
    FileCacher<String> stringCacher = new FileCacher<>(MainActivity.this, "sometext.txt");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=findViewById(R.id.tv);

        if (isNetworkAvailable()){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        Api api = retrofit.create(Api.class);
        Call<List<Hero>> call = api.getHeroes();
        call.enqueue(new Callback<List<Hero>>() {
            @Override
            public void onResponse(Call<List<Hero>> call, Response<List<Hero>> response) {
                List<Hero> heroes = response.body();
                for (Hero h : heroes) {
                    content+=h.getName()+"\n";
                    content+=h.getRealname()+"\n";
                    content+=h.getCreatedby()+"\n";
                    content+=h.getFirstappearence()+"\n";
                    content+=h.getPublisher()+"\n"+"\n"+"\n"+"\n";

                    Log.d("name", h.getName());
                    ;
                    Log.d("real name ",h.getRealname());
                    Log.d("imageurl",h.getImageurl());
                }
                tv.setText(content);
                //write content in cache
                try {
                    stringCacher.writeCache(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }

            @Override
            public void onFailure(Call<List<Hero>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });}
        else
            {
                Toast.makeText(this, "Check your internet connection for newer data", Toast.LENGTH_SHORT).show();
                // read content from cache and display itt
                if(stringCacher.hasCache()){
                    try {
                        tv.setText(stringCacher.readCache());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassCastException e){
                        e.printStackTrace();
                    }
                }
            }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
