package com.example.messagemaster

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
private const val ACTION_SMS = "com.example.messagemaster.action.SMS"
private const val ACTION_WHATSAPP = "com.example.messagemaster.action.WHATSAPP"

// TODO: Rename parameters
private const val MESSAGE = "com.example.messagemaster.extra.PARAM1"
private const val COUNT = "com.example.messagemaster.extra.PARAM2"
private const val MOBILE_NUMBER = "com.example.messagemaster.extra.PARAM3"
class MySMSservice : IntentService("MySMSservice") {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_SMS -> {
                val message = intent.getStringExtra(MESSAGE)
                val count = intent.getStringExtra(COUNT)
                val mobile_number:Array<String> = intent.getStringArrayExtra(MOBILE_NUMBER) as Array<String>
                handleActionSMS(message,count,mobile_number)
            }
            ACTION_WHATSAPP -> {
                val message = intent.getStringExtra(MESSAGE)
                val count = intent.getStringExtra(COUNT)
                val mobile_number: Array<String> = intent.getStringArrayExtra(MOBILE_NUMBER) as Array<String>
                handleActionWHATSAPP(message!!,count!!,mobile_number)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun handleActionSMS(param1: String?, param2: String?, mobile_number: Array<String>) {
        val message:String= param1.toString()
        val recipients:Array<String> = mobile_number
        //val mysmsmanager:SmsManager=SmsManager.getDefault()
        val mysmsmanager=getSystemService(SmsManager::class.java)
        for( i:String in recipients)
        { mysmsmanager.sendTextMessage(i,null,message,null,null)
          sendBroadCastMessage("Result:1 $i")}
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionWHATSAPP(message: String, count: String, mobile_number: Array<String>) {
        try {
            val packageManager=applicationContext.packageManager
            if(mobile_number.isNotEmpty()) {
                for (j in mobile_number) {
                    for (i in 1..count.toInt()) {
                        val url =
                            Uri.parse("https://api.whatsapp.com/send?phone=$j&text=$message")
                        val whatsappIntent = Intent(Intent.ACTION_VIEW,url)
                        whatsappIntent.setPackage("com.whatsapp")
                        //whatsappIntent.setData(Uri.parse(url))
                        whatsappIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        if (whatsappIntent.resolveActivity(packageManager) != null) {
                            applicationContext.startActivity(whatsappIntent)
                            Thread.sleep(10000)
                            sendBroadCastMessage("Result: $j")
                        }
                        else
                            sendBroadCastMessage("Result: Whatsapp is not installed")
                    }
                }
                sendBroadCastMessage("ALL MESSAGES HAVE BEEN DELIVERED")
            }



        }catch (e:java.lang.Exception)
        {
            Toast.makeText(this,"Failed ${e.toString()}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendBroadCastMessage(message:String)
    {
        val localIntent=Intent("my.own.broadcast")
        localIntent.putExtra("result",message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionSMS(context: Context, message: String, count: String, mobile: Array<String>  ) {

            val numbersArray:Array<String> = mobile
            val intent = Intent(context, MySMSservice::class.java).apply {
                action = ACTION_SMS
                putExtra(MESSAGE, message)
                putExtra(COUNT, count)
                putExtra(MOBILE_NUMBER,numbersArray)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionWHATSAPP(context: Context, message: String, count: String,mobile: Array<String>  ) {


            val numbersArray:Array<String> = mobile
            val intent = Intent(context, MySMSservice::class.java).apply {
                action = ACTION_WHATSAPP
                putExtra(MESSAGE, message)
                putExtra(COUNT, count)
                putExtra(MOBILE_NUMBER,numbersArray)
            }
            context.startService(intent)
        }
    }
}