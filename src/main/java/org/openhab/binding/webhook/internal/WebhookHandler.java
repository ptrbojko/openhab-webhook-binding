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

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlScript;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.webhook.internal.httprequest.RequestHandlerMediator;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link WebhookHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Piotr Bojko - Initial contribution
 */
@NonNullByDefault
public class WebhookHandler extends BaseThingHandler {

    private static final JexlEngine JEXL = new JexlBuilder().create();
    private static final Logger logger = LoggerFactory.getLogger(WebhookHandler.class);
    private static final ExpressionResultConverter CONVERTER = new ExpressionResultConverter();

    private final RequestHandlerMediator mediator;

    public WebhookHandler(Thing thing, RequestHandlerMediator mediator) {
        super(thing);
        this.mediator = mediator;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        scheduler.execute(this::updateConfiguration);
    }

    private void updateConfiguration() {
        try {
            mediator.register(getMediationId(), createWebhookChannelHandlers());
            updateStatus(ThingStatus.ONLINE);
        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
        }
    }

    private Consumer<JexlContext> createWebhookChannelHandlers() {
        Stream<Consumer<JexlContext>> channelsConsumerStream = getThing().getChannels().stream()
                .filter(ch -> !WebhookBindingConstants.LAST_CALL_CHANNEL.equals(ch.getChannelTypeUID().getId()))
                .map(this::createChannelUpdater);
        Stream<Consumer<JexlContext>> responseConsumerStream = Stream.of(createResponseConsumer());
        List<Consumer<JexlContext>> consumers = Stream.concat(channelsConsumerStream, responseConsumerStream)
                .collect(Collectors.toList());
        return context -> {
            updateState(WebhookBindingConstants.LAST_CALL_CHANNEL, new DateTimeType());
            consumers.forEach(consumer -> consumer.accept(context));
        };
    }

    private Consumer<JexlContext> createResponseConsumer() {
        Object expressionProperty = this.getConfig().get(WebhookBindingConstants.EXPRESSION_PROPERTY);
        if (expressionProperty == null) {
            return context -> {
            };
        }
        JexlScript expression = JEXL.createScript(String.valueOf(expressionProperty));
        return context -> {
            try {
                expression.execute(context);
            } catch (Exception e) {
                logger.warn("Problem evaluating jexl", e);
                return;
            }
        };
    }

    private Consumer<JexlContext> createChannelUpdater(Channel channel) {
        JexlScript expression = JEXL.createScript(
                String.valueOf(channel.getConfiguration().get(WebhookBindingConstants.EXPRESSION_PROPERTY)));
        return context -> {
            Object evaluation = null;
            try {
                evaluation = expression.execute(context);
            } catch (Exception e) {
                logger.warn("Problem evaluating jexl", e);
                return;
            }
            if (evaluation == null) {
                return;
            }
            switch (channel.getKind()) {
                case STATE:
                    updateState(channel.getUID(), CONVERTER.convert(channel.getAcceptedItemType(), evaluation));
                    return;
                case TRIGGER:
                    triggerChannel(channel.getUID(), String.valueOf(evaluation));
                    return;
                default:
                    return;
            }
        };
    }

    private String getMediationId() {
        return getThing().getUID().getId();
    }

    @Override
    public void dispose() {
        mediator.unregister(getMediationId());
    }
}
