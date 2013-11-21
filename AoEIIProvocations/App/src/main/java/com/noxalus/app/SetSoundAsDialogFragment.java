package com.noxalus.app;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.TYPE_RINGTONE;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SetSoundAsDialogFragment extends DialogFragment {
    // Logging
    private static final String TAG = "Dialog";

    private ArrayList _selectedItems;

    private Context _context;
    private int _resource;
    private String _name;

    public SetSoundAsDialogFragment(Context context, int resource, String name)
    {
        _context = context;
        _resource = resource;
        _name = name;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        _selectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.set_as_dialog_title);
        builder.setMultiChoiceItems(R.array.dialog_array, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            _selectedItems.add(which);
                        } else if (_selectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            _selectedItems.remove(Integer.valueOf(which));
                        }
                    }
                });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK, so save the mSelectedItems results somewhere
                // or return them to the component that opened the dialog

                if (!_selectedItems.isEmpty())
                {
                    // Ringtone
                    if (_selectedItems.contains(0)) {

                        SetSoundAs(RingtoneManager.TYPE_RINGTONE);

                        Toast toast = Toast.makeText(_context, "\"" + _name + "\"\n a été ajouté comme sonnerie !", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        ((TextView)((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
                        toast.show();

                    }
                    // Notification
                    if (_selectedItems.contains(1)) {
                        SetSoundAs(RingtoneManager.TYPE_NOTIFICATION);

                        Toast toast = Toast.makeText(_context, "\"" + _name + "\"\n a été ajouté comme notification !", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        ((TextView)((LinearLayout)toast.getView()).getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
                        toast.show();
                    }

                }
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        return builder.create();
    }

    public void SetSoundAs(int type) {
        String basePath = "/sdcard/Media/Audio/Ringtones/";

        if (type == RingtoneManager.TYPE_NOTIFICATION)
            basePath = "/sdcard/Media/Audio/Notifications/";

        String fileName = _name + ".mp3";

        File file = new File(basePath, fileName);
        File baseDirectory = file.getParentFile();

        if (!baseDirectory.exists()) {
            baseDirectory.mkdirs();
        }

        if (file.exists()) {
            file.delete();
        }

        ContentResolver contentResolver = _context.getContentResolver();
        AssetFileDescriptor soundFile;
        Uri uri = Uri.parse("android.resource://com.noxalus.app/" + _resource);

        try {
            soundFile = contentResolver.openAssetFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            soundFile = null;
        }

        try {
            byte[] readData = new byte[1024];
            FileInputStream fis = soundFile.createInputStream();
            FileOutputStream fos = new FileOutputStream(file);
            int i = fis.read(readData);

            while (i != -1) {
                fos.write(readData, 0, i);
                i = fis.read(readData);
            }

            fos.close();
        } catch (IOException io) {
            Log.e(TAG, io.getMessage());
        }

        SetRingtone(file, type);
    }

    public void SetRingtone(File file, int type){
        boolean isNotification = (type == RingtoneManager.TYPE_NOTIFICATION);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, _name);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "mp3");
        values.put(MediaStore.Audio.Media.ARTIST, "Age of Empires 2 Sounds");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, !isNotification);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, isNotification);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        //Insert it into the database
        Log.i(TAG, "the absolute path of the file is :" + file.getAbsolutePath());
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        Uri newUri = _context.getContentResolver().insert(uri, values);
        Uri ringtoneUri = newUri;
        Log.i(TAG,"the ringtone uri is :"+ ringtoneUri);
        RingtoneManager.setActualDefaultRingtoneUri(_context, type, newUri);
    }
}
