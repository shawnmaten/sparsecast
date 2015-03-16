package com.shawnaten.simpleweather.component;

import com.shawnaten.simpleweather.module.ActivityModule;
import com.shawnaten.simpleweather.scopes.Activity;

import dagger.Component;

@Activity
@Component(
        dependencies = NetworkComponent.class,
        modules = {
                ActivityModule.class
        }
)
public interface ActivityComponent {
}
