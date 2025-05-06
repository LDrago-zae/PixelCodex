# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Keep GameItem class and its constructors
-keep class com.example.pixelcodex.GameItem {
    public <init>();
    public <init>(java.lang.String, double);
}

# Keep all fields and methods in GameItem
-keepclassmembers class com.example.pixelcodex.GameItem {
    private java.lang.String title;
    public *;
}

# Keep UserItem class and its constructors
-keep class com.example.pixelcodex.UserItem {
    public <init>();
    public <init>(java.lang.String, java.lang.String);
}

# Keep all fields and methods in UserItem
-keepclassmembers class com.example.pixelcodex.UserItem {
    private java.lang.String name;
    public *;
}

# Keep Firebase classes (optional, for safety)
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**