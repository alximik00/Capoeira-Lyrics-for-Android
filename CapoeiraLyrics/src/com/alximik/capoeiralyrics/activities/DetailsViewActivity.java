package com.alximik.capoeiralyrics.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
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
import com.alximik.capoeiralyrics.network.ApiConstants;
import com.alximik.capoeiralyrics.network.NetworkConstants;
import com.alximik.capoeiralyrics.utils.SU;
import com.markupartist.android.widget.ActionBar;
import com.smaato.soma.AdType;
import com.smaato.soma.BannerView;
import uk.co.jasonfry.android.tools.ui.PageControl;
import uk.co.jasonfry.android.tools.ui.SwipeView;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author alximik
 * @since 02.07.12 14:41
 */
public class DetailsViewActivity extends Activity {
    private ArrayList<SpannableString> texts;
    private BannerView banner;
    private Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_details);

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        setupActionBar(actionBar);


        long songId = getIntent().getExtras().getLong(Constants.SONG_ID);
        song = SongsStorage.findById(this, songId);
        if (song == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Song not found")
                    .setMessage("Sorry, somehow I couldn't find the song. Please try again :(")
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
        swipeView.setPageControl(pageControl);

        for (int i=0; i<texts.size(); i++) {
            SpannableString text =  texts.get(i);
            swipeView.addView(constructView(text, song.getTitle(), i));
        }

        banner = (BannerView) findViewById(R.id.banner_view);
        if (banner != null) {
            Log.d(Constants.TAG, "Initializing smaato banner");
            ApiConstants constants = new NetworkConstants();
            banner.getUserSettings().setKeywordList("Android,Sports,Capoeira");
            banner.getAdSettings().setPublisherId(constants.getSmaatoPublisherId());
            banner.getAdSettings().setAdspaceId(constants.getSmaatoAdSpace());
            banner.getAdSettings().setAdType(AdType.ALL);
        }
    }

    private void setupActionBar(ActionBar actionBar) {

        actionBar.addAction(new ActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.ic_menu_share_holo_dark;
            }

            @Override
            public void performAction(View view) {
                Intent intent=new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);


                String subject = String.format("Just learn '%s' by '%s' ", song.getTitle(), song.getAuthor() );
                String url = "http://capoeiralyrics.info/Songs/Details/" + song.getId();

                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, url);

                startActivity(Intent.createChooser(intent, "How do you want to share?"));
            }
        });

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (banner != null) {
            banner.setAutoReloadEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (banner != null) {
            banner.setAutoReloadEnabled(false);
        }
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

    private View constructView(SpannableString text, String title, int index) {
        LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(linear);

        // Top tip
        if (texts.size()>1) {
            LinearLayout.LayoutParams tipParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                                                  LinearLayout.LayoutParams.WRAP_CONTENT);
            tipParams.topMargin = 10;
            tipParams.bottomMargin = 25;
            tipParams.gravity = Gravity.CENTER_HORIZONTAL;

            TextView tipText = new TextView(this);
            tipText.setLayoutParams(tipParams);

            tipText.setText(getTipText(index));

            tipText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            tipText.setTextColor(0xFF999999);
            tipText.setGravity(Gravity.CENTER_HORIZONTAL);
            linear.addView(tipText);
        }

        // Title
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.bottomMargin = 20;
        titleParams.gravity = Gravity.CENTER_HORIZONTAL;

        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleText.setGravity(Gravity.CENTER_HORIZONTAL);
        titleText.setLayoutParams(titleParams);
        titleText.setTextColor(0xFF2F7EED);
        linear.addView(titleText);

        // Content text
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        contentParams.bottomMargin = 20;
        contentParams.gravity = Gravity.CENTER_HORIZONTAL;

        TextView contentText = new TextView(this);
        contentText.setGravity( Gravity.CENTER_HORIZONTAL );
        contentText.setLayoutParams(contentParams);
        contentText.setText( text, TextView.BufferType.SPANNABLE );
        contentText.setLineSpacing(0, 2);
        linear.addView(contentText);

        return scrollView;
    }

    private CharSequence getTipText(int index) {
        String text =  "Swipe to change language";
        if (index > 0)
            text = "← " + text;

        if (index < texts.size() - 1)
            text += "→";
        return text;
    }

    private SpannableString formatText(String text) {

        int startIndex = 0;
        ArrayList<Pair> coroRegions = new ArrayList<Pair>();

        while(true) {
            startIndex = text.indexOf("[coro]", startIndex+1 );
            if (startIndex >= 0) {
                int endIndex = text.indexOf("[/coro]", startIndex+1 );
                coroRegions.add(new Pair(startIndex, endIndex));
            } else {
                break;
            }
        }

        text = text.replace("[coro]","");
        text = text.replace("[/coro]","");

        SpannableString result = new SpannableString(text);

        int correction = 0;
        for(Pair pair: coroRegions) {
            pair.first += correction;
            correction -= 6;
            pair.second += correction;
            correction -= 7;

            result.setSpan(new StyleSpan(Typeface.BOLD), pair.first, pair.second, 0);
        }
        return result;
    }

    static class Pair {
        int first;
        int second;
        Pair(int a, int b) {
            first = a;
            second = b;
        }
        public String toString() {
            return "<Pair f:" + first + " s: " + second;
        }
    }
}
