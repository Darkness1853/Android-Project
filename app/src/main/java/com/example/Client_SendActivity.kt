package com.example.calculator

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import org.zeromq.ZMQException
import java.text.SimpleDateFormat
import java.util.*

class Client_SendActivity : AppCompatActivity(), LocationListener {

    private lateinit var tvLat: TextView
    private lateinit var tvLon: TextView
    private lateinit var tvAlt: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvStatus: TextView
    private lateinit var etServerIp: EditText
    private lateinit var etServerPort: EditText
    private lateinit var btnConnect: Button
    private lateinit var btnSend: Button
    private lateinit var locationManager: LocationManager

    private var currentLocation: Location? = null
    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    private var isConnect = false
    private var isSending = false
    private var sendCounter = 0

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            tvTime.text = "Время: ${timeFormat.format(Date())}"
            handler.postDelayed(this, 1000)
        }
    }

    private val sendLocationRunnable = object : Runnable {
        override fun run() {
            if (isSending && isConnect && currentLocation != null) {
                sendLocation()
                handler.postDelayed(this, 2000)
            } else if (isSending && (!isConnect || currentLocation == null)) {
                handler.postDelayed(this, 2000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_send)

        tvLat = findViewById(R.id.tv_Lat)
        tvLon = findViewById(R.id.tv_Lon)
        tvAlt = findViewById(R.id.tv_Alt)
        tvTime = findViewById(R.id.tv_Time)
        tvStatus = findViewById(R.id.tv_status)
        etServerIp = findViewById(R.id.et_server_ip)
        etServerPort = findViewById(R.id.et_server_port)
        btnConnect = findViewById(R.id.btn_connect)
        btnSend = findViewById(R.id.btnSave1)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        btnSend.isEnabled = false
        btnSend.text = "Отправить на сервер"
        tvStatus.text = "Статус: Не подключено"

        btnSend.setOnClickListener {
            if (isConnect && currentLocation != null) {
                if (isSending) {
                    stopSending()
                } else {
                    startSending()
                }
            } else if (!isConnect) {
                Toast.makeText(this, "Нет подключения к серверу", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Нет данных GPS", Toast.LENGTH_SHORT).show()
            }
        }

        btnConnect.setOnClickListener { connectToServer() }

        findViewById<Button>(R.id.GoMain).setOnClickListener {
            stopSending()
            finish()
        }
        checkPermissions()
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateTimeRunnable)
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTimeRunnable)
        handler.removeCallbacks(sendLocationRunnable)
        stopLocationUpdates()
    }

    private fun startSending() {
        isSending = true
        sendCounter = 0
        btnSend.text = "Прекратить отправку"
        handler.post(sendLocationRunnable)
    }

    private fun stopSending() {
        isSending = false
        handler.removeCallbacks(sendLocationRunnable)
        btnSend.text = "Отправить на сервер"
        tvStatus.text = "Статус: Отправка остановлена"
    }

    private fun connectToServer() {
        val ip = etServerIp.text.toString()
        val port = etServerPort.text.toString()

        if (ip.isEmpty() || port.isEmpty()) {
            Toast.makeText(this, "Введите IP и порт", Toast.LENGTH_SHORT).show()
            return
        }
        btnConnect.isEnabled = false

        Thread {
            var context: ZContext? = null
            var socket: ZMQ.Socket? = null
            var connected = false

            try {
                context = ZContext()
                socket = context.createSocket(SocketType.REQ)
                socket.receiveTimeOut = 3000
                socket.connect("tcp://$ip:$port")

                val pingJson = """{"type":"ping_client"}"""
                socket.send(pingJson)

                val reply = socket.recvStr(0)

                if (reply != null) {
                    val jsonResponse = org.json.JSONObject(reply)
                    if (jsonResponse.has("type") && jsonResponse.getString("type") == "ping_server") {
                        connected = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    socket?.close()
                    context?.destroy()
                } catch (e: Exception) { }
            }

            val finalConnected = connected
            runOnUiThread {
                btnConnect.isEnabled = true
                if (finalConnected) {
                    isConnect = true
                    btnSend.isEnabled = true
                    tvStatus.text = "Подключено к $ip:$port"
                    Toast.makeText(this, "Подключено", Toast.LENGTH_SHORT).show()
                } else {
                    isConnect = false
                    btnSend.isEnabled = false
                    tvStatus.text = "Ошибка подключения"
                    Toast.makeText(this, "Не удалось подключиться", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun sendLocation() {
        val currentSendNumber = ++sendCounter
        val location = currentLocation!!

        val json = org.json.JSONObject().apply {
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("altitude", location.altitude)
            put("current_time", timeFormat.format(Date()))
        }

        val jsonString = json.toString()

        Thread {
            var context: ZContext? = null
            var socket: ZMQ.Socket? = null

            try {
                context = ZContext()
                socket = context.createSocket(SocketType.REQ)
                socket.receiveTimeOut = 5000
                socket.connect("tcp://${etServerIp.text}:${etServerPort.text}")

                socket.send(jsonString)
                val reply = socket.recvStr(0)

                if (reply != null) {
                    val jsonResponse = org.json.JSONObject(reply)
                    runOnUiThread {
                        if (isSending) {
                            if (jsonResponse.has("status") && jsonResponse.getString("status") == "ok") {
                                val serverCount = if (jsonResponse.has("count")) jsonResponse.getInt("count") else currentSendNumber
                                tvStatus.text = "Статус: Отправлен #$serverCount"
                            } else {
                                tvStatus.text = "Статус: Ошибка сервера"
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        if (isSending) {
                            tvStatus.text = "Нет ответа"
                        }
                    }
                }
            } catch (e: ZMQException) {
                runOnUiThread {
                    if (isSending) {
                        tvStatus.text = "Ошибка ${e.message}"
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    if (isSending) {
                        tvStatus.text = "Ошибка ${e.message}"
                    }
                }
            } finally {
                try {
                    socket?.close()
                    context?.destroy()
                } catch (e: Exception) { }
            }
        }.start()
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        tvLat.text = String.format("Широта: %.6f", location.latitude)
        tvLon.text = String.format("Долгота: %.6f", location.longitude)
        tvAlt.text = String.format("Высота: %.1f м", location.altitude)
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    private fun checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, this)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 0f, this)

                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
                    onLocationChanged(it)
                } ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let {
                    onLocationChanged(it)
                }
            } catch (e: Exception) { }
        }
    }

    private fun stopLocationUpdates() {
        try {
            locationManager.removeUpdates(this)
        } catch (e: Exception) { }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }
}