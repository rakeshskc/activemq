/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
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
package org.apache.activemq.network.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicRequestor;
import javax.jms.TopicSession;
import junit.framework.TestCase;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TopicBridgeSpringTest extends TestCase implements MessageListener {
    
    protected static final Log log = LogFactory.getLog(TopicBridgeSpringTest.class);

    protected static final int MESSAGE_COUNT = 10;
    protected AbstractApplicationContext context;
    protected TopicConnection localConnection;
    protected TopicConnection remoteConnection;
    protected TopicRequestor requestor;
    protected TopicSession requestServerSession;
    protected MessageConsumer requestServerConsumer;
    protected MessageProducer requestServerProducer;

    protected void setUp() throws Exception {
        
        super.setUp();
        context = createApplicationContext();
        ActiveMQConnectionFactory fac = (ActiveMQConnectionFactory) context.getBean("localFactory");
        localConnection = fac.createTopicConnection();
        localConnection.start();
        requestServerSession = localConnection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
        Topic theTopic = requestServerSession.createTopic(getClass().getName());
        requestServerConsumer = requestServerSession.createConsumer(theTopic);
        requestServerConsumer.setMessageListener(this);
        requestServerProducer = requestServerSession.createProducer(null);
        
        fac = (ActiveMQConnectionFactory) context.getBean("remoteFactory");
        remoteConnection = fac.createTopicConnection();
        remoteConnection.start();
        TopicSession session = remoteConnection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
        requestor = new TopicRequestor(session,theTopic);
    }


    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/apache/activemq/network/jms/topic-spring.xml");
    }

    
    protected void tearDown() throws Exception {
        localConnection.close();
        super.tearDown();
    }
    
    public void testTopicRequestorOverBridge() throws JMSException{
        for (int i =0;i < MESSAGE_COUNT; i++){
            TextMessage msg = requestServerSession.createTextMessage("test msg: " +i);
            log.info("Making request: " + msg);
            TextMessage result = (TextMessage) requestor.request(msg);
            assertNotNull(result);
            log.info("Received result: " + result.getText());
        }
    }
    
    public void onMessage(Message msg){
        try{
            TextMessage textMsg=(TextMessage) msg;
            String payload="REPLY: "+textMsg.getText();
            Destination replyTo;
            replyTo=msg.getJMSReplyTo();
            textMsg.clearBody();
            textMsg.setText(payload);
            log.info("Sending response: " + textMsg);
            requestServerProducer.send(replyTo,textMsg);
        }catch(JMSException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
