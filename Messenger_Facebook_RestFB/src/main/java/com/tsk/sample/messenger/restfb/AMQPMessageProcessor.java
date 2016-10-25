package com.tsk.sample.messenger.restfb;


//@formatter:off
/**
 * The interface <code>AQMPMessageProcessor</code> defines method for
 * processing message payload received from AMQP based Message Broker  
 * <code>AMPQConsumer</code>.
 * 
 * <emp>Important:</emp>The
 * <code>processMessage</code> method is called from the receiver's thread context,
 * so the implementation of the listener should ensure that there is no
 * deadloock, or at least not too much time spent in the method.
 *
 * @author Saravana Telapolu
 * @version 1.0, 24 Oct 2016
 * @see AMQPMessageProcessor
 */
//@formatter:on

public interface AMQPMessageProcessor {

  public void processMessage(byte[] messagePayLoad);

}
