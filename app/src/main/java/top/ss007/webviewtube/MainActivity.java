package top.ss007.webviewtube;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import top.ss007.webviewtube.playvideo.VideoPlayerAct;
import top.ss007.webviewtube.selectfiles.WebviewFileChooserAct;

public class MainActivity extends AppCompatActivity {
    private MainActivity mAct;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAct=this;
        findViewById(R.id.btn_play_video).setOnClickListener(v -> {
            startActivity(new Intent(mAct, VideoPlayerAct.class));
        });

        findViewById(R.id.btn_choose_file).setOnClickListener(v -> {
            startActivity(new Intent(mAct, WebviewFileChooserAct.class));
        });
    }


}
