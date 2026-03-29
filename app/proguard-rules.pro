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

# --- R8: optional classpath references (MinIO / OkHttp / SimpleXML) ---
# OkHttp and MinIO reference FindBugs annotations for static analysis; they are not on the Android classpath.
-dontwarn edu.umd.cs.findbugs.annotations.**

# SimpleXML (transitive of MinIO) optionally uses StAX (javax.xml.stream). Android does not ship these APIs;
# the library uses other parsers at runtime. Suppress missing-class errors for those optional references.
-dontwarn javax.xml.stream.**