package ru.denisokvrn.testvideo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

	public static void start(Context context, File videoFile) {
	    Intent starter = new Intent(context, MainActivity.class);
	    starter.putExtra(EXTRA_FILE, videoFile);
	    context.startActivity(starter);
	}

	private static final String EXTRA_FILE = "EXTRA_FILE";

	private Button addText;
	private Button addImage;
	private Button screenShotBtn;
	private TextView textView;
	private View progressBar;

	FFmpeg ffmpeg;

	private File inputFile;
	private File outputFile;
	private String screenShotsPath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inputFile = (File) getIntent().getSerializableExtra(EXTRA_FILE);
		outputFile = new File(getExternalFilesDir(null), "out_video.mp4");
		screenShotsPath = getExternalFilesDir(null) + "/img%02d.jpg";

		addText = (Button) findViewById(R.id.add_text);
		addImage = (Button) findViewById(R.id.add_image);
		screenShotBtn = (Button) findViewById(R.id.screenshot_btn);
		textView = (TextView) findViewById(R.id.video_path);
		progressBar = findViewById(R.id.progress_bar);

		textView.setText(inputFile.getAbsolutePath());

		addText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String cmd = String.format(Locale.getDefault(),
						"-i %s -vf drawtext=fontfile=/system/fonts/DroidSans.ttf:text='TestWittedText':fontcolor=white:fontsize=24:box=1:boxcolor=black@0.5:boxborderw=5:x=(w-text_w)/2:y=(h-text_h)/2 -codec:a copy %s",
						//"-i %s -vf transpose=2,drawtext=fontsize=80:fontfile=/system/fonts/DroidSans.ttf:fontcolor=green:text=Post:x=100.0:y=100.5 -strict -2 %s",
						inputFile.getAbsolutePath(),
						outputFile.getAbsolutePath());
				String[] command = cmd.split(" ");
				execFFmpegBinary(command);
			}
		});

		addImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			}
		});
		screenShotBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//olda screens
				//-i %s  -filter:v "select='gt(scene,0.4)',showinfo"  -f null  - 2> ffout

				//new scene:
				//"-i %s  -filter:v \"select='gt(scene,0.4)',showinfo\"",
				//new scene with screens
				//ffmpeg

/*				String cmd = String.format(Locale.getDefault(),
						//"-i %s  -filter:v \"select='gt(scene,0.4)',showinfo\"",
						inputFile.getAbsolutePath());*/
				String cmd = "-i " + inputFile + " -filter:v select='gt(scene,0.3)',showinfo -vsync 0 "+screenShotsPath;

						String[] command = cmd.split(" ");
				execFFmpegBinary(command);

			}
		});

		ffmpeg = FFmpeg.getInstance(this);
		loadFFMpegBinary();
	}

	private void loadFFMpegBinary() {
		try {
			ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
				@Override
				public void onFailure() {
					Toast.makeText(MainActivity.this, "fail init", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (FFmpegNotSupportedException e) {
			Toast.makeText(MainActivity.this, "fail init", Toast.LENGTH_SHORT).show();
		}
	}

	private void execFFmpegBinary(final String[] command) {
		try {
			ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
				@Override
				public void onFailure(String s) {
					textView.setText("Failure decode");
					progressBar.setVisibility(View.INVISIBLE);
					Log.d("TAGFFMPEG", "process on failure " + s);
					//Toast.makeText(MainActivity.this, "process onFailure", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess(String s) {
					progressBar.setVisibility(View.INVISIBLE);
					textView.setText("Success decode " + "file path");
					Log.d("TAGFFMPEG", "process on success " + s);
					//Toast.makeText(MainActivity.this, "process onSuccess", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onProgress(String s) {
					Log.d("TAGFFMPEG", "process on progress " + s);
					//Toast.makeText(MainActivity.this, "process progress\n" + s, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onStart() {
					progressBar.setVisibility(View.VISIBLE);
					Log.d("TAGFFMPEG", "process onstart");
					//Toast.makeText(MainActivity.this, "process onStart", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFinish() {
					progressBar.setVisibility(View.INVISIBLE);
					Log.d("TAGFFMPEG", "process onFinish");
					//Toast.makeText(MainActivity.this, "process onFinish", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (FFmpegCommandAlreadyRunningException e) {
			// do nothing for now
		}
	}
}
