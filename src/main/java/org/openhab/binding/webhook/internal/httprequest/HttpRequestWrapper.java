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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Piotr Bojko - Initial contribution
 */
public class HttpRequestWrapper {
    private final HttpServletRequest request;
    private HttpRequestBodyWrapper cachedBody;

    public HttpRequestWrapper(HttpServletRequest request) {
        this.request = request;
    }

    public String getMethod() {
        return request.getMethod();
    }

    public Map<String, String[]> getParameters() {
        return request.getParameterMap();
    }

    public HttpRequestBodyWrapper getBody() {
        if (cachedBody == null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
                cachedBody = new HttpRequestBodyWrapper(() -> reader);
            } catch (IOException e) {
                return null;
            }
        }
        return cachedBody;
    }
}
