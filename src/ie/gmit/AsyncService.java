package ie.gmit;

import java.net.Inet4Address;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class AsyncService {
	public static void main(String[] args) {
		if (args.length > 0) {// if there is a parameter

			// get command
			if (args.length == 2 || args.length == 4) {// if user put in 2 or 4
														// parameters

				int portNumber = 1099;
				String serviceName = "ChengEC";
				if (args.length == 2) {
					// only one settings need to be chage
					String cmd = args[0].toLowerCase();
					String settings = args[1];

					if (cmd.equalsIgnoreCase("-p")) {
						portNumber = Integer.parseInt(settings);
					}
					if (cmd.equalsIgnoreCase("-n")) {
						serviceName = settings;
					}
					try {
						// start an RMI service
						RemoteDataProService rdps = new RemoteDataProServiceImpl();
						LocateRegistry.createRegistry(portNumber);
						Naming.rebind(serviceName, rdps);
						// print out customized setting
						System.out
								.println("RMI Service start up successfully.The settings are:\n"
										+ "The IP address: "
										+ Inet4Address.getLocalHost()
												.getHostAddress()
										+ "\nPort Number: "
										+ portNumber
										+ "\nRegister Name: "
										+ serviceName
										+ "\nIf you change the default settings, you should also"
										+ "change the in web.xml of your JSP Page.");
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

				} else {
					if (args.length == 4) {
						String cmd = args[0].toLowerCase();
						String settings = args[1];
						String cmd1 = args[2].toLowerCase();
						String settings1 = args[3];
						if (!cmd.equalsIgnoreCase("-n")
								&& !cmd.equalsIgnoreCase("-p")) {
							// if the first parameter is not correct print help
							System.out
									.println("usage: locate [-p PortNumber] [-n ServiceName] settings ..."
											+ "\nOptions:\n\t-p Default:1099\n\t-n Default:ChengEC\n"
											+ "If you change the default settings, you should also"
											+ "change the in web.xml of your JSP Page.");
						} else {
							// if the first parameter is to change port number
							if (cmd.equalsIgnoreCase("-p")) {
								portNumber = Integer.parseInt(settings);

								// get next action it must try to change name
								if (cmd1.equalsIgnoreCase("-n")) {
									serviceName = settings1;
									try {
										// start an RMI service
										RemoteDataProService rdps = new RemoteDataProServiceImpl();
										LocateRegistry
												.createRegistry(portNumber);
										Naming.rebind(serviceName, rdps);
										// print out customized setting
										System.out
												.println("RMI Service start up successfully.The settings are:\n"
														+ "The IP address: "
														+ Inet4Address
																.getLocalHost()
																.getHostAddress()
														+ "\nPort Number: "
														+ portNumber
														+ "\nRegister Name: "
														+ serviceName
														+ "\nIf you change the default settings, you should also"
														+ "change the in web.xml of your JSP Page.");
									} catch (Exception e) {
										System.out.println(e.getMessage());
									}
								} else {
									// if it's not readable
									System.out
											.println("Invalid command!\n"
													+ "usage: locate [-p PortNumber] [-n ServiceName] settings ..."
													+ "\nOptions:\n\t-p Default:1099\n\t-n Default:ChengEC\n"
													+ "If you change the default settings, you should also"
													+ "change the in web.xml of your JSP Page.");
								}

							}
							// if the first command is to change the name
							if (cmd.equalsIgnoreCase("-n")) {
								serviceName = settings;

								// the second command should be to change the
								// port number
								if (cmd1.equalsIgnoreCase("-p")) {
									portNumber = Integer.parseInt(settings1);
									try {
										// start an RMI service
										RemoteDataProService rdps = new RemoteDataProServiceImpl();
										LocateRegistry
												.createRegistry(portNumber);
										Naming.rebind(serviceName, rdps);
										// print out the settings to user
										System.out
												.println("RMI Service start up successfully.The settings are:\n"
														+ "The IP address: "
														+ Inet4Address
																.getLocalHost()
																.getHostAddress()
														+ "\nPort Number: "
														+ portNumber
														+ "\nRegister Name: "
														+ serviceName
														+ "\nIf you change the default settings, you should also"
														+ "change the in web.xml of your JSP Page.");
									} catch (Exception e) {
										System.out.println(e.getMessage());
									}
								} else {
									// command is not readable
									System.out
											.println("Invalid data!\n"
													+ "usage: locate [-p PortNumber] [-n ServiceName] settings ..."
													+ "\nOptions:\n\t-p Default:1099\n\t-n Default:ChengEC\n"
													+ "If you change the default settings, you should also"
													+ "change the in web.xml of your JSP Page.");
								}

							}

						}

					}
				}
			} else {// help out
				System.out
						.println("usage: locate [-p PortNumber] [-n ServiceName] settings ..."
								+ "\nOptions:\n\t-p Default:1099\n\t-n Default:ChengEC\n"
								+ "If you change the default settings, you should also"
								+ "change the in web.xml of your JSP Page.");
			}
		} else {
			// the number of parameters are not correct run the default settings
			try {
				// start an RMI service
				RemoteDataProService rdps = new RemoteDataProServiceImpl();
				// use port 1099
				LocateRegistry.createRegistry(1099);
				// name is ChengEC
				Naming.rebind("ChengEC", rdps);
				System.out
						.println("RMI Service start up using default settings successfully.The settings are:\n"
								+ "The IP address: "
								+ Inet4Address.getLocalHost().getHostAddress()
								+ "\nPort Number: 1099\nRegister Name: ChengEC");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}