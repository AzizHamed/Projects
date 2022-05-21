package control;

import ocsf.server.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;

import boundary.fxmlControllers.ServerViewController;
import entity.MyMessage;
import entity.MyMessage.MessageType;
import entity.Order;
import entity.User;

public class ServerController extends ObservableServer {
	// Class variables *************************************************

	/**
	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;

	/**
	 * The server's IP Address
	 */
	private InetAddress ip;

	/**
	 * Server Host Name
	 */
	public static String host;

	private static boolean serverState;
	// Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 */
	public ServerController(int port) {
		super(port);

		try {
			ip = InetAddress.getLocalHost();
			host = ip.getHostName();
			MainController.print(getClass(), "Your current IP address : " + ip);
			MainController.print(getClass(), "Your current Hostname : " + host);

		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	// Getters********************************

	public String getIP() {
		return ip.getHostAddress();
	}

	// Instance methods ************************************************

	/**
	 * This method is responsible for the creation of the server instance (there is
	 * no UI in this phase).
	 * 
	 * @throws Exception
	 */
	public void startServer() throws Exception {
		try {
			DBController.connectDB();
			listen(); // Start listening for connections
			serverState = true;
		} catch (SQLException ex) {
			throw ex;
		} catch (Exception ex) {
			MainController.printErr(getClass(), "ERROR - Could not listen for clients!");
			throw ex;

		}
	}

	/**
	 * This method is responsible for the creation of the server instance (there is
	 * no UI in this phase).
	 */
	public void stopServer() {
		if (!serverState)
			return;
		try {
			sendToAllClients(new MyMessage(null, 0, MessageType.INFO, "/global/stop", null));
			close();
			serverState = false;
			DBController.disconnectDB();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		super.serverStarted();
		MainController.print(ServerController.class, "Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		super.serverStopped();
		MainController.print(ServerController.class, "Server has stopped listening for connections.");
	}

	@Override
	protected synchronized void clientConnected(ConnectionToClient client) {
		super.clientConnected(client);
		ServerViewController.addClient(client);
		MainController.print(getClass(), "New Client Connected: " + client.getInetAddress());
	}

	@Override
	protected synchronized void clientDisconnected(ConnectionToClient client) {
		super.clientDisconnected(client);
	}

	@Override
	public void sendToAllClients(Object msg) {
		MainController.print(getClass(), "-> " + msg);
		super.sendToAllClients(msg);
	}

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		super.handleMessageFromClient(msg, client);
		MainController.print(getClass(), "<- " + msg + " from " + client);
		handleRequest(msg, client);
	}

	/**
	 * handleRequests handles requests from clients in HTTP style
	 */
	private void handleRequest(Object msg, ConnectionToClient client) {
		if (!(msg instanceof MyMessage))
			return;
		MyMessage clMsg = (MyMessage) msg;
		switch (clMsg.getType()) {
		case INFO:
			handleInfoMessage(clMsg, client);
			break;
		case GET:
			handleGetRequest(clMsg, client);
			break;
		case POST:

			break;
		case UPDATE:
			handleUpdateRequest(clMsg);
			break;
		case DELETE:

			break;
		default:
			MainController.printErr(getClass(), "Unhandled Client Request: " + clMsg.toString());
			break;
		}

	}

	private void handleInfoMessage(MyMessage clMsg, ConnectionToClient client) {
		if (clMsg.getInfo().startsWith("/disconnect")) {
			MainController.print(getClass(), "Client Disconnected: " + client.toString());
			ServerViewController.removeClient(client);
			sendToAllClients(clMsg);

		} else if (clMsg.getInfo().startsWith("/connect")) {
			MainController.print(getClass(), "Client Connected: " + client.toString());
			try {
				client.sendToClient(clMsg);
			} catch (IOException e) {
				MainController.printErr(getClass(),
						"Could not send to client: " + client.getInetAddress().getHostName());
			}

		} else {
			MainController.printErr(getClass(), "Unhandled info Message: " + clMsg.getInfo());
		}
	}

	/**
	 * handles clients' GET requests
	 * 
	 * @param MyMessage Contains GET request
	 */

	private void handleGetRequest(MyMessage clMsg, ConnectionToClient client) {

		String[] request = clMsg.getInfo().split("/");
		
		
		if (request[0].equals("login")) {
			User user = (User) clMsg.getContent();
			if (request[1].equals("user")) {
				clMsg.setContent(DBController.getUser(user.getUsername(), user.getPassword()));

			}
			if (request[1].equals("customer")) {
				clMsg.setContent(DBController.getCustomer(user));
			}
		} 
		
		
		else if (request[0].equals("order")) {
			
			
			if (request[1].equals("all")) {
				clMsg.setContent(DBController.getOrdersAll());
			} 
			
			else if (request[1].equals("by")) {
				clMsg.setContent(DBController.getOrdersBy(request[2], request[3]));
			} 
			
			
			else if(request[1].equals("report")) { 

				
				if(request[2].equals("sale")) {
					
					
					if(request[3].equals("months")) {
						clMsg.setContent(DBController.getMonthsInBranch(request[4]));
					}
				}
			}
		}
		
		
		else if (request[0].equals("item")) {
			if (request[1].equals("all")) {
				clMsg.setContent(DBController.getItemsAll());
			} else if (request[1].equals("by")) {
				clMsg.setContent(DBController.getItemsBy(request[2], request[3]));
			}
		} else if(request[0].equals("build_item")) {
			/**********************************************************************************************/
		} else if(request[0].equals("item_in_build")) {
			/**********************************************************************************************/
			
		}
		else {
			MainController.printErr(getClass(), "Unhandled Get request: " + clMsg.getInfo());
		}

		try {
			client.sendToClient(clMsg);
		} catch (IOException e) {
			sendToAllClients(clMsg);
		}
	}

	/**
	 * handles clients' UPDATE requests
	 * 
	 * @param MyMessage Contains Update request and updated object
	 */
	private void handleUpdateRequest(MyMessage clMsg) {
		if (clMsg.getInfo().startsWith("/order")) {
			Order order = (Order) clMsg.getContent();

			ArrayList<Order> list = new ArrayList<>();

			switch (clMsg.getInfo()) {
			case "/order/color":
				// list.add(DBController.updateOrder(order, "color",order.getColor()));
				break;
			case "/order/delivery_date":
				// list.add(DBController.updateOrder(order, "date",order.getDeliveryDate()));
				break;

			default:
				MainController.printErr(getClass(), "Unhandled Order Update Parameter: " + clMsg.getInfo());
				break;
			}
			clMsg.setContent(list);
		} else {
			MainController.printErr(getClass(), "Unhandled Update request: " + clMsg.getInfo());
		}

		sendToAllClients(clMsg);
	}
}