package com.example.appusb20

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var usbManager: UsbManager
    private lateinit var permissionIntent: PendingIntent
    private val ACTION_USB_PERMISSION = "com.example.appusb20.USB_PERMISSION"
    private lateinit var btnDetectUsb: Button
    private lateinit var miTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usbManager = getSystemService(USB_SERVICE) as UsbManager
        permissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE)

        miTextView = findViewById<TextView>(R.id.mi_text_view)
        btnDetectUsb = findViewById(R.id.btn_detect_usb)
        btnDetectUsb.setOnClickListener {
            detectarDispositivosUsb()
        }
    }

    private fun detectarDispositivosUsb() {
        var hola="";
        val deviceList = usbManager.deviceList
        val deviceIterator = deviceList.values.iterator()
        while (deviceIterator.hasNext()) {
            val device = deviceIterator.next()
            usbManager.requestPermission(device, permissionIntent)
            if (usbManager.hasPermission(device)) {
                // Aquí puedes realizar operaciones con el dispositivo USB
                // Log.d("USB", "Dispositivo conectado: ${device.deviceName}")

            } else {
                Log.d("USB", "Permiso denegado para el dispositivo: ${device.deviceName}")
                //Toast.makeText(this, "Permiso denegado para el dispositivo: ${device.deviceName}", Toast.LENGTH_SHORT).show()
            }
            hola+="Dispositivo conectado! \nNombre: ${device.deviceName}, Id:${device.deviceId} \n\n"
           // miTextView.setText("Dispositivo conectado: Nombre: ${device.deviceName} \n")
            //Toast.makeText(this, "Dispositivo conectado: Nombre: ${device.deviceName}", Toast.LENGTH_SHORT).show()

        }
        miTextView.setText(hola)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        registerReceiver(usbReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(usbReceiver)
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device = intent?.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (intent?.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED,
                            false
                        ) == true
                    ) {
                        if (device != null) {

                            // Aquí puedes realizar operaciones con el dispositivo USB
                            Log.d(
                                "USB",
                                "Permiso concedido para el dispositivo: ${device.deviceName}"
                            )
                        } else {
                            Log.d(
                                "USB",
                                "Permiso denegado para el dispositivo: ${device?.deviceName}"
                            )

                        }
                    }
                }
            }
        }
    }
}
