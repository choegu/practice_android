package com.choegu.practicepermission;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Message {
    private static final String TAG = Message.class.getSimpleName();

    public static void readSmsMms(Context context) {

        String uriS = "content://mms-sms/conversations?simple=true";

        // Cursor cursor = getApplicationContext().getContentResolver().query(Uri.parse(uriS), null, null, null, null);

        Cursor cursor = context.getContentResolver().query(Uri.parse(uriS), null, null, null, "date DESC");



        if (cursor == null) {
            Log.d(TAG, "readSmsMms: null");
        }

        else {
            Log.d(TAG, "총수 = "+cursor.getCount());

            int k = 0;

            while (cursor.moveToNext()) {

                // String _id = cursor.getString(cursor.getColumnIndex("_id"));

                // String group_snippet = cursor.getString(cursor.getColumnIndex("group_snippet"));

                String recipient_ids = cursor.getString(cursor.getColumnIndex("recipient_ids"));



//                String name = "";
//
//                if (recipient_ids.contains(" ")) {
//
//                    ArrayList<String> al = new ArrayList<String>(Arrays.asList(recipient_ids.split(" ")));
//
//                    for (int i=0; i<al.size(); i++) al.set(i, getContactByRecipientId((long)Float.parseFloat(al.get(i))));
//
//                    Collections.sort(al, ourComparator);
//
//                    for (String s:al) name += " " + s;
//
//                    name = name.substring(1);
//
//                }
//
//                else name = getContactByRecipientId((long)Float.parseFloat(recipient_ids));



                // String snippet_cs = cursor.getString(cursor.getColumnIndex("snippet_cs"));

                String snippet = cursor.getString(cursor.getColumnIndex("snippet"));



//                // String message_count = cursor.getString(cursor.getColumnIndex("message_count"));
//
//                long dateL = cursor.getLong(cursor.getColumnIndex("date"));
//
//                String date = new SimpleDateFormat("yy-MM-dd").format(dateL);
//
//
//
//                textview.append("\n" // +(k++)+"번째 : _id="+_id
//
//                        // +"( 그룹="+group_snippet+")/"
//
//                        // +"( recipient_ids="+recipient_ids+")/"
//
//                        + name // +"이름="+name
//
//                        // +"/메시지="+snippet_cs
//
//                        + "\n" + snippet // +"/메시지="+snippet
//
//                        + " / " + date // +"/총="+message_count+"건/날짜="+date
//
//                );

            }

            //

//            writeFile(textview.getText().toString(),mRoot+ File.separator+"rpt.txt");

        }

    }
}
