module dev.castive.fav2 {
    requires transitive kotlin.stdlib;
    requires kotlin.stdlib.jdk8;

    // external libaries
    requires kotlinx.coroutines.core;
    requires org.jsoup;
    requires io.javalin;
    requires com.fasterxml.jackson.module.kotlin;
    requires java.desktop;
    requires dev.castive.log2;

    exports dev.castive.fav2;
}