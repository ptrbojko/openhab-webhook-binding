/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.webhook.internal.httprequest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Servlet implementing a webhook to be called by other services
 *
 * @author Piotr Bojko - Initial contribution
 */
public class WebhookServlet extends HttpServlet {

    private static final long serialVersionUID = 7516477295622773147L;

    private final RequestHandlerMediator mediator;

    public WebhookServlet(RequestHandlerMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        respond(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        respond(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        respond(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        respond(req, resp);
    }

    private void respond(HttpServletRequest req, HttpServletResponse resp) {
        JexlContext context = new MapContext(map(req, resp));
        String path = req.getPathInfo();
        if (StringUtils.isBlank(path)) {
            return;
        }
        String[] split = path.split("/");
        if (split.length < 1) {
            return;
        }
        mediator.notify(split[1], context);
    }

    private Map<String, Object> map(HttpServletRequest req, HttpServletResponse resp) {
        return Map.of("req", new HttpRequestWrapper(req), "resp", new HttpResponseWrapper(resp));
    }
}
