package com.choegu.messagetest

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.MessageFormat
import android.database.DatabaseUtils




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_start.setOnClickListener {
//            Thread{
//                val telephonyProvider = TelephonyProvider(applicationContext)
//                val smses = telephonyProvider.getSMS(TelephonyProvider.Filter.ALL).list
//                val mmses = telephonyProvider.getMms(TelephonyProvider.Filter.ALL).list
//                val threads = telephonyProvider.threads.list
//                val conversations = telephonyProvider.conversations.list
//
//                Log.d("!!!!!", "debug")
//            }.start()

//            scanMMS()
//            scanSMS()

//            val cursor = contentResolver.query(Uri.parse("content://mms-sms/conversations?simple=true"), arrayOf("*"), null, null, null)
//            DatabaseUtils.dumpCursor(cursor)
//
//            Log.d("!!!!!", "debug")

            getMessageList()
        }
    }

//    private fun getMmsText(id: String): String {
//        val partURI = Uri.parse("content://mms/part/$id")
//        var `is`: InputStream? = null
//        val sb = StringBuilder()
//        try {
//            `is` = contentResolver.openInputStream(partURI)
//            if (`is` != null) {
//                val isr = InputStreamReader(`is`, "UTF-8")
//                val reader = BufferedReader(isr)
//                var temp = reader.readLine()
//                while (temp != null) {
//                    sb.append(temp)
//                    temp = reader.readLine()
//                }
//            }
//        } catch (e: IOException) {
//        } finally {
//            if (`is` != null) {
//                try {
//                    `is`.close()
//                } catch (e: IOException) {
//                }
//
//            }
//        }
//        return sb.toString()
//    }

//    private fun getAddressNumber(id: Int): String? {
//        val selectionAdd = "msg_id=$id"
//        val uriStr = MessageFormat.format("content://mms/{0}/addr", id)
//        val uriAddress = Uri.parse(uriStr)
//        val cAdd = contentResolver.query(
//            uriAddress, null,
//            selectionAdd, null, null
//        )
//        var name: String? = null
//        if (cAdd!!.moveToFirst()) {
//            do {
//                val number = cAdd.getString(cAdd.getColumnIndex("address"))
//                if (number != null) {
//                    try {
//                        java.lang.Long.parseLong(number.replace("-", ""))
//                        name = number
//                    } catch (nfe: NumberFormatException) {
//                        if (name == null) {
//                            name = number
//                        }
//                    }
//
//                }
//            } while (cAdd.moveToNext())
//        }
//        cAdd.close()
//        return name
//    }

    //////////////////////////////////////////

    fun getMessageList() {
        val contentResolver = contentResolver
        val projection = arrayOf("_id", "ct_t", "date", "thread_id", "m_id")
        val uri = Uri.parse("content://mms-sms/conversations/")
        val query = contentResolver.query(uri, projection, null, null, "date DESC")
        DatabaseUtils.dumpCursor(query)
        query?.let {
            if (it.moveToFirst()) {
                do {
//                    val string = it.getString(it.getColumnIndex("ct_t"))
//                    if ("application/vnd.wap.multipart.related" == string) {
//                        // it's MMS
//                    } else {
//                        // it's SMS
//                        getSMS(it.getString(it.getColumnIndex("_id")))
//                    }
//                    getThreadList(it.getString(it.getColumnIndex("thread_id")))
                    val string = it.getString(it.getColumnIndex("m_id"))
                    if (string != null) {
                        // it's MMS
                        getMMS(it.getString(it.getColumnIndex("_id")))
                    } else {
                        // it's SMS
                        getSMS(it.getString(it.getColumnIndex("_id")))
                    }
                } while (it.moveToNext())
            }
        }
    }

    fun getThreadList(threadId: String) {
        val contentResolver = contentResolver
        val projection = arrayOf("_id", "ct_t", "date", "thread_id", "m_id")
        val uri = Uri.parse("content://mms-sms/conversations/$threadId")
        val query = contentResolver.query(uri, projection, null, null, null)
        DatabaseUtils.dumpCursor(query)
        query?.let {
            if (it.moveToFirst()) {
                do {
//                    val string = it.getString(it.getColumnIndex("ct_t"))
//                    if ("application/vnd.wap.multipart.related" == string) {
//                        // it's MMS
//                        getMMS(it.getString(it.getColumnIndex("_id")))
//                    } else {
//                        // it's SMS
//                        getSMS(it.getString(it.getColumnIndex("_id")))
//                    }
                    val string = it.getString(it.getColumnIndex("m_id"))
                    if (string != null) {
                        // it's MMS
                        getMMS(it.getString(it.getColumnIndex("_id")))
                    } else {
                        // it's SMS
                        getSMS(it.getString(it.getColumnIndex("_id")))
                    }
                } while (it.moveToNext())
            }
        }
    }

    fun getSMS(id: String) {
        val selection = "_id = $id"
        val uri = Uri.parse("content://sms")
        val cursor = contentResolver.query(uri, null, selection, null, null)

        cursor?.moveToFirst()

        val phone = cursor?.getString(cursor.getColumnIndex("address"))
        val type = cursor?.getInt(cursor.getColumnIndex("type"))// 2 = sent, etc.
        val date = cursor?.getString(cursor.getColumnIndex("date"))
        val body = cursor?.getString(cursor.getColumnIndex("body"))

//        DatabaseUtils.dumpCursor(cursor)
    }

    fun getMMS(mmsId: String) {
        val selectionPart = "mid=$mmsId"
        val uri = Uri.parse("content://mms/part")
        val cursor = contentResolver.query(
            uri, null,
            selectionPart, null, null
        )
        if (cursor!!.moveToFirst()) {
            do {
                val partId = cursor.getString(cursor.getColumnIndex("_id"))
                val type = cursor.getString(cursor.getColumnIndex("ct"))
                if ("text/plain" == type) {
                    val data = cursor.getString(cursor.getColumnIndex("_data"))
                    val body: String
                    if (data != null) {
                        // implementation of this method below
                        body = getMmsText(partId)
                    } else {
                        body = cursor.getString(cursor.getColumnIndex("text"))
                    }
                } else if ("image/jpeg" == type || "image/bmp" == type ||
                    "image/gif" == type || "image/jpg" == type ||
                    "image/png" == type
                ) {
                    val bitmap = getMmsImage(partId)
                }
            } while (cursor.moveToNext())
        }
    }

    fun getMmsText(id: String): String {
        val partURI = Uri.parse("content://mms/part/$id")
        var `is`: InputStream? = null
        val sb = StringBuilder()
        try {
            `is` = contentResolver.openInputStream(partURI)
            if (`is` != null) {
                val isr = InputStreamReader(`is`, "UTF-8")
                val reader = BufferedReader(isr)
                var temp: String? = reader.readLine()
                while (temp != null) {
                    sb.append(temp)
                    temp = reader.readLine()
                }
            }
        } catch (e: IOException) {
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                }

            }
        }
        return sb.toString()
    }

    private fun getMmsImage(_id: String): Bitmap? {
        val partURI = Uri.parse("content://mms/part/$_id")
        var `is`: InputStream? = null
        var bitmap: Bitmap? = null
        try {
            `is` = contentResolver.openInputStream(partURI)
            bitmap = BitmapFactory.decodeStream(`is`)
        } catch (e: IOException) {
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                }

            }
        }
        return bitmap
    }

    /////////////////////



    fun scanMMS() {
        //Initialize Box
        val uri = Uri.parse("content://mms")
        val proj = arrayOf("*")
        val cr = contentResolver

        val c = cr.query(uri, proj, null, null, null)

        if (c!!.moveToFirst()) {
            do {
                /*String[] col = c.getColumnNames();
                String str = "";
                for(int i = 0; i < col.length; i++) {
                    str = str + col[i] + ": " + c.getString(i) + ", ";
                }
                System.out.println(str);*/
                //System.out.println("--------------------MMS------------------");
                val msg = Msg(c.getString(c.getColumnIndex("_id")))
                msg.setThread(c.getString(c.getColumnIndex("thread_id")))
                msg.setDate(c.getString(c.getColumnIndex("date")))
                msg.setAddr(getMmsAddr(msg.getID()))


                ParseMMS(msg)
                //System.out.println(msg);
            } while (c.moveToNext())
        }

        c.close()
    }

    fun ParseMMS(msg: Msg) {
        val uri = Uri.parse("content://mms/part")
        val mmsId = "mid = " + msg.getID()
        val c = contentResolver.query(uri, null, mmsId, null, null)
        while (c!!.moveToNext()) {
            /*          String[] col = c.getColumnNames();
            String str = "";
            for(int i = 0; i < col.length; i++) {
                str = str + col[i] + ": " + c.getString(i) + ", ";
            }
            System.out.println(str);*/

            val pid = c.getString(c.getColumnIndex("_id"))
            val type = c.getString(c.getColumnIndex("ct"))
            if ("text/plain" == type) {
                msg.setBody(msg.getBody() + c.getString(c.getColumnIndex("text")))
            } else if (type.contains("image")) {
                msg.setImg(getMmsImg(pid))
            }


        }
        c.close()
        return
    }

    fun getMmsImg(id: String): Bitmap? {
        val uri = Uri.parse("content://mms/part/$id")
        var `in`: InputStream? = null
        var bitmap: Bitmap? = null

        try {
            `in` = contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(`in`)
            `in`?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }


        return bitmap
    }

    fun getMmsAddr(id: String) : String {
        val sel = "msg_id=$id"
        val uriString = MessageFormat.format("content://mms/{0}/addr", id)
        val uri = Uri.parse(uriString)
        val c = contentResolver.query(uri, null, sel, null, null)
        var name = ""
        while (c!!.moveToNext()) {
            /*          String[] col = c.getColumnNames();
            String str = "";
            for(int i = 0; i < col.length; i++) {
                str = str + col[i] + ": " + c.getString(i) + ", ";
            }
            System.out.println(str);*/
            val t = c.getString(c.getColumnIndex("address"))
            if (!t.contains("insert"))
                name = "$name$t "
        }
        c.close()
        return name
    }

    fun scanSMS() {
        //Initialize Box
        val uri = Uri.parse("content://sms")
        val proj = arrayOf("*")
        val cr = contentResolver

        val c = cr.query(uri, proj, null, null, null)

        if (c!!.moveToFirst()) {
            do {
                val col = c.columnNames
                var str = ""
                for (i in col.indices) {
                    str = str + col[i] + ": " + c.getString(i) + ", "
                }
                //System.out.println(str);

                println("--------------------SMS------------------")

                val msg = Msg(c.getString(c.getColumnIndex("_id")))
                msg.setDate(c.getString(c.getColumnIndex("date")))
                msg.setAddr(c.getString(c.getColumnIndex("Address")))
                msg.setBody(c.getString(c.getColumnIndex("body")))
                msg.setDirection(c.getString(c.getColumnIndex("type")))
                msg.setContact(c.getString(c.getColumnIndex("person")))
                System.out.println(msg)


            } while (c.moveToNext())
        }
        c.close()
    }
}
