package com.sar.photobook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

    private ProgressDialog progress;

    private AppDatabase appDatabase;

    private AppCompatButton seeMoreBtn;

    private int totalLoaded = 0;
    private int toBeLoadedNext = 20;

    RecyclerView recyclerViewMyAds;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        recyclerViewMyAds = findViewById(R.id.recyclerViewAlbum);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewMyAds.setLayoutManager(layoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Photo Book");

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        postsList = new ArrayList<album>();

        progress = new ProgressDialog(this);
        progress.setMessage("Loading Album... ");

        appDatabase = AppDatabase.getInstance(this);

        seeMoreBtn = findViewById(R.id.see_more_btn);
        seeMoreBtn.setVisibility(View.GONE);

        recyclerViewMyAds.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(layoutManager.findLastCompletelyVisibleItemPosition() == totalLoaded-1 &&
                        appDatabase.albumDao().getAll().size() > 0){
                    seeMoreBtn.setVisibility(View.VISIBLE);
                }
            }
        });


        if (appDatabase.albumDao().getAll().size() > 0){
            postsList = appDatabase.albumDao().getAll().subList(0,toBeLoadedNext);
            initRecyclerViewAlbum();

//            Toast.makeText(home.this, "from room" + appDatabase.albumDao().getAll().size(), Toast.LENGTH_SHORT).show();
        }
        else {
            seeMoreBtn.setVisibility(View.GONE);
            if (isOnline()){
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setIndeterminate(true);
                progress.setProgress(0);
                progress.show();
                progress.setCanceledOnTouchOutside(false);
                getAlbumsData();
            }
            else{
                Toast.makeText(home.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        seeMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeMoreBtn.setVisibility(View.GONE);
                postsList = appDatabase.albumDao().getAll().subList(0,totalLoaded + toBeLoadedNext);
                initRecyclerViewAlbum();
            }
        });
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
                            progress.setIndeterminate(false);
                            progress.setProgress(i/(jsonArray.length()/100) +1);
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            final album post = new album(jsonObject.getString("title"), jsonObject.getString("url"));
                            postsList.add(post);

                            appDatabase.albumDao().insertAll(post);

                            i++;
                        }
//                        Toast.makeText(getApplicationContext(), "from online" +i, Toast.LENGTH_LONG).show();
                        initRecyclerViewAlbum();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(home.this, "Error! \n"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private void initRecyclerViewAlbum(){

        recyclerViewAdapterAlbum adapter = new recyclerViewAdapterAlbum(this,postsList);
        recyclerViewMyAds.setItemAnimator( new DefaultItemAnimator());
        recyclerViewMyAds.setAdapter(adapter);

        if (totalLoaded > 0){
            recyclerViewMyAds.getLayoutManager().scrollToPosition(totalLoaded -1);
        }
        else{
            recyclerViewMyAds.getLayoutManager().scrollToPosition(totalLoaded);
        }
        totalLoaded = postsList.size();
        progress.dismiss();
    }
}
