package com.alximik.capoeiralyrics.network;

import com.alximik.capoeiralyrics.entities.Song;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 6/28/12 6:41 PM
 */
public class Api {
    static ApiConstants constants;
    static {
        if (constants == null) {
            constants  = new Constants();
        }
    }

    private static String getSongsSync() throws IOException {
        // Test api - only 5 songs
        //String url = "http://dl.dropbox.com/u/5324061/sample.json";
        String url = constants.getServerUrl() + "/JSONAPI/AllSongsFull?token=" + constants.getSecurityToken();

        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);
        if(response!=null){
            return  EntityUtils.toString(response.getEntity());
        } else {
            throw new IOException("Couldn't retrieve data");
        }
    }

    
    public static void getSongs(final SongsCallback callback) {
        if (callback == null)
            return;

        Thread thread = new Thread() {
            public void run() {
                try {
                    String data = getSongsSync();
                    JSONArray array = new JSONArray(data);
                    
                    List<Song> result = new ArrayList<Song>(array.length());
                    for(int i=0; i<array.length(); i++) {
                        result.add(  Song.fromJson(array.getJSONObject(i)) );
                    }

                    callback.onSuccess(result);
                } catch (IOException e) {
                    callback.onError("Couldn't connect server");
                } catch (JSONException e) {
                    callback.onError("Couldn't parse server's response");
                }

            }
        };
        thread.start();
    }

}
