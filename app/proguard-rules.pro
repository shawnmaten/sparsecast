# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/Android Studio.app/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


# for Google Play services

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# end of Google Play services

# for retrofit

-dontwarn okio.**

-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keepattributes Exceptions

# changed this from a -keep to -dontwarn on 2014-12-17
# https://github.com/ReactiveX/RxJava/issues/1415
-dontwarn sun.misc.Unsafe

#your package path where your gson models are stored
-keep class com.shawnaten.simpleweather.tools.** { *; }

-keep class com.shawnaten.simpleweather.lib.model.** { *; }

# end of retrofit

# Needed by google-api-client to keep generic types and @Key annotations accessed via reflection

-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

# for retrolambda

-dontwarn java.lang.invoke.*

# for butterknife

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# end of butterknife

# for dagger (because of the exclusions to prevent duplicate classes)

-dontwarn javax**
-dontwarn com.google.auto.value.AutoAnnotation
-dontwarn dagger.internal.codegen.ComponentProcessor
-dontwarn dagger.shaded.auto.common.BasicAnnotationProcessor
-dontwarn com.google.auto.service.AutoService

# end of dagger
