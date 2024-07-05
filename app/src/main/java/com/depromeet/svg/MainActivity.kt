package com.depromeet.svg

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.depromeet.svg.databinding.ActivityMainBinding
import com.jkh.svgsample.getHTMLBody
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    companion object {
        const val JAVASCRIPT_OBJ = "javascript_obj"
        const val BASE_URL = "file:///android_asset/web/"
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var pd: ProgressDialog
    private lateinit var binding: ActivityMainBinding

    private fun initProgressDialog() {
        pd = ProgressDialog(this)
        pd.setCancelable(false)
        pd.isIndeterminate = true
        pd.setTitle("render svg")
        pd.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButtonActions()
        setupWebLayout()
        test()
    }

    private fun setupButtonActions() {
        initProgressDialog()
        binding.btnSendToWeb.setOnClickListener {
            binding.webView.evaluateJavascript(
                "javascript: " +
                        "updateFromAndroid(\"" + binding.etSendDataField.text + "\")",
                null
            )
        }
    }

    override fun onResume() {
        super.onResume()
        callVM()
    }

    private fun callVM() {
        val url = "https://svgshare.com/i/17vt.svg"
        try {
            viewModel.downloadFileFromServer(url)
                .observe(this, Observer { responseBody ->
                    val svgString = responseBody.string()
                    binding.webView.loadDataWithBaseURL(
                        BASE_URL, getHTMLBody(svgString), "text/html",
                        "UTF-8", null
                    )
                    pd.dismiss()
                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("JavascriptInterface")
    private fun setupWebLayout() {
        binding.webView.setInitialScale(150)
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = false
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.addJavascriptInterface(
            JavaScriptInterface(),
            JAVASCRIPT_OBJ
        )
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                injectJavaScriptFunction()
            }
        }
        binding.webView.webChromeClient = WebChromeClient()
    }

    private fun injectJavaScriptFunction() {
        val textToAndroid = "javascript: window.androidObj.textToAndroid = function(message) { " +
                JAVASCRIPT_OBJ + ".textFromWeb(message) }"
        binding.webView.loadUrl(textToAndroid)
    }

    private fun test(){
        binding.tvRedSection.setOnClickListener {
            injectJSSectionClicked("RedSection",)
        }
        binding.tvBlueSection.setOnClickListener {
            injectJSSectionClicked("BlueSection",)
        }
        binding.tvGraySection.setOnClickListener {
            injectJSSectionClicked("GraySection",)
        }
    }

    private fun injectJSSectionClicked(sectionName: String){
        val textToAndroid = "javascript: window.androidObj.handleAndroidClick('$sectionName')"
        binding.webView.loadUrl(textToAndroid)
    }


    inner class JavaScriptInterface {
        @SuppressLint("SetTextI18n")
        @JavascriptInterface
        fun textFromWeb(fromWeb: String) {
            Log.e("bbb","fromWeb : ${fromWeb}")
            runOnUiThread {
                binding.tvStateName.text = fromWeb
            }
            Toast.makeText(this@MainActivity, fromWeb, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun zoomToSection(sectionName : String){
            Log.e("bbb","fromWeb testtest : $sectionName")
            runOnUiThread {
                when(sectionName){
                    "RedSection" -> binding.webView.setInitialScale(200)
                    "GraySection" -> binding.webView.setInitialScale(200)
                    "BlueSection" -> binding.webView.setInitialScale(200)
                }
            }

        }
    }

    override fun onDestroy() {
        binding.webView.removeJavascriptInterface(JAVASCRIPT_OBJ)
        super.onDestroy()
    }
}