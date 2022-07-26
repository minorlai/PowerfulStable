package com.hzzt.common.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class MyCountDownTimer {



        /**
         * Millis since epoch when alarm should stop.
         */
        public   long mMillisInFuture;

        /**
         * The interval in millis that the user receives callbacks
         */
        public  long mCountdownInterval;

    public long mStopTimeInFuture;

        /**
         * boolean representing if the timer was cancelled
         */
        private boolean mCancelled = false;

        /**
         * @param millisInFuture The number of millis in the future from the call
         *
         *   is called.
         * @param countDownInterval The interval along the way to receive
         *   {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            Log.e("shijianlog",mMillisInFuture+"");
            mMillisInFuture = millisInFuture;
            mCountdownInterval = countDownInterval;

        }

        /**
         * Cancel the countdown.
         */
        public   void cancel() {
            mCancelled = true;
            mHandler.removeMessages(MSG);
        }

        /**
         * Start the countdown.
         * @return
         */
        public   MyCountDownTimer start(long millisInFuture) {
            Log.e("shijianlog",mMillisInFuture+"");
            mMillisInFuture=millisInFuture;

            mCancelled = false;
            if (mMillisInFuture <= 0) {
                onFinish();
                return this;
            }
            mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
            mHandler.sendMessage(mHandler.obtainMessage(MSG));
            return this;
        }


    /**
     * Callback fired on regular interval.
     *
     * @param millisUntilFinished The amount of time until finished.
     */
    public void onTick(long millisUntilFinished) {

    }

    /**
     * Callback fired when the time is up.
     */
    public void onFinish() {

    }


    private static  int MSG = 1;


        // handles counting down
        private Handler mHandler = new Handler() {

            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {

                synchronized (MyCountDownTimer.this) {
                    if (mCancelled) {
                        return;
                    }

                     long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                    if (millisLeft <= 0) {
                        onFinish();
                    } else {
                        long lastTickStart = SystemClock.elapsedRealtime();
                        onTick(millisLeft);

                        // take into account user's onTick taking time to execute
                        long lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart;
                        long delay;

                        if (millisLeft < mCountdownInterval) {
                            // just delay until done
                            delay = millisLeft - lastTickDuration;

                            // special case: user's onTick took more than interval to
                            // complete, trigger onFinish without delay
                            if (delay < 0) delay = 0;
                        } else {
                            delay = mCountdownInterval - lastTickDuration;

                            // special case: user's onTick took more than interval to
                            // complete, skip to next interval
                            while (delay < 0) delay += mCountdownInterval;
                        }

                        sendMessageDelayed(obtainMessage(MSG), delay);
                    }
                }
            }
        };

    }
