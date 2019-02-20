package com.example.issue615;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;
import reactor.netty.tcp.TcpClient;

import java.net.InetSocketAddress;

@SpringBootApplication
public class Issue615Application {

	public static void main(String[] args) {
		SpringApplication.run(Issue615Application.class, args);

		TcpClient tcpClient =
				TcpClient.create()
						.secure(spec -> spec.sslContext(SslContextBuilder.forClient()
											.trustManager(InsecureTrustManagerFactory.INSTANCE)))
						.proxy(ts -> buildProxy(ts, <Proxy Host>, <Proxy Port>))
						.wiretap(true);

		HttpClient httpClient = HttpClient.from(tcpClient);

		ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

		WebClient.builder()
				.clientConnector(connector)
				.build()
				.get()
				.uri("https://google.com")
				.retrieve()
				.bodyToMono(String.class)
				.block();
	}

	private static ProxyProvider.Builder buildProxy(ProxyProvider.TypeSpec ts, String proxyHost, Integer proxyPort) {
		return ts.type(ProxyProvider.Proxy.HTTP).address(new InetSocketAddress(proxyHost, proxyPort));
	}

}
