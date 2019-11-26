# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\AndroidSDK/tools/proguard/proguard-android.txt
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


#泛型，解决出现类型转换错误的问题
-keepattributes Signature

#Serializable序列化
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#注解
-keepattributes *Annotation*
#sharesdk混淆注意
-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class m.framework.**{*;}
#Gson混淆配置
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.***
-keep class com.google.gson.stream.** { *; }
#Umeng sdk混淆配置
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep class com.umeng.**

-keep public class com.idea.fifaalarmclock.app.R$*{
    public static final int *;
}

-keep public class com.umeng.fb.ui.ThreadView {
}

-dontwarn com.umeng.**

-dontwarn org.apache.commons.**

-keep public class * extends com.umeng.**

-keep class com.umeng.** {*; }
#end
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keep class * extends android.support.v7.app.AppCompatActivity
-keep class * extends android.support.v4.app.FragmentActivity
-keepclassmembers class **.R$* {
    public static <fields>;
}
#不混淆第三方包
-keep class android.support.** { *; }
-keep class com.tencent.android.tpush.** { *; }
-keep class com.qq.taf.jce { *; }
-keep class com.nineoldandroids.** { *; }
-keep class com.sun.jna.** { *; }
-keep class com.jg.** { *; }
-keep class org.apache.** { *; }
-keep class cn.com.broadlink.blnetwork.** { *; }
-keep class de.tavendo.autobahn.** { *; }
-keep class com.map.api.location.** { *; }
-keep class com.aps.** { *; }
-keep class com.loopj.android.http.** { *; }
-keep class cz.msebera.android.** { *; }
-keep class com.google.code.gson.** { *; }
-keep class org.codehaus.jackson.** { *; }
-keep class com.github.bumptech.glide.** { *; }
-keep class com.google.android.gms.** { *; }
#-keep class de.greenrobot.** { *; }
-keep class com.honeywell.lib.** { *; }
#忽略第三方包导致的异常
-dontwarn android.support.**
-dontwarn com.tencent.android.tpush.**
-dontwarn com.qq.taf.jce
-dontwarn com.nineoldandroids.**
-dontwarn com.sun.jna.**
-dontwarn com.jg.**
-dontwarn org.apache.**
-dontwarn cn.com.broadlink.blnetwork.**
-dontwarn de.tavendo.autobahn.**
-dontwarn com.map.api.location.**
-dontwarn com.aps.**
-dontwarn com.loopj.android.http.**
-dontwarn cz.msebera.android.**
-dontwarn com.google.code.gson.**
-dontwarn org.codehaus.jackson.**
-dontwarn com.github.bumptech.glide.**
-dontwarn com.google.android.gms.**
#-dontwarn de.greenrobot.**
-dontwarn com.honeywell.lib.**

-dontwarn android.support.v4.**
-dontwarn android.support.v7.**

#eventbus
-dontwarn de.greenrobot.event.**
-keep class de.greenrobot.event.** { *; }
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}

#-dontwarn **
#-libraryjars libs/android-async-http-1.4.9.jar
#-libraryjars libs/Android_Location_V1.1.2.jar
#-libraryjars libs/autobahn-0.5.0.jar
#-libraryjars libs/BLNetwork.jar
#-libraryjars libs/commons-codec-1.9.jar
#-libraryjars libs/commons-logging-1.2.jar
#-libraryjars libs/fluent-hc-4.5.2.jar
#-libraryjars libs/httpclient-4.5.2.jar
#-libraryjars libs/httpclient-cache-4.5.2.jar
#-libraryjars libs/httpclient-win-4.5.2.jar
#-libraryjars libs/httpcore-4.4.4.jar
#-libraryjars libs/httpmime-4.5.2.jar
#-libraryjars libs/jg_filter_sdk_1.1.jar
#-libraryjars libs/jna-4.1.0.jar
#-libraryjars libs/jna-platform-4.1.0.jar
#-libraryjars libs/nineoldandroids-2.4.0.jar
#-libraryjars libs/wup-1.0.0.E-SNAPSHOT.jar
#-libraryjars libs/Xg_sdk_v2.46_20160602_1638.jar
#-libraryjars libs/xmlwise-1_2.jar



#-optimizationpasses 5
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-dontpreverify
#-verbose
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
##-keep public class * extends android.app.Activity
##-keep public class * extends android.app.Application
##-keep public class * extends android.app.Service
##-keep public class * extends android.content.BroadcastReceiver
##-keep public class * extends android.content.ContentProvider
##-keep public class com.android.vending.licensing.ILicensingService
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#-keepclasseswithmembernames class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#}
#-keepclasseswithmembernames class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#-keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}

