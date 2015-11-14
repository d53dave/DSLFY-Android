package net.d53dev.dslfy.android;

import dagger.Module;

/**
 * Add all the other modules to this one.
 */
@Module(
        includes = {
                AndroidModule.class,
                DSLFYModule.class
        }
)
public class RootModule {
}
