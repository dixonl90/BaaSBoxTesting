package com.bestbeforeapp.baasboxtesting;

import android.app.Application;

import com.baasbox.android.BaasBox;

import timber.log.Timber;

/**
 * Created by Luke Dixon on 08/10/15.
 */
public class NotesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        BaasBox.builder(this)
                .setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                .setApiDomain("192.168.1.73")
                .setPort(9000)
                .setAppCode("1234567890")
                .init();
    }

}
