package com.alximik.capoeiralyrics.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.alximik.capoeiralyrics.Constants;
import com.alximik.capoeiralyrics.R;
import com.alximik.capoeiralyrics.db.SongsStorage;
import com.alximik.capoeiralyrics.entities.Song;
import com.alximik.capoeiralyrics.utils.SU;
import com.markupartist.android.widget.ActionBar;
import uk.co.jasonfry.android.tools.ui.PageControl;
import uk.co.jasonfry.android.tools.ui.SwipeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 02.07.12 14:41
 */
public class DetailsViewActivity extends Activity {
    private ArrayList<SpannableString> texts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_details);

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
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

        SwipeView swipeView = (SwipeView) findViewById(R.id.swipe_view);
        PageControl pageControl = (PageControl) findViewById(R.id.page_control);

        swipeView.addView(constructView());

    }

    private void createTexts(Song song) {
        texts = new ArrayList<SpannableString>();
        if (!SU.isEmpty( song.getText() ) ) {
            texts.add(formatText(song.getText()));
        }

        if (!SU.isEmpty( song.getEngText() ) ) {
            texts.add( formatText(song.getEngText()) );
        }

        if (!SU.isEmpty( song.getRusText() ) ) {
            texts.add( formatText(song.getRusText()) );
        }
    }

    private View constructView() {
        LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.leftMargin = 40;
        params.bottomMargin = 40;
        params.topMargin = 40;
        linear.setLayoutParams(params);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(linear);

        TextView titleText = new TextView(this);
        titleText.setText("Swipe to change language →");
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        titleText.setTextColor(0xFF777777);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = 20;
        titleParams.topMargin = 40;
        titleParams.gravity = Gravity.CENTER_HORIZONTAL;

        titleText.setLayoutParams(titleParams);
        titleText.setGravity(Gravity.CENTER_HORIZONTAL);
        linear.addView(titleText);

        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        contentParams.bottomMargin = 20;
        contentParams.gravity = Gravity.CENTER_HORIZONTAL;

        TextView contentText = new TextView(this);
        contentText.setGravity( Gravity.CENTER_HORIZONTAL );
        contentText.setLayoutParams(contentParams);
        contentText.setText( texts.get(0), TextView.BufferType.SPANNABLE );
        linear.addView(contentText);

        return scrollView;
    }

    private SpannableString formatText(String text) {

        int oldStartIndex = 0;
        int startIndex = 0;
        
        StringBuilder builder = new StringBuilder();
        
        ArrayList<Pair<Integer, Integer>> coroRegions = new ArrayList<Pair<Integer, Integer>>();
        
        int coroRegionStart = -1;
        int coroRegionEnd = -1;

        while(true) {
            startIndex = text.indexOf("[coro]", oldStartIndex );
            if (startIndex >= 0) {
                builder.append( text.substring(oldStartIndex, startIndex) );
                startIndex += 6;

                coroRegionStart = startIndex;
                coroRegionEnd = text.indexOf("[/coro]", startIndex);

                builder.append(text.substring(coroRegionStart+6, coroRegionEnd));
                coroRegionEnd -= 6;
                
                coroRegions.add(new Pair<Integer, Integer>(coroRegionStart, coroRegionEnd));
                oldStartIndex = startIndex;
            } else {
                break;
            }
        }
        builder.append(text.substring(coroRegionEnd+4));

        SpannableString result;
        
        if (coroRegions.size() > 0) {
            result = new SpannableString(builder.toString());
            for(Pair<Integer, Integer> pair: coroRegions) {
                int start = pair.first;
                int end = pair.second;
                if (start >= 0 && end >0) {
                    result.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0) ;
                }
            }
        } else {
            result = new SpannableString(text);
        }
        
//        if (coroRegionEnd >= 0 && coroRegionEnd >= 0) {
//            result = new SpannableString(builder.toString());
//
//            result.setSpan(new StyleSpan(Typeface.BOLD), coroRegionStart, coroRegionEnd, 0) ;
//
//        } else {
//            result = new SpannableString(text);
//        }

        return result;
    }
}
