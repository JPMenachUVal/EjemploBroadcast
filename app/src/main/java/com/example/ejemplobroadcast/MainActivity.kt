package com.example.ejemplobroadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.ejemplobroadcast.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech

    private var cont = 0

    private lateinit var binding: ActivityMainBinding

    //Variable que sirva para configurar un broadcastReceiver
    //En este caso particular para comunicarse con el sistema y
    //saber si está en modo avión
    private val getAirplaneMode = object: BroadcastReceiver() {
        //Al momento de configurar un BroadcastReceiver es
        // fundamental sobreescribir el método llamado onReceive
        // porque nos va a dar la posibilidad de recibir información del evento del sistema para
        // definir la lógica que se desee aplicar a partir de ese evento e información:



        //Configurar un BroadcastReceiver referido a tratar los cambios de tiempo en el sistema
        // se conoce como Time Tick

        override fun onReceive(/*p0*/context: Context?, /*p1*/intent: Intent?) {
            val airplaneMode = intent?.getBooleanExtra("state", false)
            airplaneMode?.let {
                val mensaje = if (it) "Modo avión activado" else "Modo avión desactivado"
                binding.txtModoAvion.text = mensaje
                //speakMessage(mensaje)
            }
        }
    }

    private val getTimeChange = object: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            //val message = "El minuto cambió"
            cont++
            var message = "l'heure a changé $cont fois."
            binding.txtTimeTick.text = message
            speakMessage(message)
        }
    }

    private val getWifiModeChange = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //EXTRA_WIFI_STATE es la llave del registro temporal del estado del WiFi
            //El valor por defecto tiene que referir a que no puede resolver el servicio, es decir:
            //WifiManager.WIFI_STATE_UNKNOWN
            val wifiMode = intent?.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN)
            wifiMode?.let {
                val message = when(it) {
                    WifiManager.WIFI_STATE_ENABLED -> "WiFi habilitado"
                    WifiManager.WIFI_STATE_DISABLED -> "WiFi deshabilitado"
                    WifiManager.WIFI_STATE_UNKNOWN -> "Error con el servicio de WiFI :("
                    else -> "Cómprate un nuevo celular XD"
                }
                binding.txtModoWifi.text = message
                //speakMessage(message)
            }
        }
    }

    //Crear una variable para configurar Broadcast
    // que nos permitirá la comunicación con el
    // el servicio WiFi, se va a usar una clase propia que contiene configuraciones
    // tratamientos y controladores

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tts = TextToSpeech(this, this)
    }

    override fun onStart() {
        super.onStart()
        //Registrar BroadcastReceiver
        registerReceiver(getAirplaneMode, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        registerReceiver(getTimeChange, IntentFilter(Intent.ACTION_TIME_TICK))
        registerReceiver(getWifiModeChange, IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(getAirplaneMode)
        unregisterReceiver(getTimeChange)
        unregisterReceiver(getWifiModeChange)
    }

    private fun speakMessage(message: String) {
        var mensaje = message
        tts.speak(mensaje, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(/*p0*/status: Int) {
        //Por defecto el comando de voz está en English
        var respuesta = if (status == TextToSpeech.SUCCESS){
            tts.language = Locale.FRANCE
            //tts.language = Locale("ES")
            "Todo ha salido bien"
        } else "Algo salió mal, por favor intente más tarde"
        Toast.makeText(this,respuesta, Toast.LENGTH_SHORT).show()
    }
}