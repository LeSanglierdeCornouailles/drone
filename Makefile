


server:
	javac ThreadedServer.java ThreadedConnectionHandler.java DateTimeService.java Status.java DateTimeService.java ClientConnection.java ServerFrame.java Message.java

client:
	javac Client.java ClientConnection.java Status.java DateTimeService.java ClientFrame.java Message.java

clear:
	rm -fv *.class
