package com.example.testbluetooh;

import com.mazenrashed.printooth.Printooth;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Printooth.INSTANCE.init(getApplicationContext());
    }
}
