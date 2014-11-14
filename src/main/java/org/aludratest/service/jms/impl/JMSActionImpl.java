/*
 * Copyright (C) 2010-2014 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.service.jms.impl;

import java.io.Serializable;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.aludratest.exception.AccessFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.impl.log4testing.data.attachment.Attachment;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.jms.JMSCondition;
import org.aludratest.service.jms.JMSInteraction;
import org.aludratest.service.jms.JMSVerification;

/**
 * Implements all action interfaces of AludraJMS in one class.
 * @deprecated The implementation is not yet functional; implementation was suspended in favour of other tasks!
 * @author Volker Bergmann
 */
@Deprecated
public class JMSActionImpl implements JMSInteraction, JMSVerification, JMSCondition {

    private AludraJMSImpl service;

    /**
     * Initializes a JMSActionImpl with an {@link AludraJMSImpl}.
     * @param service
     */
    public JMSActionImpl(AludraJMSImpl service) {
        this.service = service;
    }

    @Override
    public void setSystemConnector(SystemConnector systemConnector) {
        // empty implementation
    }
    
    @Override
    public void sendTextMessage(String text, String destinationName) {
        try {
            TextMessage message = createTextMessage();
            message.setText(text);
            sendMessage(message, destinationName);
        } catch (JMSException e) {
            throw new TechnicalException("Unable to set text on message", e);
        }
    }

    @Override
    public void sendObjectMessage(Serializable object, String destinationName) {
        try {
            ObjectMessage message = createObjectMessage();
            message.setObject(object);
            sendMessage(message, destinationName);
        } catch (JMSException e) {
            throw new TechnicalException("Unable to set text on message", e);
        }
    }

    @Override
    public TextMessage createTextMessage() {
        try {
            return getSession().createTextMessage();
        } catch (JMSException e) {
            throw new TechnicalException("Unable to set text on message", e);
        }
    }

    @Override
    public ObjectMessage createObjectMessage() {
        try {
            return getSession().createObjectMessage();
        } catch (JMSException e) {
            throw new TechnicalException("Error creating ObjectMessage", e);
        }
    }

    @Override
    public BytesMessage createBytesMessage() {
        try {
            return getSession().createBytesMessage();
        } catch (JMSException e) {
            throw new TechnicalException("Error creating BytesMessage", e);
        }
    }

    @Override
    public MapMessage createMapMessage() {
        try {
            return getSession().createMapMessage();
        } catch (JMSException e) {
            throw new TechnicalException("Error creating MapMessage", e);
        }
    }

    @Override
    public StreamMessage createStreamMessage() {
        try {
            return getSession().createStreamMessage();
        } catch (JMSException e) {
            throw new TechnicalException("Error creating StreamMessage", e);
        }
    }

    @Override
    public void sendMessage(Message message, String destinationName) {
        try {
            Destination destination = getDestination(destinationName);
            MessageProducer producer = getSession().createProducer(destination);
            producer.send(message);
            producer.close();
        } catch (JMSException e) {
            throw new TechnicalException("Error sending message", e);
        }
    }

    @Override
    public Message receiveMessage(String destinationName) {
        try {
            Destination destination = getDestination(destinationName);
            MessageConsumer consumer = getSession().createConsumer(destination);
            Message message = consumer.receive();
            consumer.close();
            return message;
        } catch (JMSException e) {
            throw new TechnicalException("Error receiving message", e);
        }
    }

    @Override
    public void assertDestinationAvailable(String destinationName) {
        if (!JMSUtil.isDestinationAvailable(destinationName, service)) {
            throw new AccessFailure("Destination " + destinationName + " not found");
        }
    }

    @Override
    public boolean isDestinationPresent(String destinationName) {
        return JMSUtil.isDestinationAvailable(destinationName, service);
    }

    @Override
    public List<Attachment> createAttachments(Object object, String label) {
        throw new TechnicalException("Not supported");
    }
    
    @Override
    public List<Attachment> createDebugAttachments() {
        return null;
    }

    // private helpers -------------------------------------------------------------------------------------------------

    private Session getSession() {
        return service.getSession();
    }

    private Destination getDestination(String destinationName) {
        return service.getDestination(destinationName);
    }

}
