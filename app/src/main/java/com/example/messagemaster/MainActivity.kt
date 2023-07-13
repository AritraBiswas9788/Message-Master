package com.example.messagemaster

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.widget.Button
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private lateinit var toggleButton: MaterialButtonToggleGroup
    private lateinit var phoneEntryField:TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        toggleButton=findViewById(R.id.materialButtonToggleGroup)
        phoneEntryField=findViewById(R.id.PhoneEntryField)
        toggleButton.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(isChecked){
                when(checkedId)
                {R.id.AddOnce -> { //once things
                    phoneEntryField.inputType= InputType.TYPE_CLASS_NUMBER
                    createAlertDialog("This setting allows you to enter the phone numbers one by one in the input field. After entering every number press the arrow to button to register the number.","ADD NUMBERS")

                    }
                 R.id.AddList -> {//list things
                     phoneEntryField.inputType=InputType.TYPE_CLASS_TEXT
                     createAlertDialog("This setting allows the user to either choose a list of contacts from the contacts or Enter a comma separated list of numbers in the field.","ADD NUMBER LIST")
                    }
                }
            }
        }

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

}