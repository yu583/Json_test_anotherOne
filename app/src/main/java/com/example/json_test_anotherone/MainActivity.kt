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

            //http接続
            /*
            //var result = ""
            val url = URL("https://localhost/api.html")
            val con = url.openConnection() as? HttpURLConnection
            con?.let{
                it.connectTimeout = 1000
                it.readTimeout = 1000
                it.requestMethod = "GET"
                it.connect()

            }
            */

            //UIに移行
            val analysis = Analysis()
            _handler.post(analysis)
        }
    }

    //2.非同期処理後の処理
    private inner class Analysis(): Runnable{
        @UiThread
        override fun run(){
            val res = findViewById<TextView>(R.id.res)
            res.text = "テスト"

        }
    }

}