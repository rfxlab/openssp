package com.rfxlab.ssp.core.system.vertx;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atg.openssp.common.core.entry.CoreSupplyServlet;
import com.atg.openssp.common.core.exchange.Exchange;
import com.atg.openssp.common.exception.RequestException;
import com.atg.openssp.core.exchange.ExchangeServer;
import com.atg.openssp.core.exchange.RequestSessionAgent;

public class SspServletVertxHandler extends CoreSupplyServlet<RequestSessionAgent> {

	private static final long serialVersionUID = 1L;

	@Override
	protected RequestSessionAgent getAgent(final HttpServletRequest request, final HttpServletResponse response)
			throws RequestException {
		return new RequestSessionAgent(request, response);
	}

	@Override
	protected Exchange<RequestSessionAgent> getServer() {
		return new ExchangeServer();
	}

	public SspServletVertxHandler() throws ServletException {
		init();
	}

	public void handle(final VertxHttpServletRequest request, final VertxHttpServletResponse response)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		String site = request.getParameter("site");
		System.out.println("URI: " + uri);
		if (site != null) {
			try {
				doGet(request, response);
			} catch (Exception e) {
				e.printStackTrace();
				response.getWriter().write("error");
			}
		}
		response.flushBuffer();
		response.writeToVertx();
	}
}