package com.noxalus.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

public class SettingsActivity extends ActionBarActivity {

    private RadioGroup radioShare;
    private RadioButton radioShareButton;
    private Button btnSave;
    private static final int RadioNoneIndex = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences pref = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        int shareTypeId = pref.getInt("SHARE_TYPE", -1);

        radioShare = (RadioGroup) findViewById(R.id.radioShare);
        btnSave = (Button) findViewById(R.id.btnSave);

        if (shareTypeId > -1)
        {
            ((RadioButton)radioShare.getChildAt(shareTypeId)).setChecked(true);
        }

        // Disable radio button when no application is found
        for (int i = 0; i < RadioNoneIndex; i++)
        {
            if (MainActivity.ShareTypes[i] != "sms" && !applicationExists(MainActivity.ShareTypes[i]))
            {
                RadioButton radioButton = (RadioButton)radioShare.getChildAt(i);
                radioButton.setEnabled(false);
                radioButton.setText(radioButton.getText() + " *");
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedId = radioShare.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioShareButton = (RadioButton) findViewById(selectedId);

                int shareTypeId = radioShare.indexOfChild(radioShareButton);

                if (shareTypeId == RadioNoneIndex)
                    shareTypeId = -1;

                SharedPreferences pref = getSharedPreferences("shared_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("SHARE_TYPE", shareTypeId);
                editor.commit();

                Toast.makeText(SettingsActivity.this, "Préférence enregistré !", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        });
    }

    private boolean applicationExists(String type)
    {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()){
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(type) ||
                        info.activityInfo.name.toLowerCase().contains(type) ) {
                    return true;
                }
            }
        }

        return false;
    }

}
