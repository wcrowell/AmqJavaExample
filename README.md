# AmqJavaExample

A simple producer and consumer written in Java running on ActiveMQ.

Requirements:

Apache Maven (tested with 3.9.3)
Git (tested with 2.47.1)
Java 17 (tested with OpenJDK 17.0.15+6-LTS)
ActiveMQ Server (tested with 6.1.7

Description of lab goes here…

Start ActiveMQ: <code>${ACTIVEMQ_HOME}/bin/activemq start</code>

<code>git clone https://github.com/wcrowell/AmqJavaExample</code>

Set JAVA_HOME:

MacOS:
<code>export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home</code>
Windows (unless already set in Control Panel > System > Advanced system settings > Advanced > Environment Variables):
<code>setx /m JAVA_HOME "C:\Program Files\Java\jdk-17"</code>

<code>mvn clean install</code>

Fire up 2 consumers in separate command line windows:

<code>
java -cp ./target/AmqJavaExample-0.0.2-SNAPSHOT.jar com.openlogic.activemq.ConsumerRunner
java -cp ./target/AmqJavaExample-0.0.2-SNAPSHOT.jar com.openlogic.activemq.ConsumerRunner
</code>

Run: <code>activemq dstat</code>

Verify 2 consumers are connected to the queue.

To run the producer: 
<code>java -cp ./target/AmqJavaExample-0.0.2-SNAPSHOT.jar com.openlogic.activemq.ProducerRunner</code>

Run: <code>activemq dstat</code>

No producer has yet connected to the broker until data is sent.  It is waiting for data to be sent.

Type something into the producer window.

Verify consumer 1 got the message.

Type something again into the producer window.

Verify consumer 2 got the message.

Run: <code>activemq dstat</code>

Output shows a producer has connected for the queue.
