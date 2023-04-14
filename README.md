# AmqJavaExample

A simple producer and consumer written in Java running on ActiveMQ.

Requirements:

Apache Maven (tested with 3.8.7)
Git (tested with 2.39.2)
Java 1.8 (tested with OpenJDK 1.8.0_362)
ActiveMQ Server (tested with 5.18.0, but example uses 5.16.6 due to Java 1.8 requirement)

Description of lab goes here…

Start ActiveMQ: <code>${ACTIVEMQ_HOME}/bin/activemq start</code>

<code>git clone https://github.com/wcrowell/AmqJavaExample</code>

Set JAVA_HOME:

MacOS:
<code>export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-8.jdk/Contents/Home</code>
Windows (unless already set in Control Panel > System > Advanced system settings > Advanced > Environment Variables):
<code>setx /m JAVA_HOME "C:\Program Files\Java\jdk1.8.0"</code>

<code>mvn clean install</code>

Fire up 2 consumers in separate command line windows:

<code>
java -cp ./target/AmqJavaExample-0.0.1-SNAPSHOT.jar com.openlogic.activemq.ConsumerRunner
java -cp ./target/AmqJavaExample-0.0.1-SNAPSHOT.jar com.openlogic.activemq.ConsumerRunner
</code>

Run: <code>activemq dstat</code>

Verify 2 consumers are connected to the queue.

To run the producer: 
<code>java -cp ./target/AmqJavaExample-0.0.1-SNAPSHOT.jar com.openlogic.activemq.ProducerRunner</code>

Run: <code>activemq dstat</code>

No producer has yet connected to the broker until data is sent.  It is waiting for data to be sent.

Type something into the producer window.

Verify consumer 1 got the message.

Type something again into the producer window.

Verify consumer 2 got the message.

Run: <code>activemq dstat<code>

Output shows a producer has connected for the queue.
