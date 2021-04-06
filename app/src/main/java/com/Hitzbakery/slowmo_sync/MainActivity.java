package com.Hitzbakery.slowmo_sync;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.*;


public class MainActivity extends AppCompatActivity {

    private Button btnPlay,btnStop,btnPause,loadbtn;
    private RadioGroup radioGroup_import,radioGroup_Export;

    private RadioButton btn24Fps,btn30Fps,btn60Fps,btn120Fps,
            btnExport24Fps,btnExport30Fps,importTotal,exportTotal,importRadio,exportRadio;

    private SeekBar seekBar;
    private  TextView remainingTimeLabel,elapsedTtimeLabel;
    private MediaPlayer mediaPlayer,mediaPlayer2;
    private Switch addBeeps;
    private int totalTime;
    private float speed = 1f;
    float exportFps = 1f;
    float importFps = 1f;
    private boolean beeps = false;
    private Uri audio;
    public int requestCode =1;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //AppCenter
        AppCenter.start(getApplication(), "784e7b7d-2fa2-4d16-8af6-daf29527caf1",
                Analytics.class, Crashes.class);

        audio = audio.EMPTY;



        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnStop = (Button) findViewById(R.id.btnStop);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        remainingTimeLabel = (TextView) findViewById(R.id.remainingTimeLabel);
        elapsedTtimeLabel = (TextView) findViewById(R.id.elapsedTtimeLabel);
        btn24Fps = (RadioButton) findViewById(R.id.btn24Fps);
        btn30Fps = (RadioButton) findViewById(R.id.btn30Fps);
        btn60Fps = (RadioButton) findViewById(R.id.btn60Fps);
        btn120Fps = (RadioButton) findViewById(R.id.btn120Fps);
        btnExport24Fps = (RadioButton) findViewById(R.id.btnExport24Fps);
        btnExport30Fps = (RadioButton) findViewById(R.id.btnExport30Fps);
        radioGroup_import = (RadioGroup)findViewById(R.id.radioGroup_import);
        radioGroup_Export = (RadioGroup) findViewById(R.id.radioGroup_export);
        addBeeps = (Switch) findViewById(R.id.addBeeps);
        loadbtn = (Button) findViewById(R.id.loadbtn);

        seekBar.setEnabled(false);






        ///////////// Switch button Toast \\\\\\\\\\\\\\\\\


        addBeeps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    beeps = true;
                    Toast.makeText(MainActivity.this,"beeps is activated",Toast.LENGTH_SHORT).show();
                }else
                {
                    beeps = false;
                    Toast.makeText(MainActivity.this,"beeps is not activated",Toast.LENGTH_SHORT).show();
                }
            }
        });



        loadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mediaPlayer != null)
                {
                    stopPlayer();
                    elapsedTtimeLabel.setText("0:00");
                    remainingTimeLabel.setText("-0:00");
                    seekBar.setProgress(0);
                    seekBar.setEnabled(false);

                    btn24Fps.setEnabled(true);
                    btn30Fps.setEnabled(true);
                    btn60Fps.setEnabled(true);
                    btn120Fps.setEnabled(true);
                    btnExport24Fps.setEnabled(true);
                    btnExport30Fps.setEnabled(true);

                    radioGroup_import.clearCheck();
                    radioGroup_Export.clearCheck();
/*
        audio = audio.EMPTY;
*/
                }


                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                startActivityForResult(intent,1);



            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){


                //the selected audio.
                audio = data.getData();
/*
                Toast.makeText(this,audio.toString(),Toast.LENGTH_SHORT).show();
*/
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
















/////// SET THE SPEED VALUE FOR THE MEDIA PLAYER \\\\\\\\\\\\\\\

    public void importFpsCheckBtn (View view)
    {

        int importRadioId = radioGroup_import.getCheckedRadioButtonId();
        importRadio = findViewById(importRadioId);
        Toast.makeText(this,"Selected shooting FPS rate: " + importRadio.getText(),Toast.LENGTH_SHORT).show();

        importFps = Float.valueOf(importRadio.getText().toString());


    }


    public void exportFpsCheckBtn (View view)
    {

        int exportRadioId = radioGroup_Export.getCheckedRadioButtonId();
        exportRadio = findViewById(exportRadioId);
        Toast.makeText(this,"your export FPS rate: " + exportRadio.getText(),Toast.LENGTH_SHORT).show();

        exportFps = Float.valueOf(exportRadio.getText().toString());


    }


    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition  = msg.what;
            seekBar.setProgress(currentPosition);

            //Update Labels
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTtimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime - currentPosition);
            remainingTimeLabel.setText("-" + remainingTime);



        }
    };




    public String createTimeLabel (int time)
    {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10 ) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;



    }





////// Media Player buns ///////

    public void play (View v)
    {


        try {


            if (exportFps == 1f && importFps != 1f)
            {
                Toast.makeText(MainActivity.this,"Please choose your export FPS",Toast.LENGTH_SHORT).show();

            }
            if (importFps ==1f && exportFps !=1f )
            {
                Toast.makeText(MainActivity.this,"Please choose your import FPS",Toast.LENGTH_SHORT).show();

            }
            if (exportFps ==1f && importFps == 1f)
            {
                Toast.makeText(MainActivity.this,"Please choose your FPS settings ",Toast.LENGTH_SHORT).show();
            }
            if (exportFps != 1f && exportFps != 1f)
            {
                speed = importFps / exportFps;

                if (exportFps != 1f && exportFps != 1f && audio == audio.EMPTY)
                {
                    Toast.makeText(MainActivity.this,"Please choose song to load ",Toast.LENGTH_SHORT).show();
                    return;

                }

                if (beeps == true && mediaPlayer == null)
                {



                    mediaPlayer2 = MediaPlayer.create(this,R.raw.threebeeps);

                    mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer2.release();
                            mediaPlayer = MediaPlayer.create(MainActivity.this,audio);
                            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setPitch(speed));
                            totalTime = mediaPlayer.getDuration();
                            seekBar.setEnabled(true);

                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    stopPlayer();
                                }
                            });
                            mediaPlayer.start();
                        }


                    });
                    mediaPlayer2.start();

                }

                if ( beeps == false && mediaPlayer == null)
                {
                    seekBar.setEnabled(true);
                    mediaPlayer = MediaPlayer.create(this,audio);
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setPitch(speed));
                    totalTime = mediaPlayer.getDuration();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopPlayer();
                        }
                    });
                    mediaPlayer.start();
                }
                if (mediaPlayer !=null && beeps ==true)
                {
                    seekBar.setEnabled(true);

                    mediaPlayer2 = MediaPlayer.create(this,R.raw.threebeeps);
                    mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer2.release();
                            mediaPlayer.start();
                        }
                    });mediaPlayer2.start();

                }


                if (mediaPlayer != null && beeps == false)
                {
                    mediaPlayer.start();
                }



                btn24Fps.setEnabled(false);
                btn30Fps.setEnabled(false);
                btn60Fps.setEnabled(false);
                btn120Fps.setEnabled(false);
                btnExport24Fps.setEnabled(false);
                btnExport30Fps.setEnabled(false);
            }
        }
        catch (Exception e){


        }










        //SeekBar

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(totalTime);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                {
/*
                    if (mediaPlayer == null )
                    {
                        mediaPlayer.seekTo(progress);
                        seekBar.setProgress(progress);


                    }
*/
                    if (mediaPlayer!= null)
                    {
                        mediaPlayer.seekTo(progress);
                        seekBar.setProgress(progress);
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // Thread (Update seekbar and Time label)

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null )
                {
                    try
                    {
                        Message msg = new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);

                        Thread.sleep(300);

                    }catch (InterruptedException e)
                    {


                    }
                }
            }
        }).start();
    }


    public void pause (View v)
    {
/*
        seekBar.setEnabled(false);
*/

        if (mediaPlayer != null)
        {
            mediaPlayer.pause();
        }
    }

    public  void stop (View v)
    {
        stopPlayer();

        elapsedTtimeLabel.setText("0:00");
        remainingTimeLabel.setText("-0:00");
        seekBar.setProgress(0);
        seekBar.setEnabled(false);

        btn24Fps.setEnabled(true);
        btn30Fps.setEnabled(true);
        btn60Fps.setEnabled(true);
        btn120Fps.setEnabled(true);
        btnExport24Fps.setEnabled(true);
        btnExport30Fps.setEnabled(true);

        radioGroup_import.clearCheck();
        radioGroup_Export.clearCheck();
/*
        audio = audio.EMPTY;
*/



    }

    private void stopPlayer()
    {
        if (mediaPlayer !=null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
            seekBar.setProgress(0);


        }
        if (mediaPlayer2 !=null)
        {
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
}
