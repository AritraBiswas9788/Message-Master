package com.example.messagemaster

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.onegravity.contactpicker.contact.Contact
import com.onegravity.contactpicker.contact.ContactDescription
import com.onegravity.contactpicker.contact.ContactSortOrder
import com.onegravity.contactpicker.core.ContactPickerActivity
import com.onegravity.contactpicker.picture.ContactPictureType

class MainActivity : AppCompatActivity() {
    private lateinit var toggleButton: MaterialButtonToggleGroup
    private lateinit var phoneEntryField:TextInputEditText
    private lateinit var button:Button
    private lateinit var listView: ListView
    private lateinit var contactListImage:ImageView
    private lateinit var mailCheckBox: CheckBox
    private lateinit var numCheckBox: CheckBox

    private lateinit var numBlock:ConstraintLayout
    private lateinit var mailBlock:ConstraintLayout
    private lateinit var mailEntryField:TextInputEditText
    private lateinit var mailButton:Button
    private lateinit var mailList:ListView
    private lateinit var messageButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        contactListImage=findViewById(R.id.contactlist)
        button=findViewById(R.id.button)
        toggleButton=findViewById(R.id.materialButtonToggleGroup)
        phoneEntryField=findViewById(R.id.PhoneEntryField)
        listView=findViewById(R.id.numList)
        numBlock=findViewById(R.id.PhoneNumbersBlock)
        mailBlock=findViewById(R.id.EmailBlock)

        mailEntryField=findViewById(R.id.EmailEntryField)
        mailButton=findViewById(R.id.mailButton)
        mailList=findViewById(R.id.mailList)
        numCheckBox=findViewById(R.id.numberCheckBox)
        mailCheckBox=findViewById(R.id.mailCheckBox)
        messageButton=findViewById(R.id.MessageButton)

        val phonenolist= mutableListOf<String>()
        button.setOnClickListener {
            if(phoneEntryField.text.toString().isNotEmpty()) {

                listView.background=resources.getDrawable(R.drawable.listbg)
                val recipientslist: String = phoneEntryField.text.toString()
                val recipient: Array<String> = recipientslist.split(",").toTypedArray()
                for (i: String in recipient) {
                    phonenolist.add(i.trim())
                }

                phonenolist.reverse()
                val ListAdapter =
                    ArrayAdapter(this, android.R.layout.simple_list_item_1, phonenolist)
                listView.adapter = ListAdapter
                phoneEntryField.text!!.clear()
            }
            else
                Toast.makeText(this,"No Numbers Entered",Toast.LENGTH_SHORT).show()


        }

        numCheckBox.setOnClickListener {
            if(numCheckBox.isChecked) {
                numBlock.visibility = View.VISIBLE
                val dp:Float = 50.0f
                val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    resources.displayMetrics
                )
                val params=mailBlock.layoutParams as ViewGroup.MarginLayoutParams
                //ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) linearLayoutRv.getLayoutParams();
                params.setMargins(0,0,0,0 )
                mailBlock.layoutParams = params
            }
            else
            {numBlock.visibility=View.GONE
                val dp:Float = 50.0f
                val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    resources.displayMetrics
                )
                val params=mailBlock.layoutParams as ViewGroup.MarginLayoutParams
                //ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) linearLayoutRv.getLayoutParams();
                params.setMargins(0,px.toInt(),0,0 )
                mailBlock.layoutParams = params
            }
        }

        mailCheckBox.setOnClickListener {
            if(mailCheckBox.isChecked)
            {
                mailBlock.visibility=View.VISIBLE
            }
            else
                mailBlock.visibility=View.GONE
        }

        var emailList = mutableListOf<String>()
        mailButton.setOnClickListener {
            if(mailEntryField.text.toString().isNotEmpty())
            {
                mailList.background=resources.getDrawable(R.drawable.listbg)
                val addressList:String = mailEntryField.text.toString()
                val address:Array<String> = addressList.split(",").toTypedArray()
                for(i in address)
                {
                    emailList.add(i.trim())
                }
                emailList.reverse()
                val Adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,emailList)
                mailList.adapter=Adapter
                mailEntryField.text!!.clear()
            }
            else
                Toast.makeText(this,"No Emails Entered",Toast.LENGTH_SHORT).show()

        }

        contactListImage.setOnClickListener {
            opencontactlist()
        }


        toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(isChecked){
                when(checkedId)
                {R.id.AddOnce -> { //
                    // once things
                    contactListImage.visibility=View.GONE
                    phoneEntryField.inputType= InputType.TYPE_CLASS_NUMBER
                    createAlertDialog("This setting allows you to enter the phone numbers one by one in the input field. After entering every number press the arrow to button to register the number.","ADD NUMBERS")

                    }
                 R.id.AddList -> {//list things

                     phoneEntryField.inputType=InputType.TYPE_CLASS_TEXT
                     contactListImage.visibility=View.VISIBLE

                     createAlertDialog("This setting allows the user to either choose a list of contacts from the contacts or Enter a comma separated list of numbers in the field.","ADD NUMBER LIST")
                    }
                }
            }
        }
        messageButton.setOnClickListener {
            val intent = Intent(this,messageActivity::class.java)

            intent.putStringArrayListExtra("numbers",getStringArrayList(phonenolist))
            intent.putStringArrayListExtra("emails",getStringArrayList(emailList))
            startActivity(intent)
        }

    }
    private fun getStringArrayList(list:MutableList<String>):ArrayList<String>
    {
        val retArrayList= arrayListOf<String>()
        for(i in list)
            retArrayList.add(i)
        return retArrayList
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
    private fun opencontactlist() {
        val res = askPermission(0)
        if (res) {
            startIntent()
        }
    }

    fun startIntent() {
        val intent: Intent = Intent(
            this,
            ContactPickerActivity::class.java
        )
            .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name)
            .putExtra(
                ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION,
                ContactDescription.ADDRESS.name
            ).putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, true)
            .putExtra(ContactPickerActivity.EXTRA_SELECT_CONTACTS_LIMIT, 0)
            .putExtra(ContactPickerActivity.EXTRA_ONLY_CONTACTS_WITH_PHONE, false).putExtra(
                ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE,
                ContactsContract.CommonDataKinds.Email.TYPE_WORK
            ).putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.hasExtra(
                ContactPickerActivity.RESULT_CONTACT_DATA
            )
        ) {

            // we got a result from the contact picker

            // process contacts
            var mainstring:String
            if(phoneEntryField.text!!.isEmpty())
            {
                mainstring=""}
            else
            {
                mainstring=","
            }
            val contacts =
                data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA) as List<Contact>?
            for (contact in contacts!!) {
                var maincontacts:String=contact.mapPhone.values.toString()
                maincontacts=maincontacts.substring(1)
                var i:Int=maincontacts.length
                maincontacts=maincontacts.substring(0,i-1)
                mainstring = mainstring+maincontacts+","
            }
            var i=mainstring.length
            mainstring=mainstring.substring(0,i-1)
            phoneEntryField.append(mainstring)
            // process groups
//            val groups =
//                data.getSerializableExtra(ContactPickerActivity.RESULT_GROUP_DATA) as List<Group>?
//            for (group in groups) {
//
//                val layout1=layoutInflater.inflate(R.layout.error_toast_layout,findViewById(R.id.view_layout_of_toast1))
//                val toast1: Toast = Toast(this)
//                toast1.view=layout1
//                val txtmsg1: TextView =layout1.findViewById(R.id.textview_toast1)
//                txtmsg1.setText("You Have Not Chosen Any Contact")
//                toast1.duration.toShort()
//                toast1.show()
//            }
        }
        else{
            val layout1=layoutInflater.inflate(R.layout.error_toast_layout,findViewById(R.id.view_layout_of_toast1))
            val toast1: Toast = Toast(this)
            toast1.view=layout1
            val txtmsg1: TextView =layout1.findViewById(R.id.textview_toast1)
            txtmsg1.setText("You Have Not Chosen Any Contact")
            toast1.duration.toShort()
            toast1.show()
        }
    }

    //asking permission from the user to access to contact
    fun askPermission(identification: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    identification
                )
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    //Accordng to the permission result back and perform what data is selected from the user
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(
                    this,
                    "Go Directive wants to access permission..Please give to access permission by click on + -> allow",
                    Toast.LENGTH_LONG
                ).show()
            }
            1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(
                    this,
                    "Go Directive wants to access permission..Please give to access permission by click on + -> allow",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


}