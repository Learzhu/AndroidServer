package com.learzhu.androidserver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.learzhu.androidserver.app.Constants
import com.learzhu.androidserver.server.OnServerChangeListener
import com.learzhu.androidserver.server.ServerPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity(), OnServerChangeListener, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_start -> startServer()
            R.id.tv_stop -> stopServer()
            R.id.fab -> Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

        }
    }

    private val REQUEST_WRITE_EXTERNAL_STORAGE = 10

    private val TAG = "AndroidServerApp"
    var server = null
    //    var serverPresenter = null
    lateinit var mServerPresenter: ServerPresenter

    lateinit var mStartTv: TextView
    lateinit var mStopTv: TextView
    lateinit var mMessageTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mServerPresenter = ServerPresenter(this, this)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
//        tv_start.setOnClickListener { startServer() }
//        tv_start.setOnClickListener { view -> stopServer() }
        tv_start.performClick()
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }
        tv_start.setOnClickListener(this)
        tv_stop.setOnClickListener(this)
        fab.setOnClickListener(this)
    }

    private fun stopServer() {
        mServerPresenter.stopServer(this)
    }

    /**
     * 开启一个android端的服务
     */
    private fun startServer() {
        mServerPresenter.startServer(this)
//        var server = AndServer.serverBuilder().inetAddress(NetUtils.getLocalIPAddress())//服务器要监听的网络地址
//            .port(Constants.PORT_SERVER) //服务器要监听的端口
//            .timeout(10, TimeUnit.SECONDS) //Socket超时时间
//            .registerHandler(Constants.GET_FILE, DownloadFileHandler()) //注册一个文件下载接口
//            .registerHandler(Constants.GET_IMAGE, DownloadImageHandler()) //注册一个图片下载接口
//            .registerHandler(Constants.POST_JSON, JsonHandler()) //注册一个Post Json接口
//            .filter(HttpCacheFilter()) //开启缓存支持
//            .listener(Server().ServerListener {
//                //服务器监听接口
//                fun onStarted() {
//                    String hostAddress =this@MainActivity..getInetAddress().getHostAddress();
//                    Log.e(TAG, "onStarted : " + hostAddress);
//                    ServerPresenter.onServerStarted(ServerService.this, hostAddress);
//                }
//
//                fun onStopped() {
//                    Log.e(TAG, "onStopped");
//                    ServerPresenter.onServerStopped(ServerService.this);
//                }
//
//                fun onError(Exception e) {
//                    Log.e(TAG, "onError : " + e.getMessage());
//                    ServerPresenter.onServerError(ServerService.this, e.getMessage());
//                }
//            })
//            .build()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onServerStarted(ipAddress: String?) {
//        mStartTv.visibility=View.VISIBLE
        tv_start.visibility = View.GONE
        tv_stop.visibility = View.VISIBLE
        Log.e(TAG, "IP Address: $ipAddress")
        if (!TextUtils.isEmpty(ipAddress)) {
            val addressList = ArrayList<String>()
            addressList.add("http://" + ipAddress + ":" + Constants.PORT_SERVER + Constants.GET_FILE)
            addressList.add("http://" + ipAddress + ":" + Constants.PORT_SERVER + Constants.GET_IMAGE)
            addressList.add("http://" + ipAddress + ":" + Constants.PORT_SERVER + Constants.POST_JSON)
            tv_message.text = TextUtils.join("\n", addressList)
        } else {
            tv_message.text = "error"
        }
    }

    override fun onServerStopped() {
        tv_start.visibility = View.VISIBLE
        tv_stop.visibility = View.GONE
        tv_message.setText("服务器停止了")
    }

    override fun onServerError(errorMessage: String?) {
        tv_start.visibility = View.VISIBLE
        tv_stop.visibility = View.GONE
        tv_message.setText(errorMessage)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_STORAGE ->
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                showToast("请开放文件写入权限")
                    finish()
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mServerPresenter.unregister(this)
    }
}
