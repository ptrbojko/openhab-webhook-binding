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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.jexl3.JexlContext;
import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * @author Piotr Bojko - Initial contribution
 */
@NonNullByDefault
public class RequestHandlerMediator {

    private final Map<String, Consumer<JexlContext>> observers = new HashMap<>();

    public void register(String id, Consumer<JexlContext> observer) {
        observers.put(id, observer);
    }

    public void unregister(String id) {
        observers.remove(id);
    }

    public void notify(String path, JexlContext context) {
        Consumer<JexlContext> observer = observers.get(path);
        if (observer == null) {
            return;
        }
        observer.accept(context);
    }
}
