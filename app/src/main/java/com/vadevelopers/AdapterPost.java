package com.vadevelopers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.HolderPost> {

    private Context context;
    private ArrayList<ModelPost> postArrayList;

    public AdapterPost(Context context, ArrayList<ModelPost> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public HolderPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout

        View view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false);
        return new HolderPost(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPost holder, int position) {
        // get data set data

        ModelPost model = postArrayList.get(position);// get data at specific position

        // get data

        String authorName = model.getAutherName();
        String content = model.getContent();
        String id = model.getContent();
        String published = model.getPublished();
        String title = model.getTitle();
        String updated = model.getUpdated();
        String url = model.getUrl();

        //Content description in HTMl So we need to Convert using JSOUP

        Document document = Jsoup.parse(content);

        // there may be multiple image on post so get one first

        try {
            Elements elements = document.select("img");
            String image = elements.get(0).attr("src");
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageTv);


        } catch (Exception e) {
            holder.imageTv.setImageResource(R.drawable.ic_image_black);

        }
        //format date

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

        holder.titletv.setText(title);
        holder.descriptionTv.setText(document.text());
        holder.publishInfo.setText(" By " + authorName + "  " +  formattedDate);//By Vivek Anand


    }

    @Override
    public int getItemCount() {
        return postArrayList.size(); //retrurns number of post
    }

    class HolderPost extends RecyclerView.ViewHolder {

        //UI view of row_post.xml

        ImageButton moreBtn;
        TextView titletv, publishInfo, descriptionTv;
        ImageView imageTv;


        public HolderPost(@NonNull View itemView) {
            super(itemView);

            moreBtn = itemView.findViewById(R.id.moreBtn);
            titletv = itemView.findViewById(R.id.titleTv);
            publishInfo = itemView.findViewById(R.id.publishInfoTv);
            imageTv = itemView.findViewById(R.id.imageTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);


        }
    }
}
