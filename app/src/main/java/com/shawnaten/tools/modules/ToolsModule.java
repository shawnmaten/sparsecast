package com.shawnaten.tools.modules;

import dagger.Module;

@Module(
        complete = false,
        includes = {
                LocationModule.class
        }
)
public class ToolsModule {

}
