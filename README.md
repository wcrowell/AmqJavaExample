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

Show 2 consumers connected to the queue.

To run the producer: 
<code>java -cp ./target/AmqJavaExample-0.0.1-SNAPSHOT.jar com.openlogic.activemq.ProducerRunner</code>

Run: <code>activemq dstat</code>

Show that no producer has yet connected to the broker until data is sent.  It is waiting for data to be sent.

Type something into the producer window.

Show consumer 1 got it.

Type something again into the producer window.

Show consumer 2 got it.

Run: <code>activemq dstat<code>

Then show a producer has connected for the queue.


![image](https://user-images.githubusercontent.com/18705165/232038752-8d87576c-6ca9-4fb5-8c95-abc1c2142f7d.png)
