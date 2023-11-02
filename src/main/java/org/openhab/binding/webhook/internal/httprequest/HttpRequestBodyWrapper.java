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
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.Gson;

/**
 * @author Piotr Bojko - Initial contribution
 */
public class HttpRequestBodyWrapper {
    private final Supplier<BufferedReader> bodyReaderSupplier;
    private String cachedString;
    private Map<String, Object> cachedJson;

    public HttpRequestBodyWrapper(Supplier<BufferedReader> bodyReaderSupplier) {
        this.bodyReaderSupplier = bodyReaderSupplier;
    }

    public String getText() {
        if (cachedString == null) {
            cachedString = bodyReaderSupplier.get().lines().collect(Collectors.joining(System.lineSeparator()));
        }
        return cachedString;
    }

    public Map<String, Object> getJson() {
        if (cachedJson == null) {
            Gson gson = new Gson();
            cachedJson = gson.<Map<String, Object>> fromJson(getText(), Map.class);
        }
        return cachedJson;
    }
}
