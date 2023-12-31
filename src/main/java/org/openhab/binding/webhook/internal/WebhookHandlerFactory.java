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

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.webhook.internal.httprequest.RequestHandlerMediator;
import org.openhab.binding.webhook.internal.httprequest.WebhookServlet;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link WebhookHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Piotr Bojko - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.webhook", service = ThingHandlerFactory.class)
public class WebhookHandlerFactory extends BaseThingHandlerFactory {

    private final Logger logger = LoggerFactory.getLogger(WebhookHandlerFactory.class);

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set
            .of(WebhookBindingConstants.THING_TYPE_WEBHOOK);

    private final RequestHandlerMediator mediator;

    @Activate
    public WebhookHandlerFactory(@Reference HttpService httpService) {
        this.mediator = new RequestHandlerMediator();
        initServlet(httpService);
    }

    private void initServlet(HttpService httpService) {
        try {
            httpService.registerServlet("/webhook", new WebhookServlet(this.mediator), null,
                    httpService.createDefaultHttpContext());
        } catch (Exception e) {
            logger.error("Cannot register servlet", e);
        }
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (WebhookBindingConstants.THING_TYPE_WEBHOOK.equals(thingTypeUID)) {
            return new WebhookHandler(thing, mediator);
        }

        return null;
    }
}
