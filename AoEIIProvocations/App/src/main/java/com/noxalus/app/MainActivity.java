package com.noxalus.app;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    // Logging
    private static final String TAG = "MainActivity";

    private int[] _soundsResources;
    private String[] _soundsTitles;
    public final static String[] ShareTypes =  {
            "com.twitter.android",
            "com.facebook.katana",
            "com.google.android.apps.plus",
            "com.android.email",
            "com.google.android.gm",
            "com.android.mms" };
    private int _currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _currentPosition = 0;
        _soundsResources = new int[]
        {
                R.raw.yes,
                R.raw.no,
                R.raw.food_please,
                R.raw.wood_please,
                R.raw.gold_please,
                R.raw.stone_please,
                R.raw.ahh,
                R.raw.all_hail,
                R.raw.oooh,
                R.raw.back_to_age_1,
                R.raw.herb_laugh,
                R.raw.being_rushed,
                R.raw.blame_your_isp,
                R.raw.start_the_game,
                R.raw.don_t_point_that_thing,
                R.raw.enemy_sighted,
                R.raw.it_is_good,
                R.raw.i_need_a_monk,
                R.raw.long_time_no_siege,
                R.raw.my_granny,
                R.raw.nice_town_i_ll_take_it,
                R.raw.quit_touchin,
                R.raw.raiding_party,
                R.raw.dadgum,
                R.raw.smite_me,
                R.raw.the_wonder,
                R.raw.you_play_2_hours,
                R.raw.you_should_see_the_other_guy,
                R.raw.roggan,
                R.raw.wololo,
                R.raw.attack_an_enemy_now,
                R.raw.cease_creating_extra_villagers,
                R.raw.create_extra_villagers,
                R.raw.build_a_navy,
                R.raw.stop_building_a_navy,
                R.raw.wait_for_my_signal_to_attack,
                R.raw.build_a_wonder,
                R.raw.give_me_your_extra_resources,
                R.raw.ally,
                R.raw.enemy,
                R.raw.neutral,
                R.raw.what_age_are_you_in,
        };

        // We create the sound list
        ListView listView = (ListView) findViewById(R.id.sound_list);

        _soundsTitles = getResources().getStringArray(R.array.array_sound_titles);

        MyAdapter adapter = new MyAdapter(this, R.layout.sound_list_layout, _soundsTitles);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                MyAdapter adapter = (MyAdapter) adapterView.getAdapter();
                SetSoundAsDialogFragment dialog = new SetSoundAsDialogFragment(
                        adapter.context, _soundsResources[pos], _soundsTitles[pos]);

                FragmentManager fm = getFragmentManager();

                dialog.show(fm, "SET_SOUND_AS");

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                MyAdapter adapter = (MyAdapter) adapterView.getAdapter();

                _currentPosition = pos;

                MediaPlayer mp = MediaPlayer.create(adapter.context, _soundsResources[pos]);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();

                        // Share with social network
                        SharedPreferences pref = getSharedPreferences("shared_preferences", MODE_PRIVATE);
                        int shareTypeId = pref.getInt("SHARE_TYPE", -1);
                        if (shareTypeId > -1)
                        {
                            String name = _soundsTitles[_currentPosition];
                            String subject = "[AoE2][Provoc] Devine ce que j'ai écouté ?";
                            String content = "J'ai écouté le son \"" + name + "\" à l'aide de l'application \"Age of Empires 2 Provocations\" !";
                            shareIntent(ShareTypes[shareTypeId], subject, content);
                        }

                        /*
                        String name = _soundsTitles[_currentPosition];
                        final Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, "J'ai écouté le son \"" + name + "\" à l'aide de l'application \"Age of Empires 2 Provocations\" !");
                        startActivity(Intent.createChooser(MessIntent, "Partager avec"));
                        */
                    };
                });


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareIntent(String type, String subject, String content) {
        boolean found = false;

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type) ) {
                    share.putExtra(Intent.EXTRA_SUBJECT, subject);
                    share.putExtra(Intent.EXTRA_TEXT, content);
                    share.setPackage(info.activityInfo.packageName);

                    found = true;

                    break;
                }
            }
            if (!found)
                return;

            startActivity(Intent.createChooser(share, "Partager avec"));
        }
    }

}
