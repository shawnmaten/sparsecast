package com.shawnaten.simpleweather.module;

import android.app.Fragment;
import android.os.Bundle;

import com.shawnaten.simpleweather.scopes.Activity;

import dagger.Module;
import dagger.Provides;

@Module
public class TestModule {

    @Provides @Activity
    Fragment provideInteger() {
        Fragment fragment = new Fragment();
        Bundle args = new Bundle();
        args.putString("arg1", "how bout that dependency injection?");
        fragment.setArguments(args);
        return fragment;
    }
}
