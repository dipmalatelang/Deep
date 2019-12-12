package com.travel.cotravel.fragment.account.profile.ui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;


import com.travel.cotravel.BaseActivity;
import com.travel.cotravel.R;
import com.travel.cotravel.fragment.account.profile.videoTrimmer.videoTrimmer.customVideoViews.BarThumb;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewVideoActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.imgPlay)
    ImageView imgPlay;
    @BindView(R.id.seekBarVideo)
    SeekBar seekBarVideo;
    @BindView(R.id.txtVideoLength)
    TextView txtVideoLength;
    @BindView(R.id.llVideoView)
    ConstraintLayout llVideoView;
    private int mDuration = 0;
    private int mTimeVideo = 0;
    private int mStartPosition = 0;
    private int mEndPosition = 0;
    // set your max video trim seconds
    private int mMaxDuration = 10;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            String srcFile = getIntent().getStringExtra("VideoUrl");
            videoView.setVideoURI(Uri.parse(srcFile));
        }

      videoView.seekTo(100);
        videoView.setOnPreparedListener(mp -> {
                onVideoPrepared(mp);
                videoView.start();
                imgPlay.setBackgroundResource(R.drawable.ic_white_pause);
                if (seekBarVideo.getProgress() == 0) {
                    txtVideoLength.setText("00:00");
                    updateProgressBar();

            }
        });

        videoView.setOnCompletionListener(mp -> onVideoCompleted());

        imgPlay.setOnClickListener(this);

        // handle changes on seekbar for video play
        seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (videoView != null) {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    seekBarVideo.setMax(mTimeVideo * 1000);
                    seekBarVideo.setProgress(0);
                    videoView.seekTo(mStartPosition * 1000);
                    videoView.pause();
                    imgPlay.setBackgroundResource(R.drawable.ic_white_play);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                videoView.seekTo((mStartPosition * 1000) - seekBarVideo.getProgress());
            }
        });
    }

    @Override
    public void onClick(View view) {
         if (view == imgPlay) {
            if (videoView.isPlaying()) {
                if (videoView != null) {
                    videoView.pause();
                    imgPlay.setBackgroundResource(R.drawable.ic_white_play);
                }
            } else {
                if (videoView != null) {
                    videoView.start();
                    imgPlay.setBackgroundResource(R.drawable.ic_white_pause);
                    if (seekBarVideo.getProgress() == 0) {
                        txtVideoLength.setText("00:00");
                        updateProgressBar();
                    }
                }
            }
        }
    }

    private void onVideoPrepared(@NonNull MediaPlayer mp) {
        // Adjust the size of the video
        // so it fits on the screen
        //TODO manage proportion for video
        /*int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = rlVideoView.getWidth();
        int screenHeight = rlVideoView.getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        videoView.setLayoutParams(lp);*/

        mDuration = videoView.getDuration() / 1000;
        setSeekBarPosition();
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (seekBarVideo.getProgress() >= seekBarVideo.getMax()) {
                seekBarVideo.setProgress((videoView.getCurrentPosition() - mStartPosition * 1000));
                txtVideoLength.setText(milliSecondsToTimer(seekBarVideo.getProgress()) + "");
                videoView.seekTo(mStartPosition * 1000);
                videoView.pause();
                seekBarVideo.setProgress(0);
                txtVideoLength.setText("00:00");
                imgPlay.setBackgroundResource(R.drawable.ic_white_play);
            } else {
                seekBarVideo.setProgress((videoView.getCurrentPosition() - mStartPosition * 1000));
                txtVideoLength.setText(milliSecondsToTimer(seekBarVideo.getProgress()) + "");
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void setSeekBarPosition() {

        if (mDuration >= mMaxDuration) {
            mStartPosition = 0;
            mEndPosition = mMaxDuration;

        } else {
            mStartPosition = 0;
            mEndPosition = mDuration;
        }


        mTimeVideo = mDuration;
        seekBarVideo.setMax(mMaxDuration * 1000);
        videoView.seekTo(mStartPosition * 1000);

        String mStart = mStartPosition + "";
        if (mStartPosition < 10)
            mStart = "0" + mStartPosition;

        int startMin = Integer.parseInt(mStart) / 60;
        int startSec = Integer.parseInt(mStart) % 60;

        String mEnd = mEndPosition + "";
        if (mEndPosition < 10)
            mEnd = "0" + mEndPosition;

        int endMin = Integer.parseInt(mEnd) / 60;
        int endSec = Integer.parseInt(mEnd) % 60;

    }

    /**
     * called when playing video completes
     */
    private void onVideoCompleted() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        seekBarVideo.setProgress(0);
        videoView.seekTo(mStartPosition * 1000);
        videoView.pause();
        imgPlay.setBackgroundResource(R.drawable.ic_white_play);
        finish();
    }

    /**
     * Handle changes of left and right thumb movements
     *
     * @param index index of thumb
     * @param value value
     */
    private void onSeekThumbs(int index, float value) {
        switch (index) {
            case BarThumb.LEFT: {
                mStartPosition = (int) ((mDuration * value) / 100L);
                videoView.seekTo(mStartPosition * 1000);
                break;
            }
            case BarThumb.RIGHT: {
                mEndPosition = (int) ((mDuration * value) / 100L);
                break;
            }
        }
        mTimeVideo = (mEndPosition - mStartPosition);
        seekBarVideo.setMax(mTimeVideo * 1000);
        seekBarVideo.setProgress(0);
        videoView.seekTo(mStartPosition * 1000);

        String mStart = mStartPosition + "";
        if (mStartPosition < 10)
            mStart = "0" + mStartPosition;

        int startMin = Integer.parseInt(mStart) / 60;
        int startSec = Integer.parseInt(mStart) % 60;

        String mEnd = mEndPosition + "";
        if (mEndPosition < 10)
            mEnd = "0" + mEndPosition;
        int endMin = Integer.parseInt(mEnd) / 60;
        int endSec = Integer.parseInt(mEnd) % 60;


    }

    private void onStopSeekThumbs() {
//        mMessageHandler.removeMessages(SHOW_PROGRESS);
//        videoView.pause();
//        mPlayView.setVisibility(View.VISIBLE);
    }



}
