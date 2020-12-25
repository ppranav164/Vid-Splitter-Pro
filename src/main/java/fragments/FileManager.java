package fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.DrmInitData;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.card.splitter_pro.DeleteContent;
import com.card.splitter_pro.DeselectContent;
import com.card.splitter_pro.ExternalClick;
import com.card.splitter_pro.FileManager_RecyclerView;
import com.card.splitter_pro.R;
import com.card.splitter_pro.videoPlayer;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FileManager#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileManager extends Fragment implements ExternalClick , DeleteContent , View.OnClickListener, DeselectContent {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    ArrayList<String> filename = new ArrayList<>();
    RecyclerView recyclerView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String DIR_OUTPUT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/Video Splitter";
    String DIR_TEMP = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/.Temp";
    FileManager_RecyclerView fileManager_recyclerView;

    ImageView deleteBtn,clearAll;
    TextView infotext;

    ArrayList<String> selectedVideoList = new ArrayList<>();
    ArrayList<Uri> selectedVideo = new ArrayList<Uri>();

    public FileManager() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FileManager.
     */
    // TODO: Rename and change types and number of parameters
    public static FileManager newInstance(String param1, String param2) {
        FileManager fragment = new FileManager();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.e("Fragment","File Manager");

        setHasOptionsMenu(true);
        reset();

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.files,menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.delete : deleteSelectedVideo();
            break;
            case R.id.share : shareVideo();
            break;
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_file_manager, container, false);
        recyclerView = view.findViewById(R.id.filesview);
        deleteBtn = view.findViewById(R.id.deleteV);
        clearAll = view.findViewById(R.id.clearAll);
        infotext = view.findViewById(R.id.info);
        deleteBtn.setOnClickListener(this);
        clearAll.setOnClickListener(this);
        if (recyclerView != null)
        {
            showFiles();
        }
        return view;
    }


    public void showFiles()
    {
        disableSwitches();
        clearAll.setVisibility(View.INVISIBLE);
        File[] files = null;
        filename.clear();

        getSelectedVideos();

        File sdCard = new File(DIR_OUTPUT);
        if (!sdCard.exists())
        {
            sdCard.mkdir();
        }

        FilenameFilter fileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                Log.e("file",name);

                if (validateVideo(name))
                {
                    filename.add(name);
                }
                return dir.isFile();
            }
        };

        files = sdCard.listFiles(fileFilter);

        fileManager_recyclerView = new FileManager_RecyclerView(files,getContext(),this,filename,DIR_OUTPUT,this,this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(fileManager_recyclerView);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
    }


    @Override
    public void isClicked(boolean clicked, String dirname, int id) {
        if (validateVideo(dirname))
        {
            openPlayer(dirname);
        }
    }


    public boolean validateVideo(String video)
    {
        int EXTENSION = video.lastIndexOf(".")+1;
        String name = video.substring(EXTENSION);

        if (name.equals("mp4"))
        {
            return true;
        }
        return false;
    }


    public void openPlayer(String videoPath)
    {
        Intent intent = new Intent(getContext(),videoPlayer.class);
        intent.putExtra("video",videoPath);
        startActivity(intent);
    }


    @Override
    public void deleteVideo(ArrayList<String> videoname) {

        selectedVideoList = videoname;
        enableSwitches();
        selectedVideo.clear();

        for (int i=0; i < selectedVideoList.size(); i++)
        {
            Uri uri = Uri.parse(DIR_OUTPUT+"/"+selectedVideoList.get(i));
            selectedVideo.add(uri);
        }
       
    }

   
    public void enableSwitches()
    {
        infotext.setVisibility(View.VISIBLE);
        clearAll.setVisibility(View.VISIBLE);
        deleteBtn.setClickable(true);
        deleteBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_red));
        updateInfo();
    }

    public void disableSwitches()
    {
        infotext.setVisibility(View.INVISIBLE);
        clearAll.setVisibility(View.INVISIBLE);
        deleteBtn.setClickable(false);
        deleteBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete));
    }

    public void shareVideo()
    {
       if (selectedVideo.size() > 0)
       {
           Log.e("Menu","shareVideo");
           Intent shareIntent = new Intent();
           shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
           shareIntent.setType("video/");
           shareIntent.putParcelableArrayListExtra(shareIntent.EXTRA_STREAM,selectedVideo);
           startActivity(Intent.createChooser(shareIntent,"Share Video via"));
           showFilesForshare();
       }
    }



    public void deleteSelectedVideo(){

        Log.e("Menu","deleteSelectedVideo");
        for (int i=0; i < selectedVideoList.size(); i++)
        {
            String video = selectedVideoList.get(i);
            onDeletionConfirm(video);
            Log.e("deleted",video);
        }
    }


    public void onDeletionConfirm(String video)
    {
        File file = new File(DIR_OUTPUT+"/"+video);
        File tempFile = new File(DIR_TEMP+"/"+video+".png");
        tempFile.delete();
        file.delete();
        showFiles();
    }


    public void clearAllSelection()
    {
        selectedVideoList.clear();
        selectedVideo.clear();
        showFiles();
    }


    public void reset()
    {
        selectedVideoList.clear();
        selectedVideo.clear();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.deleteV : deleteSelectedVideo();
            break;

            case  R.id.clearAll : clearAllSelection();
            break;
        }
    }


    @Override
    public void deselect(int position, String video) {

        removeSelectedVideo(position,video);
        clearSelect(position,video);
        getSelectedVideos();


        if (!(selectedVideoList.size() > 0))
        {
            disableSwitches();
        }else {
            enableSwitches();
            updateInfo();
        }
    }



    public void updateInfo()
    {
        int total = selectedVideoList.size();
        String text = "("+total+")"+" Videos";
        infotext.setText(text);
    }


    public void getSelectedVideos()
    {
        for (int i =0; i < selectedVideoList.size(); i++)
        {
            String video = selectedVideoList.get(i);
            Log.e("getSelectedVideos >> ",""+video);

        }
    }


    public void showFilesForshare()
    {
        for (int i =0; i < selectedVideo.size(); i++)
        {
            String video = selectedVideo.get(i).toString();
            Log.e("showFilesForshare >> ",""+video);

        }
    }



    public void removeSelectedVideo(int pos,String video_name)
    {
        for (int i =0; i < selectedVideoList.size(); i++)
        {
            if (selectedVideoList.get(i).equals(video_name))
            {
                String video = selectedVideoList.get(i);
                selectedVideoList.remove(i);
                Log.e("deselect found at",video + "Pos "+i);
            }
        }

        Uri uri = Uri.parse(DIR_OUTPUT+"/"+video_name);
        for (int i=0; i < selectedVideo.size(); i++)
        {
            if (selectedVideo.get(i).equals(uri))
            {
                selectedVideo.remove(i);
            }
        }
    }


    public void clearSelect(int pos,String video_name)
    {

    }


}