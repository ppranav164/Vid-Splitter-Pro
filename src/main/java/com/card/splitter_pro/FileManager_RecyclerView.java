package com.card.splitter_pro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class FileManager_RecyclerView extends RecyclerView.Adapter<FileManager_RecyclerView.listHolder>  {


    File[] files;
    Context context;
    ExternalClick externalClick;
    ArrayList<String> filename;
    String DIR_SPL;
    DeleteContent deleteContent;
    DeselectContent deselectContent;
    ArrayList<String> bitmaps = new ArrayList<>();
    boolean isLogClicked = false;
    String DIR_TEMP = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/.Temp";
    ArrayList<String> selectedVideo = new ArrayList<>();

    public FileManager_RecyclerView(File[] files, Context context, ExternalClick externalClick, ArrayList<String> filename, String DIR_SPL, DeleteContent deleteContent, DeselectContent deselectContent) {
        this.files = files;
        this.context = context;
        this.externalClick = externalClick;
        this.filename = filename;
        this.DIR_SPL = DIR_SPL;
        this.deleteContent = deleteContent;
        this.deselectContent = deselectContent;
    }

    @NonNull
    @Override
    public listHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_row,parent,false);
        listHolder listHolder = new listHolder(view);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final listHolder holder, int position) {

        holder.foldername.setText(filename.get(position));

        String file = DIR_TEMP+"/"+filename.get(position)+".png";

        Picasso.get().load(new File(file)).into(holder.iconview);


        holder.bar_layout.setTag(filename.get(position));

        holder.bar_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foldername = v.getTag().toString();

                if (!(selectedVideo.size() > 0))
                {
                    externalClick.isClicked(true,foldername,1);
                    isLogClicked = false;
                }else {

                    holder.checkView.setVisibility(View.VISIBLE);
                    LinearLayout linearLayout = (LinearLayout) v;
                    setSelected(linearLayout,holder.getAdapterPosition());
                }

            }
        });

        holder.bar_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.checkView.setVisibility(View.VISIBLE);
                LinearLayout linearLayout = (LinearLayout) v;
                setSelected(linearLayout,holder.getAdapterPosition());
                isLogClicked = true;
                return true;
            }
        });

    }


    public void setSelected(LinearLayout linearLayout,int position)
    {
        String videoNam = linearLayout.getTag().toString();
        ImageView imageView = (ImageView) linearLayout.getChildAt(0);
        String tag = imageView.getTag().toString();
        if (tag.equals("deselect"))
        {
            imageView.setTag("select");
            imageView.setVisibility(View.VISIBLE);
            selectedVideo.add(videoNam);
            deleteContent.deleteVideo(selectedVideo);
        }else {

            imageView.setTag("deselect");
            imageView.setVisibility(View.INVISIBLE);
            deselectContent.deselect(position,videoNam);
        }
    }


    public void showImage(ImageView imageView)
    {
        for (int i=0; i < bitmaps.size(); i++)
        {
            String image = DIR_SPL+"/"+bitmaps.get(i)+".png";
            Picasso.get().load(image).into(imageView);
        }
    }


    @Override
    public int getItemCount() {
        return filename.size();
    }


    public class listHolder extends  RecyclerView.ViewHolder
    {

        TextView foldername;
        ImageView iconview;
        LinearLayout bar_layout;
        ImageView checkView;

        public listHolder(@NonNull View itemView) {
            super(itemView);

            foldername = itemView.findViewById(R.id.dir_text);
            iconview = itemView.findViewById(R.id.icon_view);
            bar_layout = itemView.findViewById(R.id.layout_bar);
            checkView = itemView.findViewById(R.id.check);
        }
    }


}
