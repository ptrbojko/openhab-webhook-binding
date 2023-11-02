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
package org.openhab.binding.webhook.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;

/**
 * @author Piotr Bojko - Initial contribution
 */
public class ExpressionResultConverter {

    private final Map<String, Function<Object, State>> converters;

    public ExpressionResultConverter() {
        converters = new HashMap<>();
        converters.put("String", this::convertToStringType);
        converters.put("Number", this::convertToDecimalType);
        converters.put("Switch", this::convertToOnOffType);
        converters.put("Contact", this::convertToOpenClosedType);
    }

    public State convert(String stateType, Object o) {
        return converters.getOrDefault(stateType, this::convertToStringType).apply(o);
    }

    private StringType convertToStringType(Object o) {
        return StringType.valueOf(String.valueOf(o));
    }

    private DecimalType convertToDecimalType(Object o) {
        return DecimalType.valueOf(String.valueOf(o));
    }

    private OnOffType convertToOnOffType(Object o) {
        if (o instanceof Boolean) {
            return OnOffType.from((Boolean) o);
        } else {
            return OnOffType.from(String.valueOf(o));
        }
    }

    private OpenClosedType convertToOpenClosedType(Object o) {
        if (o instanceof Boolean) {
            return (Boolean) o ? OpenClosedType.OPEN : OpenClosedType.CLOSED;
        } else {
            return OpenClosedType.valueOf(String.valueOf(o));
        }
    }
}
