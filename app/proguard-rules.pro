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

# This rule specifies that all classes and class members can be obfuscated.
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod

# This rule specifies that all classes extending android.app.Activity will be kept as-is during obfuscation.
-keep class * extends android.app.Activity

# This rule specifies that all classes implementing Parcelable interface will be kept as-is during obfuscation.
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# This rule specifies that all methods named 'onCreate' will be kept as-is during obfuscation.
-keepclassmembers class * {
    void onCreate(android.os.Bundle);
}

# This rule specifies that all fields named 'serialVersionUID' will be kept as-is during obfuscation.
-keepclassmembers class * {
    static final long serialVersionUID;
}
