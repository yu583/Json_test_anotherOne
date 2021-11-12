package com.example.json_test_anotherone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.core.os.HandlerCompat
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors



//非同期処理はExecutorを使用
//http接続はHttpURLConnectionを使用
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val bt = findViewById<Button>(R.id.rm_click)
        val listener = StartListener()
        bt.setOnClickListener(listener)

    }

    //イベントハンドラ処理
    private inner class StartListener : View.OnClickListener{
        @UiThread
        override fun onClick(view: View){
            //非同期処理の実行
            val handler = HandlerCompat.createAsync(mainLooper)
            val connection =  Connection(handler)
            val executeService = Executors.newSingleThreadExecutor()
            executeService.submit(connection)
        }
    }

    //1,非同期処理
    private inner class  Connection(handler : Handler):Runnable{
        private val _handler = handler
        @WorkerThread
        override fun run(){
            //Json作成
            var json = roommake()
            val sendDataJson = json.toString()
            val bodyData = sendDataJson.toByteArray()

            //http接続
            var result =""
            val url = URL("http://192.168.0.3:8080/api/room")
            val con = url.openConnection() as? HttpURLConnection
            //POST通信
            con?.let{
                it.connectTimeout = 1000
                it.readTimeout = 1000
                it.requestMethod = "POST"
                it.doOutput = true
                it.setFixedLengthStreamingMode(bodyData.size)
                it.setRequestProperty("Content-type", "application/json; charset=utf-8")
                it.connect()
                //Bodyの書き込み
                val outputStream = it.outputStream
                outputStream.write(bodyData)
                outputStream.flush()
                outputStream.close()
                //Responseの取り出し
                val stream = it.inputStream
                result = changestring(stream)
                //終了
                it.disconnect()
            }
            //GET通信

            //UIに移行
            val analysis = Analysis(result)
            _handler.post(analysis)
        }
    }

    //2.非同期処理後の処理
    private inner class Analysis(result: String): Runnable{
        private val _result =result
        @UiThread
        override fun run(){
            //書き込み
            val res = findViewById<TextView>(R.id.res)
            res.text = "test"

        }
    }

    //InputStreamオブジェクトをStringに変換
    private fun changestring(stream: InputStream): String{
        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(stream,"UTF-8"))
        var line = reader.readLine()
        while(line != null){
            sb.append(line)
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }
    //部屋の作成
    private fun roommake():org.json.JSONObject{
        var mjson = org.json.JSONObject()
        mjson.put("name","kari")
        mjson.put("is_personal",true)

        var tjson = org.json.JSONArray()
        tjson.put("test")
        mjson.put("tags",tjson)
        return mjson
    }

    //タグの作成
    private fun tagmake():org.json.JSONObject{
        var json = org.json.JSONObject()
        json.put("id",810)
        json.put("name","kari")
        return json
    }
}