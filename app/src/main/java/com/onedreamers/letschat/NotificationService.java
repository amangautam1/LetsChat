package com.onedreamers.letschat;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Aman gautam on 03-Apr-17.
 */

public class NotificationService extends Service {
    MainActivity activity;
    ListView listView;
    int NOTIFICATION_ID;
    NotificationManager mNotificationManager;
    private FirebaseListAdapter<ChatMessage> adapter;
    public NotificationService() {


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activity = new MainActivity();
        DatabaseHelper databaseHelper = new DatabaseHelper(new MainActivity());
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DatabaseHelper databaseHelper = new DatabaseHelper(new MainActivity());
        adapter = new MessageAdapter(new MainActivity(), ChatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReference());
           // new AsynTask().execute();
            Toast.makeText(this, "service started onStart", Toast.LENGTH_SHORT).show();
            return super.onStartCommand(intent, flags, startId);

    }



    @Override
    public void onDestroy(){
        Toast.makeText(this,"service stopped",Toast.LENGTH_SHORT).show();
    }




}
