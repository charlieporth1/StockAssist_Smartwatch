package com.example.samplesmartwatch;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends ControlExtension {


	MainActivity(final String hostAppPackageName, final Context context,
            Handler handler) {
        super(context, hostAppPackageName);
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
    }

    /**
     * Get supported control width.
     *
     * @param context The context.
     * @return the width.
     */

    @Override
    public void onDestroy() {

    };

    @Override
    public void onStart() {
        // Nothing to do. Animation is handled in onResume.
    }

    @Override
    public void onStop() {
        // Nothing to do. Animation is handled in onPause.
    }

    @Override
    public void onResume() {
    //    mIsVisible = true;

        Log.d("My Tag", "Starting animation");

        // Animation not showing. Show animation.
      //  mIsShowingAnimation = true;
       // mAnimation = new Animation();
       // mAnimation.run();
        showLayout(R.layout.activity_main,null);
    }

    @Override
    public void onObjectClick(ControlObjectClickEvent event) 
    {
    	// TODO Auto-generated method stub
    	super.onObjectClick(event);

    	sendText(R.id.textView1, "changed you");
    }
    
    @Override
    public void onPause() {
      /*  Log.d(SampleExtensionService.LOG_TAG, "Stopping animation");
        mIsVisible = false;

        if (mIsShowingAnimation) {
            stopAnimation();
        }*/
    }

    /**
     * Stop showing animation on control.
     */
    public void stopAnimation() {
      
    }

    @Override
    public void onTouch(final ControlTouchEvent event) {
    	
    }


}
