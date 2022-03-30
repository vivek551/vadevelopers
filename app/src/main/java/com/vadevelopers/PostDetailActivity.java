package com.vadevelopers;

import static javax.xml.transform.OutputKeys.ENCODING;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostDetailActivity extends AppCompatActivity {

    private TextView titleTv,publishInfoTv;
    private WebView WebView;

    private String postId; // get this from intent
    public static final String TAG = "POST_DETAIL_TAG";

    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        titleTv = findViewById(R.id.titleTv);
        publishInfoTv = findViewById(R.id.publishInfoTv);
        WebView = findViewById(R.id.WebView);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Post Details");


        postId = getIntent().getStringExtra("postId");
        Log.d(TAG, "onCreate: "+postId);


        WebView.getSettings().setJavaScriptEnabled(true);
        WebView.getSettings().setDomStorageEnabled(true);
        WebView.setWebViewClient(new WebViewClient());
        WebView.setWebChromeClient(new WebChromeClient());

        loadPostDetails();




    }

    private void loadPostDetails() {
        String url = "https://www.googleapis.com/blogger/v3/blogs/"+Constants.BLOG_ID
                +"/posts/"+postId
                +"?key=+"+Constants.API_KEY;

        Log.d(TAG, "loadPostDetails: URl"+url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Succesfully received response
                Log.d(TAG, "onResponse: "+response);

                try {
                    JSONObject jsonObject  = new JSONObject(response);
                    //-----------get data-------------
                    String title = jsonObject.getString("title");
                    String published = jsonObject.getString("published");
                    String content = jsonObject.getString("content");
                    String url = jsonObject.getString("url");

                    String displayName = jsonObject.getJSONObject("author").getString("displayName");

                    //date

                    String gmtDate = published;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd 'Time' HH:mm:ss");
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/mm/yyyy K:mm a"); //25/10/2020 02:10pm

                    String formattedDate = " ";

                    try {
                        Date date = dateFormat.parse(gmtDate);
                        formattedDate = dateFormat2.format(date);


                    } catch (Exception e) {
                        formattedDate = published;
                        e.printStackTrace();

                    }

                    //----------Set data----------
                    actionBar.setSubtitle(title);

                    titleTv.setText(title);
                    publishInfoTv.setText("By " +displayName+ " " +formattedDate);

                    //contents HTML
                    WebView.loadDataWithBaseURL(null ,content,"text/html" ,ENCODING, null);




                }catch (Exception e){
                    Log.d(TAG, "onResponse: "+e.getMessage());
                    Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //failed to recieve response
                Toast.makeText(PostDetailActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest );


    }

    @Override
    public boolean onSupportNavigateUp() {
     onBackPressed();
        return super.onSupportNavigateUp();
    }
}