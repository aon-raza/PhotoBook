package com.sar.photobook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.mongodb.util.JSON;
import com.sar.photobook.models.album;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class home extends AppCompatActivity {
    private Toolbar toolbar;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    private List<album> postsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Photo Book");

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        postsList = new ArrayList<album>();

        if (isOnline()){
            getAlbumsData();
        }
        else{
            Toast.makeText(home.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                moveTaskToBack(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void getAlbumsData(){

        compositeDisposable.add(iMyService.getAlbum()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {
                        //my own
                        JSONArray jsonArray = new JSONArray(JSON.serialize(response));
                        int i=0;
                        while(i<jsonArray.length()){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            album post = new album(jsonObject.getString("title"), jsonObject.getString("url"));
                            postsList.add(post);

                            i++;
                        }
                        initRecyclerViewAlbum();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(home .this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private void initRecyclerViewAlbum(){
        RecyclerView recyclerViewMyAds = findViewById(R.id.recyclerViewAlbum);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMyAds.setLayoutManager(layoutManager4);

        recyclerViewAdapterAlbum adapter = new recyclerViewAdapterAlbum(this,postsList);
        recyclerViewMyAds.setItemAnimator( new DefaultItemAnimator());
        recyclerViewMyAds.setAdapter(adapter);
    }
}
