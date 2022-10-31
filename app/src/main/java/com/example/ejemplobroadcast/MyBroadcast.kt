package com.example.ejemplobroadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.ejemplobroadcast.databinding.ActivityBatteryBinding
import kotlin.math.round

//Para que la clase se comporte coo broadcast receiver se debe heredar una clase abstracta
// llamada BroadcastReceiver

class MyBroadcast(
    private val bindingObject: ActivityBatteryBinding
): BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            //Intent.ACTION_BATTERY_CHANGED -> showBatteryLevel(intent)
            Intent.ACTION_BATTERY_LOW ->evaluateLowBattery(context, intent)
            Intent.ACTION_BATTERY_CHANGED -> batteryHealth(intent)
        }
    }

    private fun batteryHealth(intent: Intent) {
        showBatteryLevel(intent)
        val isOkay = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
        isOkay?.let {
            bindingObject.txtSalud.text = when (isOkay) {
                BatteryManager.BATTERY_HEALTH_DEAD -> "Batería muerta"
                BatteryManager.BATTERY_HEALTH_GOOD -> "Bateria en buen estado"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Batería con fallas"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Bateria sobrecalentada"
                BatteryManager.BATTERY_HEALTH_UNKNOWN -> "Bateria en estado desconocido"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Bateria con sobrevoltaje"
                else -> "Error, no se puede verificar la salud de la bateria"

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun evaluateLowBattery(context: Context?, intent: Intent?) {
        //El nivel bajo de batería se maneja con un dato booleano
        val lowBattery = intent?.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW,false)
        lowBattery?.let {
            bindingObject.txtMensajeBateria.text = "Alerta batería baja"
            configureScreenBrightness(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun configureScreenBrightness(context: Context?) {
        //No es posible utilizar recursos de hardware y configuraciones del sistema sin antes tener
        // sus respectivos permisos u opciones.
        if (hasWriteSettingsEnabled(context)) {
            //1. El nivel de brillo se maneja de 0 a 255
            //2. el brillo se ajusta automáticamente
            //3. por tanto, cambiar el brillo a modo manual
            val screenBrightnessLevel = 20
            Settings.System.putInt(
                context?.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )
            Settings.System.putInt(
                context?.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                screenBrightnessLevel
            )
            val percentage = screenBrightnessLevel.toDouble()/255
            Toast.makeText(context, "El porcentaje es: ${round(percentage*100)}%", Toast.LENGTH_SHORT).show()
        }else Toast.makeText(context, "No puedes configurar los settings", Toast.LENGTH_SHORT).show()
    }

    private fun showBatteryLevel(intent: Intent?) {
        //Cuando se trata del nivel de la batería, el sistema envía a través de un intent
        // un valor entero que representa el porcenjaje de batería restante
        // La batería es gestionada y configurada desde la clase BatteryManager
        val batteryLevel = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        batteryLevel?.let {
            val porcentaje = "$it% batería"
            bindingObject.txtPorcentajeBateria.text = porcentaje
            bindingObject.pbNivelBateria.progress = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasWriteSettingsEnabled(context: Context?): Boolean {
        return Settings.System.canWrite(context)
    }
}