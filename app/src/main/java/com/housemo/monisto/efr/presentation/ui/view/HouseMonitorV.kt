package com.housemo.monisto.efr.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.housemo.monisto.efr.presentation.app.HouseMonitorApplication
import com.housemo.monisto.efr.presentation.ui.load.HouseMonitorLoadFragment
import org.koin.android.ext.android.inject

class HouseMonitorV : Fragment(){

    private lateinit var houseMonitorPhoto: Uri
    private var houseMonitorFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val houseMonitorTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        houseMonitorFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        houseMonitorFilePathFromChrome = null
    }

    private val houseMonitorTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            houseMonitorFilePathFromChrome?.onReceiveValue(arrayOf(houseMonitorPhoto))
            houseMonitorFilePathFromChrome = null
        } else {
            houseMonitorFilePathFromChrome?.onReceiveValue(null)
            houseMonitorFilePathFromChrome = null
        }
    }

    private val houseMonitorDataStore by activityViewModels<HouseMonitorDataStore>()


    private val houseMonitorViFun by inject<HouseMonitorViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (houseMonitorDataStore.houseMonitorView.canGoBack()) {
                        houseMonitorDataStore.houseMonitorView.goBack()
                        Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "WebView can go back")
                    } else if (houseMonitorDataStore.houseMonitorViList.size > 1) {
                        Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "WebView can`t go back")
                        houseMonitorDataStore.houseMonitorViList.removeAt(houseMonitorDataStore.houseMonitorViList.lastIndex)
                        Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "WebView list size ${houseMonitorDataStore.houseMonitorViList.size}")
                        houseMonitorDataStore.houseMonitorView.destroy()
                        val previousWebView = houseMonitorDataStore.houseMonitorViList.last()
                        houseMonitorAttachWebViewToContainer(previousWebView)
                        houseMonitorDataStore.houseMonitorView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (houseMonitorDataStore.houseMonitorIsFirstCreate) {
            houseMonitorDataStore.houseMonitorIsFirstCreate = false
            houseMonitorDataStore.houseMonitorContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return houseMonitorDataStore.houseMonitorContainerView
        } else {
            return houseMonitorDataStore.houseMonitorContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "onViewCreated")
        if (houseMonitorDataStore.houseMonitorViList.isEmpty()) {
            houseMonitorDataStore.houseMonitorView = HouseMonitorVi(requireContext(), object :
                HouseMonitorCallBack {
                override fun houseMonitorHandleCreateWebWindowRequest(houseMonitorVi: HouseMonitorVi) {
                    houseMonitorDataStore.houseMonitorViList.add(houseMonitorVi)
                    Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "WebView list size = ${houseMonitorDataStore.houseMonitorViList.size}")
                    Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "CreateWebWindowRequest")
                    houseMonitorDataStore.houseMonitorView = houseMonitorVi
                    houseMonitorVi.houseMonitorSetFileChooserHandler { callback ->
                        houseMonitorHandleFileChooser(callback)
                    }
                    houseMonitorAttachWebViewToContainer(houseMonitorVi)
                }

            }, houseMonitorWindow = requireActivity().window).apply {
                houseMonitorSetFileChooserHandler { callback ->
                    houseMonitorHandleFileChooser(callback)
                }
            }
            houseMonitorDataStore.houseMonitorView.houseMonitorFLoad(arguments?.getString(
                HouseMonitorLoadFragment.HOUSE_MONITOR_D) ?: "")
//            ejvview.fLoad("www.google.com")
            houseMonitorDataStore.houseMonitorViList.add(houseMonitorDataStore.houseMonitorView)
            houseMonitorAttachWebViewToContainer(houseMonitorDataStore.houseMonitorView)
        } else {
            houseMonitorDataStore.houseMonitorViList.forEach { webView ->
                webView.houseMonitorSetFileChooserHandler { callback ->
                    houseMonitorHandleFileChooser(callback)
                }
            }
            houseMonitorDataStore.houseMonitorView = houseMonitorDataStore.houseMonitorViList.last()

            houseMonitorAttachWebViewToContainer(houseMonitorDataStore.houseMonitorView)
        }
        Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "WebView list size = ${houseMonitorDataStore.houseMonitorViList.size}")
    }

    private fun houseMonitorHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        houseMonitorFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Launching file picker")
                    houseMonitorTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "Launching camera")
                    houseMonitorPhoto = houseMonitorViFun.houseMonitorSavePhoto()
                    houseMonitorTakePhoto.launch(houseMonitorPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(HouseMonitorApplication.HOUSE_MONITOR_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                houseMonitorFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun houseMonitorAttachWebViewToContainer(w: HouseMonitorVi) {
        houseMonitorDataStore.houseMonitorContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            houseMonitorDataStore.houseMonitorContainerView.removeAllViews()
            houseMonitorDataStore.houseMonitorContainerView.addView(w)
        }
    }


}