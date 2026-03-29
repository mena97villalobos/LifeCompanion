# MinIO (OkHttp) optional FindBugs annotations — not on Android classpath.
-dontwarn edu.umd.cs.findbugs.annotations.**

# SimpleXML (transitive of MinIO) optional StAX — not available on Android; runtime uses other parsers.
-dontwarn javax.xml.stream.**
