package com.example.ejemplobroadcast

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.example.ejemplobroadcast.databinding.ActivityBatteryBinding

class BatteryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBatteryBinding
    //Objeto de la clase BroadcastReceiver:
    private lateinit var myBroadcast: MyBroadcast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Inicializar instancia de Broadcast Receiver
        myBroadcast = MyBroadcast(binding)
        binding.fabSettings.setOnClickListener{
            enabledWriteSettings()
        }
    }

    //Este método va a servir para ir a la pantalla de su celular donde se tiene el listado de apps
    // y los permisos que tienen
    private fun enabledWriteSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        //Registra el Broadcast Receiver
        registerReceiver(myBroadcast, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        registerReceiver(myBroadcast, IntentFilter(Intent.ACTION_BATTERY_LOW))
        //registerReceiver(myBroadcast, IntentFilter(Intent.ACTION_BATTERY_OKAY))
        //Crear un intent, configurarlo y resolverlo
        val intent = Intent(this, MyBroadcast::class.java)
        sendBroadcast(intent)
    }

    override fun onStop() {
        super.onStop()
        //Quitar el registro de Broadcast Receiver
        unregisterReceiver(myBroadcast)
    }
}