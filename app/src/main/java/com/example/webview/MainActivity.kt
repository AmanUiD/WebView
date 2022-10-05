package com.example.webview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*


const val URL = ""

class MainActivity : AppCompatActivity() {
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @SuppressLint("SetJavaScriptEnabled", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.domStorageEnabled = true
        myWebView.loadUrl(URL)
        swipeRefreshLayout = findViewById(R.id.refresh)

        myWebView.webViewClient = object : WebViewClient() {
             // load any url in this activity
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url?.contains(URL)!!) {
                    view?.loadUrl(url)!!
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                }
                return true
            }


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                //progessbar
                progressBar.visibility = View.VISIBLE
                // refresh activity
                swipeRefreshLayout.setOnRefreshListener {
                    myWebView.reload()

                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.INVISIBLE
                swipeRefreshLayout.isRefreshing = false
                injectJavascript(view)
            }
        }
        myWebView.addJavascriptInterface(JSBridge, "Bridge")
    }
   //add javascript in android for invoke some function
    private fun injectJavascript(view: WebView?) {
        view!!.loadUrl(
            """
               javascript: (function(){
               let header = document.querySelector(".shared-global_header-markets_nav_component__siteLogoWrapper")
               header.addEventListener("click", function(){
   
              Bridge.calledFromJS()
              })
               })() 
            """
        )

    }

    object JSBridge {
        @JavascriptInterface
        fun calledFromJS() {

        }
    }

   // back button
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
            myWebView.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {

        if (myWebView.canGoBack()) {
            myWebView.goBack()
        }

    }
}



