package com.example.new_dialog_architecture

import android.app.Activity
import android.os.Bundle
import android.webkit.WebView

class WebviewActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_webview)
        val webView: WebView = findViewById(R.id.webView1)
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("www.google.com")
    }
}
