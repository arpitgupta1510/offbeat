package com.example.chats

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
     fun loadSettings(){
         var sp=PreferenceManager.getDefaultSharedPreferences(this)
         var isNightMode=sp.getBoolean("NIGHT",false)
         if(isNightMode){
             Toast.makeText(this, "Night Mode", Toast.LENGTH_SHORT).show()
         }else{
             Toast.makeText(this, "Light Mode", Toast.LENGTH_SHORT).show()
         }
//          var checkNightInstant:CheckBoxPreference=
     }

    override fun onResume() {
        loadSettings()
        super.onResume()
    }
}