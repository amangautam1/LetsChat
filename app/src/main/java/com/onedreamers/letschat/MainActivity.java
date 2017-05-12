package com.onedreamers.letschat;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int SIGN_IN_REQUEST_CODE = 111;
    private FirebaseListAdapter<ChatMessage> adapter;
    private ListView listView;
    private ArrayList<ChatMessage> arrayList;
    Snackbar snackbar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseHelper databaseHelper;
    private String loggedInUserName = "";
    public static ArrayList<String> ArrayofName = new ArrayList<String>();
    Toolbar tb;
    ActionBarDrawerToggle ab;
    DrawerLayout dl;
    FirebaseDatabase database;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*tb=(Toolbar)findViewById(R.id.tb);
        dl=(DrawerLayout)findViewById(R.id.drawer_layout);
        setSupportActionBar(tb);
        ab=new ActionBarDrawerToggle(this,dl,tb,R.string.open,R.string.close);
        dl.addDrawerListener(ab);
        ab.syncState();*/
       // database = FirebaseDatabase.getInstance();
        //ref = database.getReference(ref.toString().substring(ref.root().toString().length-1));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final EditText input = (EditText) findViewById(R.id.input);
        listView = (ListView) findViewById(R.id.list);
        databaseHelper = new DatabaseHelper(this);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
           /*startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), SIGN_IN_REQUEST_CODE);*/
             Intent intent=new Intent(MainActivity.this,Log_in.class);
            startActivity(intent);
        } else {

            showAllOldMessages();
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            runTask();
            new AsynTask().execute();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Snackbar.make(findViewById(R.id.activity_main), "Sign in first", Toast.LENGTH_SHORT).show();
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(), SIGN_IN_REQUEST_CODE);
                } else if (input.getText().toString().trim().equals("")) {
                    snackbar = Snackbar.make(findViewById(R.id.activity_main), "Please enter some texts!", Toast.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                } else {
                    if (!isNetworkAvailable()) {
                         runTask();
                    } else {
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .push()
                                .setValue(new ChatMessage(input.getText().toString(),
                                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid())
                                );
                        input.setText("");
                    }
                }
            }
        });

       if(!isMyServiceRunning(NotificationService.class))
        {
            serviceStart();

        }



    }
    @Override
    public void onConfigurationChanged(Configuration configuration)
    {
     super.onConfigurationChanged(configuration);
        ab.onConfigurationChanged(configuration);
    }
    private boolean isMyServiceRunning(Class<?> ServiceClass)
    {
        ActivityManager manager=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo:manager.getRunningServices(Integer.MAX_VALUE)){
            if(ServiceClass.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Log Out?");
            builder.setMessage("Are you sure want to logout ?");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AuthUI.getInstance().signOut(MainActivity.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MainActivity.this, "You have logged out!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, Log_out.class);
                                    startActivity(intent);

                }
            });
                        }

                    });
            AlertDialog dialog = builder.create(); // calling builder.create after adding buttons
            dialog.show();

        return true;}
        if(item.getItemId()==R.id.about){
            Intent i=new Intent(MainActivity.this,About.class);
            startActivity(i);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in successful!", Toast.LENGTH_LONG).show();
                showAllOldMessages();
            } else {
                Toast.makeText(this, "Sign in failed, please try again later", Toast.LENGTH_LONG).show();

                finish();
            }
        }
    }


    public void runTask() {
        if (isNetworkAvailable()) {
            showAllOldMessages();
        } else {
            snackbar = Snackbar.make(findViewById(R.id.activity_main), "Network Unavailable!", Snackbar.LENGTH_LONG).setAction("retry", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    runTask();

                }
            });
            snackbar.show();

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

     void showAllOldMessages() {
         loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        adapter = new MessageAdapter(this, ChatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReference());
        listView.setAdapter(adapter);

    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }


    public void serviceStart()
    {
        Intent intent=new Intent(this,NotificationService.class);
        startService(intent);
    }
    public class AsynTask extends AsyncTask<String,String,String> {
        private FirebaseListAdapter<ChatMessage> adapter;
        MainActivity activity;
        @Override
        protected void onPreExecute(){
            activity=new MainActivity();

        }

        @Override
        protected String doInBackground(String... strings) {

           // new thread().start();
            //showAllOldMessages();
            /*for(int i=0;i<5;i++)
            {try {
                Thread.sleep(3000);
                sendMeNotification(i,"test1","test message");
            } catch(Exception e)
                {}


            }*/
            return null;
        }
        @Override
        protected void onPostExecute(String result)
        {
            //  Toast.makeText(activity,"AsyncTask stopped",Toast.LENGTH_SHORT).show();
        }

    }
  /*  public class thread extends Thread {
        public void run() {
            new MessageAdapter(MainActivity.this, ChatMessage.class, R.layout.item_in_message,
                    FirebaseDatabase.getInstance().getReference());
           /* ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    ChatMessage newVal = dataSnapshot.getValue(ChatMessage.class);
                    Log.e(" name="+newVal.getMessageUser()+" message ="+newVal.getMessageText(),"log");
                    //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();("Author: " + newVal.getMessageUser());
                    //System.out.println("Title: " + newVal.getMessageText());
                    //System.out.println("Previous Post ID: " + prevChildKey);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(3000);
                    sendMeNotification(i, "test1", "test message");
                } catch (Exception e) {
                }

            }
        }
    }*/
    public void sendMeNotification(long date,String userName,String message) {

        //  model=new ChatMessage();
        if (!FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(userName)) {
            //Toast.makeText(this,"fire="+FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+" regular ="+userName,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Lets Chat")
                    .setContentText(userName + " : " + message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify((int) (date), notificationBuilder.build());
        }
    }
}

