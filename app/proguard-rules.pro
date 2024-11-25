# 代码混淆压缩比，在0~7之间
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

-verbose

#google推荐算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# 避免混淆Annotation、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

# 重命名抛出异常时的文件名称
-renamesourcefileattribute SourceFile

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
# 忽略警告，避免打包时某些警告出现，没有这个的话，构建报错
-ignorewarnings

# 避免混淆泛型
-keepattributes Signature

# 处理support包
-dontnote android.support.**
-dontwarn android.support.**

# 保留四大组件，自定义的Application等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留Activity中参数类型为View的所有方法
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}


# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
#audio不做混淆
-keep class com.hongri.multimedia.audio.**{*;}
#第三方jar包不被混淆
-keep class com.github.test.** {*;}
#保留bean不被混淆
-keep class com.hongri.multimedia.bean.**{*;}

#保留自定义的Test类和类成员不被混淆
-keep class com.hongri.multimedia.audio.** {*;}
#保留自定义的xlog文件夹下面的类、类成员和方法不被混淆
-keep class com.test.xlog.** {
    <fields>;
    <methods>;
}

# 不混淆log
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

#关闭sout输出
-assumenosideeffects class java.io.PrintStream {

public void println(...);

public void print(...);

}
# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# Gson
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.idea.fifaalarmclock.entity.**
-keep class com.google.gson.stream.** { *; }
-keep class com.bean.** { *; }


# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**


# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}


# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
#Aria混淆配置
-dontwarn com.arialyy.aria.**
-keep class com.arialyy.aria.**{*;}
-keep class **$$DownloadListenerProxy{ *; }
-keep class **$$UploadListenerProxy{ *; }
-keep class **$$DownloadGroupListenerProxy{ *; }
-keep class **$$DGSubListenerProxy{ *; }
-keepclasseswithmembernames class * {
    @Download.* <methods>;
    @Upload.* <methods>;
    @DownloadGroup.* <methods>;
}

#保留Keep注解的类名和方法
-keep,allowobfuscation @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclassmembers class * {
    @android.support.annotation.Keep *;
}
