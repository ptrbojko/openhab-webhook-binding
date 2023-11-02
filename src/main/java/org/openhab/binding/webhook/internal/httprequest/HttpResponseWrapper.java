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

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Wrapper for raw HttpServletResponse class with helper methods for scripting
 *
 * @author Piotr Bojko - Initial contribution
 */
@NonNullByDefault
public class HttpResponseWrapper {
    private final HttpServletResponse response;

    public HttpResponseWrapper(HttpServletResponse resp) {
        response = resp;
    }

    public void setStatus(int status) {
        response.setStatus(status);
    }

    public void setContentType(String contentType) {
        response.setContentType(contentType);
    }

    public void setStringAsBody(Object body) throws IOException {
        response.getWriter().print(String.valueOf(body));
    }
}
