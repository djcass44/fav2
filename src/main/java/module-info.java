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
	requires org.eclipse.jetty.http;
	requires okhttp3;

	// required for ssl
	requires jdk.crypto.ec;
	requires com.google.common;
	requires io.swagger.v3.oas.models;
	requires java.xml.bind;
	requires java.activation;

	// jetty
	requires org.eclipse.jetty.server;
	requires org.eclipse.jetty.alpn.server;
	requires org.eclipse.jetty.http2.server;
	requires org.eclipse.jetty.util;
	requires org.eclipse.jetty.http2.common;


	opens dev.castive.fav2.http to io.javalin;
	exports dev.castive.fav2;
}
