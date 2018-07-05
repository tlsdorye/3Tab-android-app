package com.example.project1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class Tab2_Fragment extends Fragment{
    private final static String TAG = "Tab2_Fragment";
    private static final int REQUEST_PERMISSION_KEY = 1;
    ArrayList<HashMap<String, String >> albumList = new ArrayList<>();
    LoadAlbum loadAlbumTask;
    GridView tab2GridView;
    Context context;
    private DeliveryData deliveryData;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);
        tab2GridView = view.findViewById(R.id.tab2_F_GridView);
        context = getActivity();
        setTab2GridView(view);

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Function.hasPermissions(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSION_KEY);
        }

        return view;
    }

    public void setTab2GridView(View view){

        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);
        if(dp < 360) {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getContext());
            tab2GridView.setColumnWidth(Math.round(px));
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_PERMISSION_KEY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadAlbumTask = new LoadAlbum();
                    loadAlbumTask.execute();
                } else {
                    Toast.makeText(getContext(), "You must accept permissions.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Function.hasPermissions(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSION_KEY);
        }else{
            loadAlbumTask = new LoadAlbum();
            loadAlbumTask.execute();
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DeliveryData){
            deliveryData = (DeliveryData)context;
        }else {
            throw new RuntimeException(context.toString()+"must implement this interface");
        }
    }

    public void onDetach() {
        super.onDetach();
        deliveryData = null;
    }

    class LoadAlbum extends AsyncTask<String, Void, String > {

        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";
            String path = null;
            String album = null;
            String timestamp = null;
            String countPhoto = null;
            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };

            Cursor cursorExternal =getActivity().getContentResolver().query(uriExternal, projection,
                    "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null);
            Cursor cursorInternal = getActivity().getContentResolver().query(uriInternal, projection,
                    "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});

            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                countPhoto = Function.getCount(getActivity(), album);

                albumList.add(Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), countPhoto));
            }
            cursor.close();
            Collections.sort(albumList, new MapComparator(Function.KEY_TIMESTAMP, "dsc"));
            return xml;
        }

        protected void onPostExecute(String xml) {
            GridViewAdapter adapter = new GridViewAdapter(context, R.layout.tab2_album_row, albumList);
            tab2GridView.setAdapter(adapter);
            tab2GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    //Intent intent = new Intent(getActivity(),Tab2AlbumActivity.class);
                    //intent.putExtra("name", albumList.get(+position).get(Function.KEY_ALBUM));
                    //startActivity(intent);
                    deliveryData.deliver(albumList,position);
                }
            });
        }
    }

    public interface DeliveryData{
        void deliver(ArrayList<HashMap<String,String>> data, int position);
    }

}

class GridViewAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<HashMap<String, String>> data;
    LayoutInflater inf;

    public GridViewAdapter(Context context, int layout, ArrayList<HashMap<String, String>> data) {

        this.context = context;
        this.layout = layout;
        this.data = data;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            convertView = inf.inflate(layout, null);
            holder.gallery_image = convertView.findViewById(R.id.tab2_AR_image);
            holder.gallery_count = convertView.findViewById(R.id.tab2_AR_count);
            holder.gallery_title = convertView.findViewById(R.id.tab2_AR_title);
            convertView.setTag(holder);

            //Bitmap myBitmap = BitmapFactory.decodeFile(data.get(position));
            //holder.gallery_image.setImageBitmap(myBitmap);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.gallery_image.setId(position);
        holder.gallery_count.setId(position);
        holder.gallery_title.setId(position);

        HashMap<String, String > song = data.get(position);
        try{
            holder.gallery_title.setText(song.get(Function.KEY_ALBUM));
            holder.gallery_count.setText(song.get(Function.KEY_COUNT));
            Glide.with(context).load(new File(song.get(Function.KEY_PATH))).into(holder.gallery_image);
        } catch (Exception e){}
        return convertView;
    }

}

class ViewHolder {
    ImageView gallery_image;
    TextView gallery_count, gallery_title;
}

class Function {

    static final String KEY_ALBUM = "album_name";
    static final String KEY_PATH = "path";
    static final String KEY_TIMESTAMP = "timestamp";
    static final String KEY_TIME = "date";
    static final String KEY_COUNT = "date";

    public static  boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static HashMap<String, String> mappingInbox(String album, String path, String timestamp, String time, String count) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_ALBUM, album);
        map.put(KEY_PATH, path);
        map.put(KEY_TIMESTAMP, timestamp);
        map.put(KEY_TIME, time);
        map.put(KEY_COUNT, count);
        return map;
    }

    public static String getCount(Context c, String album_name)
    {
        Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };
        Cursor cursorExternal = c.getContentResolver().query(uriExternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
        Cursor cursorInternal = c.getContentResolver().query(uriInternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});

        return cursor.getCount()+" Photos";
    }

    public static String converToTime(String timestamp)
    {
        long datetime = Long.parseLong(timestamp);
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd/MM HH:mm");
        return formatter.format(date);
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}

class MapComparator implements Comparator<HashMap<String, String>> {
    private final String key;
    private final String order;

    public MapComparator(String key, String order)
    {
        this.key = key;
        this.order = order;
    }

    public int compare(HashMap<String, String> first,
                       HashMap<String, String> second)
    {
        // TODO: Null checking, both for maps and values
        String firstValue = first.get(key);
        String secondValue = second.get(key);
        if(this.order.toLowerCase().contentEquals("asc"))
        {
            return firstValue.compareTo(secondValue);
        }else{
            return secondValue.compareTo(firstValue);
        }
    }
}
