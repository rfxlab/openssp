package server.vertx;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atg.openssp.common.core.entry.CoreSupplyServlet;
import com.atg.openssp.common.core.exchange.Exchange;
import com.atg.openssp.common.exception.RequestException;
import com.atg.openssp.core.exchange.ExchangeServer;
import com.atg.openssp.core.exchange.RequestSessionAgent;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class SimpleSspServer {

	private static final Logger log = LoggerFactory.getLogger(SimpleSspServer.class);

	public static void main(String[] args) {
		new SimpleSspServer().start();
	}

	//http://devssp?site=1&domain=size_1.com&h=600&w=800&publisher=pubisher_1
	public void start() {
		Vertx vertx = Vertx.vertx();
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		// initing cache data
		DataIniter.init();
		
		router.route().handler(context -> {
			VertxHttpServletRequest request = new VertxHttpServletRequest(context);
			VertxHttpServletResponse response = new VertxHttpServletResponse(context);
			handlerBidding(request, response);
		});

		HttpServer server = vertx.createHttpServer();
		server.requestHandler(router::accept);
		server.listen(9790);
	}

	void handlerBidding(final VertxHttpServletRequest request, final VertxHttpServletResponse response) {
		try {
			new VertxServletHandler().handle(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
