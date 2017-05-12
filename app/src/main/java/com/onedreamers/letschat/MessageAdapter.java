package com.onedreamers.letschat;

/**
 * Created by Aman gautam on 31-Mar-17.
 */

import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


public class MessageAdapter extends FirebaseListAdapter<ChatMessage> {

     MainActivity activity;
    DatabaseHelper databaseHelper;
    ArrayList<ChatMessage> arrayList;
   // FirebaseDatabase fref;
    long maxDate;


    public MessageAdapter(MainActivity activity, Class<ChatMessage> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;
        //activity=new MainActivity();


        databaseHelper=new DatabaseHelper(activity);
        arrayList=new ArrayList<>();
    }

    @Override
    public void populateView(View v, ChatMessage model, int position) {
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        TextView messageUser = (TextView) v.findViewById(R.id.message_user);
        TextView messageTime = (TextView) v.findViewById(R.id.message_time);

        messageText.setText(model.getMessageText());
        messageUser.setText(model.getMessageUser());

        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getMessageTime()));
        Cursor cursor=databaseHelper.getLast();

        while(cursor.moveToNext())
        {
            maxDate=cursor.getLong(2);
        }

        if(maxDate<model.getMessageTime()) {
          activity.sendMeNotification(maxDate,model.getMessageUser(), model.getMessageText());
            //Toast.makeText(activity,"new Message arrived",Toast.LENGTH_LONG).show();

            boolean st = databaseHelper.insertData(model.getMessageUser(), model.getMessageText(), model.getMessageTime());
            if (st == true) {

               // Toast.makeText(activity, "data inserted max =" + maxDate, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(activity, "data not inserted", Toast.LENGTH_SHORT).show();
            }
        }
        }
/*
    public void sendMeNotification1(long date,String userName,String message)
    {

        //  model=new ChatMessage();
        if(!FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(userName)) {
            //Toast.makeText(this,"fire="+FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+" regular ="+userName,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0 , intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activity)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Lets Chat")
                    .setContentText(userName+" : "+message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify((int)(date), notificationBuilder.build());
        }
    }*/
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ChatMessage chatMessage = getItem(position);
        if (chatMessage.getMessageUserId().equals(activity.getLoggedInUserName()))
            view = activity.getLayoutInflater().inflate(R.layout.item_out_message, viewGroup, false);
        else
            view = activity.getLayoutInflater().inflate(R.layout.item_in_message, viewGroup, false);

        //generating view
       // Toast.makeText(activity, "pos"+position, Toast.LENGTH_SHORT).show();


        populateView(view, chatMessage, position);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }


}

