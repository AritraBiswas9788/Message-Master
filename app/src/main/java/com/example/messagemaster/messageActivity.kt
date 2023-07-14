package com.example.messagemaster

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.button.MaterialButtonToggleGroup
import java.net.URLEncoder
import java.util.ArrayList

class messageActivity : AppCompatActivity() {

    private lateinit var titleBox:TextView
    private lateinit var messageBox: TextView
    private lateinit var platformToggleGroup: MaterialButtonToggleGroup
    private lateinit var automateCheckBox: CheckBox
    private lateinit var sendButton: Button

    private var sms:Boolean=false
    private var email:Boolean=false
    private var whatsapp:Boolean=false
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        supportActionBar?.hide()
        val phoneList=intent.getStringArrayListExtra("numbers")!!
        val mailList =intent.getStringArrayListExtra("emails")!!

        titleBox=findViewById(R.id.TitleMessage)
        messageBox=findViewById(R.id.textMessage)
        platformToggleGroup=findViewById(R.id.PlatformSelectionToggleGroup)
        automateCheckBox=findViewById(R.id.whatsappCheckBox)
        sendButton=findViewById(R.id.SendButton)
        val intent= IntentFilter("my.own.broadcast")
        LocalBroadcastManager.getInstance(this).registerReceiver(myLocalBroadcastReceiver(),intent)
        ActivityCompat.requestPermissions(this,
            (arrayOf(android.Manifest.permission.SEND_SMS, android.Manifest.permission.READ_SMS)),
            PackageManager.PERMISSION_GRANTED
        )


        platformToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when(checkedId)
            {
                R.id.SmsButton ->{
                    sms=!sms
                }
                R.id.EmailButton ->{
                    email=!email
                }
                R.id.WhatsappButton -> {
                    whatsapp=!whatsapp
                }
            }
        }
        automateCheckBox.setOnClickListener {
            val builder: AlertDialog.Builder= AlertDialog.Builder(this)
            builder.setMessage("Automating Messages is riskier due to WhatsApp security policies. \nThis involves a process where every selected contact will be opened and the message will be sent one by one over a period of time. \nThis requires an additional permission known as Accessibility. \nDo you still want to enable this setting?")
            builder.setTitle("DISCLAIMER")
            builder.apply {
                setPositiveButton("YES") { dialog, id ->
                    showToast("Redirects to accessibility")
                    //createAlertDialog("Navigate To the Installed apps section and enable My WhatsApp Accessibility for WhatsApp Automation to work.","STEPS for Accessibility")
                    val builder: AlertDialog.Builder= AlertDialog.Builder(context)
                    builder.setMessage("Navigate To the Installed apps section and enable My WhatsApp Accessibility for WhatsApp Automation to work.")
                    builder.setTitle("STEPS for Accessibility")
                    builder.apply {
                        setPositiveButton("OK") { dialog, id ->
                            val intentAccess = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            intentAccess.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intentAccess)
                        }
                    }
                    val dialog: AlertDialog =builder.create()
                    dialog.show()

                }
                setNegativeButton("NO"){ dialogInterface: DialogInterface, i: Int ->
                    automateCheckBox.isChecked=false
                }
            }
            val dialog: AlertDialog =builder.create()
            dialog.show()
        }
        sendButton.setOnClickListener {

            if(titleBox.text.toString().isEmpty() && email)
                showToast("Title Section cannot be empty if Email is Selected!")
            else
                if(messageBox.text.toString().isEmpty())
                    showToast("Message-Box cannot be Empty!!")
                else
                    if(!email && !sms && !whatsapp)
                        showToast("Atleast One Platform must be selected!")
                    else
                    {
                        val message=messageBox.text.toString().trim()
                        val title = titleBox.text.toString().trim()
                        if(sms)
                           {if(sendSMSmessages(message,phoneList))
                               showToast("ALL SMS WERE SENT SUCCESSFULLY")}
                        if(email)
                        {
                            sendemail(title,message,getArray(mailList))
                            val builder: AlertDialog.Builder= AlertDialog.Builder(this)
                            builder.setMessage("Click OK if th E-mail process was successful!")
                            builder.setTitle("Emails Sent")
                            builder.apply {
                                setPositiveButton("OK") { dialog, id ->
                                    if(whatsapp)
                                        handleWhatsappMessage(message,phoneList,automateCheckBox.isChecked)

                                }
                            }
                            val dialog: AlertDialog =builder.create()
                            dialog.show()
                        }
                        if(!email && whatsapp)
                            handleWhatsappMessage(message,phoneList,automateCheckBox.isChecked)


                    }

        }

    }

    private fun whatsappMessage(message: String)
    {
        Log.i("wap","func called")
        val context:Context= this.applicationContext
        val packageManager =context.packageManager
        val intent:Intent = Intent(Intent.ACTION_VIEW)
        try {
            val url:String = "https://api.whatsapp.com/send?text="+ URLEncoder.encode(message,"UTF-8")
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.i("wap","check reached")
            if(intent.resolveActivity(packageManager)!=null)
            {
                Log.i("wap","check passed")
                context.startActivity(intent)
                Toast.makeText(this,"Successful",Toast.LENGTH_SHORT).show()
            }
        }
        catch (e:java.lang.Exception)
        {
            e.printStackTrace()

            Log.i("meh",e.toString())
            Toast.makeText(this,"Failed ${e.toString()}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleWhatsappMessage(message: String, phoneList: ArrayList<String>, checked: Boolean) {

        if(checked)
            MySMSservice.startActionWHATSAPP(applicationContext, "$message   " ,"1",getArray(phoneList))
        else
            whatsappMessage(message)

    }

    private fun getArray(mailList: ArrayList<String>): Array<String> {

        val retArray =Array<String>(mailList.size){index ->
            mailList[index].trim()
        }
        return retArray
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendSMSmessages(message:String, numList: ArrayList<String>):Boolean {
        try {
            val mysmsmanager = getSystemService(SmsManager::class.java)
            for (i: String in numList) {
                mysmsmanager.sendTextMessage(i, null, message, null, null)
            }
        }
        catch(e:Exception)
        {
            Log.i("smserror",e.toString())
            showToast(e.toString())
            return false
        }
        return true
    }
    private fun sendemail(title: String,message: String,recipients:Array<String>) {

        val intent= Intent(Intent.ACTION_SENDTO)
        intent.data= Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL,recipients)
        intent.putExtra(Intent.EXTRA_SUBJECT,title)
        intent.putExtra(Intent.EXTRA_TEXT,message)
        startActivity(Intent.createChooser(intent,"Choose an Email Option"))


    }


    private fun showToast(message:String)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    private fun createAlertDialog(message:String,title:String)
    {

        val builder: AlertDialog.Builder= AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setTitle(title)
        builder.apply {
            setPositiveButton("OK") { dialog, id ->
            }
        }
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }
    private class myLocalBroadcastReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val result= intent?.getStringExtra("result")
            /*if (result.equals("ALL MESSAGES HAVE BEEN DELIVERED"))
                makeNotification()*/
            Toast.makeText(context,result!!,Toast.LENGTH_SHORT).show()
        }

    }
}