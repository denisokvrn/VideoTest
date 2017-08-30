package ru.denisokvrn.testvideo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.File;

public class CreateVideoActivity extends AppCompatActivity {

	private CameraView cameraView;
	private Button startBtn;
	private Button stopBtn;

	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_video);
		cameraView = (CameraView) findViewById(R.id.camera_view);
		cameraView.setCameraListener(new CameraListener() {
			@Override
			public void onVideoTaken(File video) {
				super.onVideoTaken(video);
				Log.d("TAGVIDEO", "file:" + video.getAbsolutePath());
				MainActivity.start(CreateVideoActivity.this, video);
				// The File parameter is an MP4 file.
			}
		});

		startBtn = (Button) findViewById(R.id.start_btn);
		stopBtn = (Button) findViewById(R.id.stop_btn);
		startBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stopBtn.setEnabled(true);
				cameraView.startRecordingVideo();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						stopBtn.setEnabled(false);
						cameraView.stopRecordingVideo();
					}
				}, 1000 * 10);
			}
		});
		stopBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stopBtn.setEnabled(false);
				handler.removeCallbacksAndMessages(null);
				cameraView.stopRecordingVideo();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		cameraView.start();
	}

	@Override
	protected void onPause() {
		cameraView.stop();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
}
