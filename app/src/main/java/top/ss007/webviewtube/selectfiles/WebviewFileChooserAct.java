package top.ss007.webviewtube.selectfiles;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import top.ss007.webviewtube.R;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

public class WebviewFileChooserAct extends AppCompatActivity {
    public static final String HTML_PAGE_FILE = "file:///android_asset/file.html";
    private static final int REQUEST_CODE_FILE_CHOOSER = 1;
    public static final int P_REQUEST_CODE = 100;

    private ValueCallback<Uri> mUploadCallbackForLowApi;
    private ValueCallback<Uri[]> mUploadCallbackForHighApi;

    private WebView mWebView;
    private WebChromeClient myWebChromeClient = new WebChromeClient() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mUploadCallbackForHighApi = filePathCallback;
            Intent intent = fileChooserParams.createIntent();
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if (intent.getType().contains("image")) {
                intent.setType("image/*");
            }
            try {
                startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER);
            } catch (ActivityNotFoundException e) {
                mUploadCallbackForHighApi = null;
                Toast.makeText(WebviewFileChooserAct.this, "您设备上没有可以选择文件的APP", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        // For 3.0+
        protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFilerChooser(uploadMsg);
        }

        //For Android 4.1+
        protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFilerChooser(uploadMsg);
        }
    };

    private WebViewClient myWebViewClient = new WebViewClient() {
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return;
            }
            //ToastUtils.showShortToast(getString(R.string.eshop_page_load_error, description));
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()) {
                //ToastUtils.showShortToast(getString(R.string.eshop_page_load_error, error.getDescription()));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_file_chooser);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, P_REQUEST_CODE);

        mWebView = findViewById(R.id.webview);
        configWebView(mWebView);
        mWebView.loadUrl(HTML_PAGE_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FILE_CHOOSER && (resultCode == RESULT_OK || resultCode == RESULT_CANCELED)) {
            afterFileChooseGoing(resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==P_REQUEST_CODE){
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i]!=PERMISSION_GRANTED){
                    Toast.makeText(WebviewFileChooserAct.this,permissions[i]+" 权限受限",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void afterFileChooseGoing(int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mUploadCallbackForHighApi == null) {
                return;
            }
            mUploadCallbackForHighApi.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            mUploadCallbackForHighApi = null;
        } else {
            if (mUploadCallbackForLowApi == null) {
                return;
            }
            Uri result = data == null ? null : data.getData();
            mUploadCallbackForLowApi.onReceiveValue(result);
            mUploadCallbackForLowApi = null;
        }
    }

    private void configWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(myWebViewClient);
        webView.setWebChromeClient(myWebChromeClient);
    }

    private void openFilerChooser(ValueCallback<Uri> uploadMsg) {
        mUploadCallbackForLowApi = uploadMsg;
        startActivityForResult(Intent.createChooser(getFilerChooserIntent(), "File Chooser"), REQUEST_CODE_FILE_CHOOSER);
    }

    private Intent getFilerChooserIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.getType().contains("image")) {
            intent.setType("image/*");
        }
        return intent;
    }
}
