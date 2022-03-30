package com.vadevelopers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private RecyclerView postsRv;
    private Button loadMorebtn;
    private EditText Searchet;
    private ImageButton Searchbtn;


    private String url = "";
    private String nextToken = "";
    private ArrayList<ModelPost> postArrayList;
    private AdapterPost adapterPost;
    private ProgressDialog progressDialog;
    private static final String TAG = "MAIN_TAG";
    private boolean isSearch = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postsRv = findViewById(R.id.postsRv);
        loadMorebtn = findViewById(R.id.loadMorebtn);
        Searchbtn = findViewById(R.id.SearchBtn);
        Searchet = findViewById(R.id.SearchEt);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");


        postArrayList = new ArrayList<>();
        postArrayList.clear();

        loadPosts();

        //loadmore btnclick

        loadMorebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = Searchet.getText().toString().trim();
                if (TextUtils.isEmpty(query)) {
                    loadPosts();

                } else {
                    searchPost(query);
                }
            }
        });

        Searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextToken = "";
                url = "";
                postArrayList = new ArrayList<>();
                postArrayList.clear();

                String query = Searchet.getText().toString().trim();
                if (TextUtils.isEmpty(query)) {
                    loadPosts();

                } else {

                    searchPost(query);


                }

            }
        });


    }

    private void searchPost(String query) {
        isSearch = true;
        Log.d(TAG, "searchPost: isSearch" + isSearch);

        progressDialog.show();

        if (nextToken.equals("")) {
            Log.d(TAG, "searchPost: Next page token is empty...");
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID + "/posts/search?q="
                    + query + "&key="
                    + Constants.API_KEY;

        } else if (nextToken.equals("end")) {
            Log.d(TAG, "loadpost:Next page token empty/end so no more post");
            Toast.makeText(this, "No more Posts", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        } else {
            Log.d(TAG, "searchPost: Next Token : " + nextToken);
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID + "/posts/search?q="
                    + query +
                    "&pageToken=" + nextToken
                    + "&key=" + Constants.API_KEY;

        }
        Log.d(TAG, "searchPost: URL " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, "onRespone: " + response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    try {
                        nextToken = jsonObject.getString("nextPageToken");
                        Log.d(TAG, "onResponse: NextPageToken:" + nextToken);
                    } catch (Exception e) {

                        Toast.makeText(MainActivity.this, "Reached end of this page...", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: Reached end of page:" + e.getMessage());
                        nextToken = "end";
                    }
                    // get data from json array

                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    // continue getting data using loop

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String title = jsonObject1.getString("title");
                            String content = jsonObject1.getString("content");
                            String published = jsonObject1.getString("published");
                            String updated = jsonObject1.getString("updated");
                            String url = jsonObject1.getString("url");
                            String selfLink = jsonObject1.getString("selfLink");
                            String authorName = jsonObject1.getJSONObject("author").getString("displayName");
                            //String image = jsonObject1.getJSONObject("author").getString("image");

                            //set data

                            ModelPost modelPost = new ModelPost("" + authorName,
                                    "" + content,
                                    "" + id,
                                    "" + published,
                                    "" + selfLink,
                                    "" + title,
                                    "" + updated,
                                    "" + url);


                            postArrayList.add(modelPost);


                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: 1:" + e.getMessage());
                            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }

                    //setup adpter

                    adapterPost = new AdapterPost(MainActivity.this, postArrayList);
                    //setup adapter recyclerview

                    postsRv.setAdapter(adapterPost);

                    progressDialog.dismiss();

                } catch (Exception e) {

                    Log.d(TAG, "onResponse: 2:" + e.getMessage());
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

        //request que

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void loadPosts() {
        isSearch = false;
        Log.d(TAG, "loadPosts: isSearch" + isSearch);
        progressDialog.show();

        if (nextToken.equals("")) {
            Log.d(TAG, "loadPost: Next page token is empty...");
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID + "/posts?maxResults="
                    + Constants.MAX_POST_RESULTS + "&key="
                    + Constants.API_KEY;

        } else if (nextToken.equals("end")) {
            Log.d(TAG, "loadpost:Next page token empty/end so no more post");
            Toast.makeText(this, "No more Posts", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        } else {
            Log.d(TAG, "loadPost: Next Token : " + nextToken);
            url = "https://www.googleapis.com/blogger/v3/blogs/"
                    + Constants.BLOG_ID + "/posts?maxResults="
                    + Constants.MAX_POST_RESULTS +
                    "&pageToken=" + nextToken
                    + "&key=" + Constants.API_KEY;

        }
        Log.d(TAG, "loadPost: URL " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d(TAG, "onRespone: " + response);


                try {
                    JSONObject jsonObject = new JSONObject(response);

                    try {
                        nextToken = jsonObject.getString("nextPageToken");
                        Log.d(TAG, "onResponse: NextPageToken:" + nextToken);
                    } catch (Exception e) {

                        Toast.makeText(MainActivity.this, "Reached end of this page...", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: Reached end of page:" + e.getMessage());
                        nextToken = "end";
                    }
                    // get data from json array

                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    // continue getting data using loop

                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String title = jsonObject1.getString("title");
                            String content = jsonObject1.getString("content");
                            String published = jsonObject1.getString("published");
                            String updated = jsonObject1.getString("updated");
                            String url = jsonObject1.getString("url");
                            String selfLink = jsonObject1.getString("selfLink");
                            String authorName = jsonObject1.getJSONObject("author").getString("displayName");
                            //String image = jsonObject1.getJSONObject("author").getString("image");

                            //set data

                            ModelPost modelPost = new ModelPost("" + authorName,
                                    "" + content,
                                    "" + id,
                                    "" + published,
                                    "" + selfLink,
                                    "" + title,
                                    "" + updated,
                                    "" + url);


                            postArrayList.add(modelPost);


                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: 1:" + e.getMessage());
                            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }

                    //setup adpter

                    adapterPost = new AdapterPost(MainActivity.this, postArrayList);
                    //setup adapter recyclerview

                    postsRv.setAdapter(adapterPost);

                    progressDialog.dismiss();

                } catch (Exception e) {

                    Log.d(TAG, "onResponse: 2:" + e.getMessage());
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });

        //request que

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}