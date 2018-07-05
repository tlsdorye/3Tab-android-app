package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tab3_Fragment extends Fragment {
    private static final String TAG = "Tab3Fragment";
    private static final String serverKey = "AIzaSyC6qPl9B3BO6oNCgEtLhpVH0w86niS4eQE";
    static DrawableManager drawableManager = new DrawableManager();
    private EditText editText;
    AsyncTask<?, ?, ?> searchTask;
    ArrayList<SearchData> searchData = new ArrayList<>();
    Tab3DeliveryData tab3DeliveryData;
    ListView searchList;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment,container,false);
        editText = view.findViewById(R.id.tab3_F_edittext);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchList = view.findViewById(R.id.tab3_F_listview);
        searchTask = new SearchTask().execute();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH) {
                    searchTask = new SearchTask().execute();
                }
                return false;
            }
        });
        return view;
    }

    class SearchTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                paringJsonData(getYouTube());
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            StoreListAdapter adapter = new StoreListAdapter(getActivity(), R.layout.tab3_listview_start, searchData);
            searchList.setAdapter(adapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Tab3DeliveryData){
            tab3DeliveryData = (Tab3DeliveryData)context;
        }else {
            throw new RuntimeException(context.toString()+"must implement this interface");
        }
    }

    public void onDetach() {
        super.onDetach();
        tab3DeliveryData = null;
    }


    public JSONObject getYouTube(){

        HttpGet httpGet = new HttpGet(
                "https://www.googleapis.com/youtube/v3/search?"
                        + "part=snippet&maxResults=20&q=" + editText.getText().toString()
                        + "&key="+ serverKey);
        // part(snippet),
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void paringJsonData(JSONObject jsonObject) throws JSONException {
        searchData.clear();
        JSONArray contacts = jsonObject.getJSONArray("items");

        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            String vodid = c.getJSONObject("id").getString("videoId");

            String title = c.getJSONObject("snippet").getString("title");
            String changString = "";
            try {
                changString = new String(title.getBytes("8859_1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String date = c.getJSONObject("snippet").getString("publishedAt")
                    .substring(0, 10);
            String imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails")
                    .getJSONObject("default").getString("url");

            searchData.add(new SearchData(vodid, changString, imgUrl, date));
        }
    }


    class StoreListAdapter extends ArrayAdapter<SearchData>{
        private ArrayList<SearchData> items;
        SearchData fInfo;
        // LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public StoreListAdapter(Context context, int resource, ArrayList<SearchData> items) {
            super(context, resource, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            fInfo = items.get(position);

            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.tab3_listview_start, null);
            ImageView img = (ImageView) v.findViewById(R.id.tab3_LS_image);

            String url = fInfo.getUrl();

            String sUrl = "";
            String eUrl = "";
            sUrl = url.substring(0, url.lastIndexOf("/") + 1);
            eUrl = url.substring(url.lastIndexOf("/") + 1, url.length());
            try {
                eUrl = URLEncoder.encode(eUrl, "EUC-KR").replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String new_url = sUrl + eUrl;

            drawableManager.fetchDrawableOnThread(new_url, img);

            v.setTag(position);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (Integer) v.getTag();
                    tab3DeliveryData.deliver3(items.get(pos).getVideoId());
                }
            });

            ((TextView) v.findViewById(R.id.tab3_LS_title)).setText(fInfo.getTitle());
            ((TextView) v.findViewById(R.id.tab3_LS_date)).setText(fInfo
                    .getPublishedAt());

            return v;
        }
    }

    public interface Tab3DeliveryData{
        void deliver3(String videoId);
    }

}

class DrawableManager {
    private final Map<String, Drawable> drawableMap;

    public DrawableManager() {
        drawableMap = new HashMap<String, Drawable>();
    }

    public Drawable fetchDrawable(String urlString) {
        if (drawableMap.containsKey(urlString)) {
            return drawableMap.get(urlString);
        }

        Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
        try {
            InputStream is = fetch(urlString);
            Drawable drawable = Drawable.createFromStream(is, "src");


            if (drawable != null) {
                drawableMap.put(urlString, drawable);
                Log.d(this.getClass().getSimpleName(), "got a thumbnail drawable: " + drawable.getBounds() + ", "
                        + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                        + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
            } else {
                Log.w(this.getClass().getSimpleName(), "could not get thumbnail");
            }

            return drawable;
        } catch (MalformedURLException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            return null;
        }
    }

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {
        if (drawableMap.containsKey(urlString)) {
            imageView.setImageDrawable(drawableMap.get(urlString));
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageView.setImageDrawable((Drawable) message.obj);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                //TODO : set imageView to a "pending" image
                Drawable drawable = fetchDrawable(urlString);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }

    private InputStream fetch(String urlString) throws MalformedURLException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }
}


class SearchData {
    String videoId;
    String title;
    String url;
    String publishedAt;

    public SearchData(String videoId, String title, String url,
                      String publishedAt) {
        super();
        this.videoId = videoId;
        this.title = title;
        this.url = url;
        this.publishedAt = publishedAt;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}