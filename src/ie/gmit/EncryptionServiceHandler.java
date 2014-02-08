package ie.gmit;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

public class EncryptionServiceHandler extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File tempDir; // temp dir for file upload transfer
	private File saveDir; // save uploaded file
	private String fileName; // uploaded file path

	public void init() {
		// Get the file location where it would be stored.
		tempDir = new File(getServletContext().getRealPath("/") + "/tmp/");
		saveDir = new File(getServletContext().getRealPath("/") + "/data/");
		// make dir if they are not exsit
		if (!tempDir.isDirectory())
			tempDir.mkdir();
		if (!saveDir.isDirectory())
			saveDir.mkdir();
	}

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html"); // Set the MIME type of our HTTP
											// response
		PrintWriter out = resp.getWriter(); // Use a PrintWriter to build the
											// output to the client browser
		out.print("<html><head><title>Pending Page</title></head><body>");

		Queue<MessagePack> inQu = (Queue<MessagePack>) getServletContext()
				.getAttribute("inqueue");
		Map<String, String> outQu = (Map<String, String>) getServletContext()
				.getAttribute("outqueue");
		String inputValue = "";
		String inputKey = "";
		int act = 9; // set default action to 9
		// check if the content is multipart
		if (ServletFileUpload.isMultipartContent(req)) {
			try {
				// set buffer dir
				DiskFileItemFactory dff = new DiskFileItemFactory();
				// double check tempDIR, so server manager can delete the folder
				// any time
				if (!tempDir.isDirectory())
					tempDir.mkdir();
				dff.setRepository(tempDir);
				dff.setSizeThreshold(10240000);
				ServletFileUpload sfu = new ServletFileUpload(dff);
				sfu.setFileSizeMax(50000000);
				sfu.setSizeMax(10000000);
				FileItemIterator fii = sfu.getItemIterator(req);
				fileName = "";
				while (fii.hasNext()) {// read file items
					FileItemStream fis = fii.next();
					if (fis.isFormField()) {// if the data is field value
						String fName = fis.getFieldName(); // get field name
						BufferedInputStream inStream = new BufferedInputStream(
								fis.openStream());
						byte[] tempB = new byte[1024];
						inStream.read(tempB);
						String fValue = new String(tempB); // get its value
						// get User data from JSP page
						if (fName.contains("txtMessage")) {
							String message = null;
							if (fValue != null) {
								message = fValue.replaceAll("\\s+", "");// remove
																		// all
																		// invisible
																		// characters
							}
							inputValue = message;
						}

						if (fName.contains("txtKey")) {
							if (fValue == "" && fValue != null) {
								inputKey = null;
							} else {
								inputKey = fValue;
							}

						}

						if (fName.contains("cmbOperation")) {
							if (fValue != null && fValue.length() > 0
									&& fValue.trim() != "") {
								switch (fValue.toLowerCase().trim()) {
								default:
									act = 0;
									break;
								case "decrypt":
									act = 1;
									break;
								case "compress":
									act = 2;
									break;
								case "decompress":
									act = 3;
									break;
								case "encryptandcompress":
									act = 4;
									break;
								case "decompressanddecrypt":
									act = 5;
									break;
								}

							}
						}
					}
					// if the data is a file and not empty
					if (!fis.isFormField() && fis.getName().length() > 0) {
						// get filepath
						fileName = fis.getName();
						String uploadPath = saveDir.getAbsolutePath();
						// double check saveDIR, so server manager can delete
						// the folder
						// any time
						if (!saveDir.isDirectory())
							saveDir.mkdirs();

						// rename file to ensure it's unique
						fileName = fileName.substring(fileName.indexOf("."));
						Date time = new Date();
						String dynamicName = uploadPath + "/"
								+ String.valueOf(time.getTime());
						String tmpFileName = dynamicName + fileName;
						File tmpFile = new File(tmpFileName);
						// if the file exist add 'c' to filename
						while (tmpFile.exists()) {
							dynamicName += "c";
							tmpFileName = dynamicName + fileName;
							tmpFile = new File(tmpFileName);
						}
						fileName = tmpFileName;
						BufferedInputStream inStream = new BufferedInputStream(
								fis.openStream());
						BufferedOutputStream outStream = new BufferedOutputStream(
								new FileOutputStream(new File(fileName)));
						Streams.copy(inStream, outStream, true);
					}

				}

			} catch (Exception e) {
				out.println(e.getMessage());
			}

		}
		if (act != 9) { // got the value of operation
			MessagePack mp = new MessagePack();
			// generate unique id
			String id = Calendar.getInstance().getTime().toString();
			// add 'c' to id if exist
			while (outQu.containsKey(id)) {
				id += "c";
			}
			mp.setId(id);
			mp.setAction(act);
			mp.setFileName(fileName);
			mp.setInput(inputValue);
			mp.setKey(inputKey);
			inQu.add(mp);
			out.print("<H1>Check your result at:</H1><a href=\"http://localhost:8080/ds/bootstrap?id="
					+ mp.getId() + "\">HERE</a><br/>");
		} else {// can't get operation, user may jumped to this page
			out.print("<H1>Please start from:<a href=\"http://localhost:8080/ds/\">Homepage</a></H1>");
		}
		out.print("</body></html>");
	}

	// If anyone issues a POST request, dispatch the request and response
	// objects to the GET method
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doGet(req, resp);

	}
}
