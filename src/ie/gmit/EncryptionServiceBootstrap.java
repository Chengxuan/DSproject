package ie.gmit;

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.tomcat.util.http.fileupload.util.Streams;

public class EncryptionServiceBootstrap extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Queue<MessagePack> inQu; // inqueue store user's request
	private static Map<String, String> outQu;// outqueue store processed result
	private static RemoteDataProcessor sdp; // remote string processor
	private static RemoteDataProcessor fdp; // remote file processor

	// Because <load-on-startup> is set to 1, this init() method will be
	// executed when Tomcat is started
	public void init() throws ServletException {
		// ServletContext represents the application context for the web
		// application
		ServletContext ctx = getServletContext();
		// Read in the values of the <context-param> elements in web.xml
		String remoteHost = ctx.getInitParameter("RMI_SERVER");
		String registryPort = ctx.getInitParameter("RMI_REGISTRY_PORT");
		String remoteObjectLookupName = ctx.getInitParameter("FACADE_NAME");

		// looking for RMI service
		try {
			RemoteDataProService rdps = (RemoteDataProService) Naming
					.lookup("rmi://" + remoteHost.trim() + ":"
							+ registryPort.trim() + "/"
							+ remoteObjectLookupName.trim());
			sdp = rdps.getStringProcessor();
			fdp = rdps.getFileProcessor();
		} catch (Exception e) {

		}
		inQu = new ConcurrentLinkedQueue<MessagePack>(); // Initialize inqueue
															// as concurrent
		outQu = new HashMap<String, String>(); // outqueue use HashMap to store
												// and retrieve data

		// Add them to the ServletContext. They are now available from ANY
		// JSP/Servlet in the web application
		ctx.setAttribute("inqueue", inQu);
		ctx.setAttribute("outqueue", outQu);

		new Thread(new QueueInspector()).start();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html"); // Set the MIME type of our HTTP
											// response
		PrintWriter out = resp.getWriter(); // Use a PrintWriter to build the
											// output to the client browser

		String detail = req.getParameter("id"); // Read in the value associated
												// with a querystring, e.g.
												// http://localhost:8080/ds//bootstrap?id=123-AA
		out.print("<html><head><title>Result Page</title></head><body>");
		// Print out some stuff to prove it works okay...
		if (outQu.containsKey(detail)) {// if there is result for this request
										// ID

			if (outQu.get(detail) != null) {// if there is a result of this ID
				out.print("<H1>Refresh this page you will lost your data!</H1>");
				out.print("<p>" + new String(outQu.get(detail)) + "</p>");

			} else {// user didn't put in any text or file
				out.print("<H1>You haven't put in any data!</H1>");
				out.print("<p>Just let you know: <b>Programmers are not Superman.</b></p>");
			}
			outQu.remove(detail);// remove the element from outQueue
		} else {
			// check if this user ID is still in the In queue
			// which means waiting to be processed
			boolean exist = false;
			while (inQu.iterator().hasNext()) {
				MessagePack mp = inQu.iterator().next();
				if (mp.getId() == detail) { // if the ID being found
					out.print("<H1>You request is being processing, please check later.</H1>");
					exist = true;
					break;
				}
			}
			if (!exist) {// if there is no matching ID in the In queue and Out
							// queue
				out.print("<H1>Sorry, we can't find your request!</H1>");
			}

		}
		// give user a link to home page
		out.print("<p><a href=\"http://localhost:8080/ds/\">Back to Homepage</a></p>");
		out.print("</body></html>");
	}

	// If anyone issues a POST request, dispatch the request and response
	// objects to the GET method
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	class QueueInspector implements Runnable {
		// this class is doing fetching from In queque; processing data; then
		// put result into the Out queue
		@Override
		public void run() {
			String remoteError = "<br/><H2>Sorry, the RMI server has been shut down.<H2><br/>";
			String stringError = "<br/><H2>String process failed, Check if you string was correctly formated.<H2><br/>";
			String fileError = "<br/><H2>File process failed, Check if you provide correct file!<H2><br/>";
			while (true) { // keep running while server is running
				if (!inQu.isEmpty()) { // when the Inqueue is not empty
					MessagePack mp = inQu.poll(); // get the first element from
													// head
					String temIn = mp.getInput().trim(); // get string
					String temFi = mp.getFileName().trim(); // get file path
					if (temIn.trim().length() > 0) {// if there is a string need
													// to be
						// processing
						String sh = "<br/><H2>Your String after processing is:</H2><p>";
						String st = "</p><br/>";
						switch (mp.getAction()) {
						case 0: // string encryption
							try {
								outQu.put(mp.getId(),
										sh + sdp.encrypt(temIn, mp.getKey())
												+ st);
							} catch (RemoteException re) {
								// RMI service Error
								outQu.put(mp.getId(), remoteError);
								// try to reBind RMI service
								this.reBindRMI();
							} catch (Exception e) {
								// String process failed
								outQu.put(mp.getId(), stringError);
							}
							break;
						case 1:// string decryption
							try {
								outQu.put(mp.getId(),
										sh + sdp.decrypt(temIn, mp.getKey())
												+ st);
							} catch (RemoteException re) {
								// RMI service Error
								outQu.put(mp.getId(), remoteError);
								// try to reBind RMI service
								this.reBindRMI();
							} catch (Exception e) {
								// String process failed
								outQu.put(mp.getId(), stringError);
							}
							break;
						case 2:// string compress
							try {
								outQu.put(mp.getId(), sh + sdp.compress(temIn)
										+ st);

							} catch (RemoteException re) {
								// RMI service Error
								outQu.put(mp.getId(), remoteError);
								// try to reBind RMI service
								this.reBindRMI();
							} catch (Exception e) {
								// String process failed
								outQu.put(mp.getId(), stringError);
							}
							break;
						case 3:// string decompress
							try {
								outQu.put(mp.getId(),
										sh + sdp.decompress(temIn) + st);
							} catch (RemoteException re) {
								// RMI service Error
								outQu.put(mp.getId(), remoteError);
								// try to reBind RMI service
								this.reBindRMI();
							} catch (Exception e) {
								// String process failed
								outQu.put(mp.getId(), stringError);
							}
							break;
						case 4:// string encrypt and compress
							try {
								String s = sdp.encrypt(temIn, mp.getKey());// get
																			// encrypted
																			// string
								String ss = sdp.compress(s);// get compresssed
															// string
								outQu.put(mp.getId(), sh + ss + st);
							} catch (RemoteException re) {
								// RMI service Error
								outQu.put(mp.getId(), remoteError);
								// try to reBind RMI service
								this.reBindRMI();
							} catch (Exception e) {
								// String process failed
								outQu.put(mp.getId(), stringError);
							}
							break;
						case 5:// string decompress and decrypt
							try {
								String s = sdp.decompress(temIn);// get
																	// decompressed
																	// string
								String ss = sdp.decrypt(s, mp.getKey());// get
																		// decrypted
																		// sting
								outQu.put(mp.getId(), sh + ss + st);
							} catch (RemoteException re) {
								// RMI service Error
								outQu.put(mp.getId(), remoteError);
								// try to reBind RMI service
								this.reBindRMI();
							} catch (Exception e) {
								// String process failed
								outQu.put(mp.getId(), stringError);
							}
							break;

						}
						if (temFi.trim().length() > 0) {
							String already = outQu.get(mp.getId());
							String fh = "<br/><H2>Please download your processed file:</H2><br/><a href=\"./data/";
							String ft = "\">Dowload File</a><br/>";
							switch (mp.getAction()) {
							case 0:// file encryption
								try {
									outQu.put(
											mp.getId(),
											already
													+ fh
													+ copyFile(fdp.encrypt(
															temFi, mp.getKey()))
													+ ft);
								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), already + remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), already + fileError);
								}
								break;
							case 1:// file decryption
								try {
									outQu.put(
											mp.getId(),
											already
													+ fh
													+ copyFile(fdp.decrypt(
															temFi, mp.getKey()))
													+ ft);
								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), already + remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), already + fileError);
								}
								break;
							case 2:// file compression
								try {
									outQu.put(mp.getId(), already + fh
											+ copyFile(fdp.compress(temFi))
											+ ft);

								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), already + remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), already + fileError);
								}
								break;
							case 3:// file decompression
								try {
									outQu.put(mp.getId(), already + fh
											+ copyFile(fdp.decompress(temFi))
											+ ft);
								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), already + remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), already + fileError);
								}
								break;
							case 4:// file encrypt and compress
								try {
									String s = fdp.encrypt(temFi, mp.getKey());// get
																				// encrypted
																				// file
																				// path
									String ss = already + fh
											+ copyFile(fdp.compress(s)) + ft;// get
																				// link
																				// to
																				// compressed
																				// file
									outQu.put(mp.getId(), ss);
								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), already + remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), already + fileError);
								}
								break;
							case 5:// file decompress and decryption
								try {
									String s = fdp.decompress(temFi);// get
																		// decompressed
																		// file
																		// path
									String ss = already
											+ fh
											+ copyFile(fdp.decrypt(s,
													mp.getKey())) + ft;// get
																		// download
																		// link
									outQu.put(mp.getId(), ss);
								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), already + remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), already + fileError);
								}
								break;

							}
						}
					} else {// if there is no input text
						if (temFi.trim().length() > 0) {// if there is input
														// file
							String h = "<br/><H2>Please download your processed file:</H2><br/><a href=\"./data/";
							String t = "\">Dowload File</a><br/>";
							switch (mp.getAction()) {
							case 0:// file encrypt
								try {
									outQu.put(
											mp.getId(),
											h
													+ copyFile(fdp.encrypt(
															temFi, mp.getKey()))
													+ t);
								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), fileError);
								}
								break;
							case 1:// file decrypt
								try {
									outQu.put(
											mp.getId(),
											h
													+ copyFile(fdp.decrypt(
															temFi, mp.getKey()))
													+ t);
								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), fileError);
								}
								break;
							case 2:// file compress
								try {
									outQu.put(mp.getId(),
											h + copyFile(fdp.compress(temFi))
													+ t);

								} catch (RemoteException re) {
									outQu.put(mp.getId(), remoteError);
								} catch (Exception e) {
									outQu.put(mp.getId(), fileError);
								}
								break;
							case 3:// file decompress
								try {
									outQu.put(mp.getId(),
											h + copyFile(fdp.decompress(temFi))
													+ t);
								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), fileError);
								}
								break;
							case 4:// file encrypt and compress
								try {
									String s = fdp.encrypt(temFi, mp.getKey());
									String ss = h + copyFile(fdp.compress(s))
											+ t;
									outQu.put(mp.getId(), ss);
								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), fileError);
								}
								break;
							case 5:// file decompress and decrypt
								try {
									String s = fdp.decompress(temFi);

									String ss = h
											+ copyFile(fdp.decrypt(s,
													mp.getKey())) + t;
									outQu.put(mp.getId(), ss);
								} catch (RemoteException re) {
									// RMI service error
									outQu.put(mp.getId(), remoteError);
									// try to reBind RMI
									this.reBindRMI();
								} catch (Exception e) {
									// file process failed
									outQu.put(mp.getId(), fileError);
								}
								break;

							}
						} else {// user put in nothing
							outQu.put(mp.getId(), null);
						}
					}
				} else {// if In queue is empty sleep to save resource
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}

				}
			}
		}

		private String copyFile(String inputFileName) throws Exception {
			// copy server local file to web service folder
			// this function is to solve "permission deny" problem
			// on UNIX-based OS
			File f = new File(inputFileName);

			String outputFile = getServletContext().getRealPath("/") + "/data/"
					+ f.getName();
			File outf = new File(outputFile);
			BufferedInputStream inStream = new BufferedInputStream(
					new FileInputStream(f));

			BufferedOutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(outf));
			Streams.copy(inStream, outStream, true);
			return outf.getName();
		}

		private void reBindRMI() {
			// ServletContext represents the application context for the web
			// application
			ServletContext ctx = getServletContext();
			// Read in the values of the <context-param> elements in web.xml
			String remoteHost = ctx.getInitParameter("RMI_SERVER");
			String registryPort = ctx.getInitParameter("RMI_REGISTRY_PORT");
			String remoteObjectLookupName = ctx.getInitParameter("FACADE_NAME");

			// looking for RMI service
			try {
				RemoteDataProService rdps = (RemoteDataProService) Naming
						.lookup("rmi://" + remoteHost.trim() + ":"
								+ registryPort.trim() + "/"
								+ remoteObjectLookupName.trim());
				sdp = rdps.getStringProcessor();
				fdp = rdps.getFileProcessor();
			} catch (Exception e) {

			}
		}

	}
}
