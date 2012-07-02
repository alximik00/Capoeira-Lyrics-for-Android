package com.alximik.capoeiralyrics.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import com.alximik.capoeiralyrics.Constants;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.db.SongsStorage;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.utils.SU;
import com.markupartist.android.widget.ActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 02.07.12 14:41
 */
public class DetailsViewActivity extends Activity {
    private ActionBar actionBar;
    private List<String> texts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_details);

        actionBar = (ActionBar)findViewById(R.id.actionbar);
        actionBar.addAction(new ActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.arrow_left;
            }

            @Override
            public void performAction(View view) {
                finish();
            }
        });
        long songId = getIntent().getExtras().getLong(Constants.SONG_ID);
        Song song = SongsStorage.findById(this, songId);
        if (song == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Song not found")
                    .setMessage("Sorry, somehow I couldnt find the song. Please try again :(")
                    .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).show();
            return;
        }

        createTexts(song);
    }

    private void createTexts(Song song) {
        texts = new ArrayList<String>();
        if (!SU.isEmpty( song.getText() ) ) {
            texts.add( song.getText() );
        }

        if (!SU.isEmpty( song.getEngText() ) ) {
            texts.add( song.getEngText() );
        }

        if (!SU.isEmpty( song.getRusText() ) ) {
            texts.add( song.getRusText() );
        }

        for (int i=0; i<texts.size(); i++) {
            String text = formatText( texts.get( i ) );
            texts.set(i, text);
        }
    }

    private String formatText(String text) {
        return  text;
    }
}
