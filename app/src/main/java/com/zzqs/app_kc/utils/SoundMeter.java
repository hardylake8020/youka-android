package com.zzqs.app_kc.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.zzqs.app_kc.app.ContentData;

import java.io.IOException;

public class SoundMeter {
    static final private double EMA_FILTER = 0.6;

    private MediaRecorder mRecorder = null;
    private double mEMA = 0.0;
    public void start(String name) {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(ContentData.BASE_SOUNDS + "/" + name);
            try {
                mRecorder.prepare();
                mRecorder.start();

                mEMA = 0.0;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.setOnErrorListener(null);
            mRecorder.setPreviewDisplay(null);
            try {
                mRecorder.stop();
            } catch (IllegalStateException e) {
                Log.w("zzqs", "stopRecord", e);
            } catch (RuntimeException e) {
                Log.w("zzqs", "stopRecord", e);
            } catch (Exception e) {
                Log.w("zzqs", "stopRecord", e);
            }
            mRecorder.release();
            if (mRecorder != null) {
                mRecorder.setOnErrorListener(null);
                try {
                    mRecorder.release();
                } catch (IllegalStateException e) {
                    Log.w("zzqs", "stopRecord", e);
                } catch (Exception e) {
                    Log.w("zzqs", "stopRecord", e);
                }
            }
            mRecorder = null;
        }
    }

    public void pause() {
        if (mRecorder != null) {
            mRecorder.stop();
        }
    }

    public void start() {
        if (mRecorder != null) {
            mRecorder.start();
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
}
