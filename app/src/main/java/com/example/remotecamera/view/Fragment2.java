package com.example.remotecamera.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.remotecamera.R;


public class Fragment2 extends Fragment {
    String httpLink = "http://192.168.0.124:8880/1";
    WebView webview;


    public Fragment2() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        webview = view.findViewById(R.id.webview2);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(httpLink);
        return view;
    }
}