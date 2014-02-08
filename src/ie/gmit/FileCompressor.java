package ie.gmit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileCompressor implements Compressor {
	// file compressor
	private File saveDir; // dir for output files

	public FileCompressor() {
		// find relative folder tmp
		saveDir = new File("tmp/");
		// if it's not exist, create one
		if (!saveDir.isDirectory())
			saveDir.mkdir();
	}

	@Override
	public String compress(String inFileName) throws Exception {

		File temFile = new File(inFileName);
		System.out.println("Compressed File: " + inFileName);
		// create outfile
		String outFileName = this.saveDir.getAbsolutePath() + "/"
				+ temFile.getName() + ".gz";
		// make a zip stream to compress
		GZIPOutputStream out = null;
		out = new GZIPOutputStream(new FileOutputStream(outFileName));
		FileInputStream in = null;
		in = new FileInputStream(inFileName);
		// read and write from inputfile to outputfile through a buffer bytes
		// this buff array could be improved by using PureBAOS class, but I
		// haven't fully
		// understand the GZIP mechanism
		byte[] buf = new byte[20480];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.finish();
		out.close();
		System.out.println("To: " + outFileName);
		return outFileName;
	}

	@Override
	public String decompress(String inFileName) throws Exception {
		// the input extension must be .gz
		if (!getExtension(inFileName).equalsIgnoreCase("gz")) {
			return "Your File has wrong extension.";
		}

		System.out.println("Decompressed file: " + inFileName);
		GZIPInputStream in = null;

		in = new GZIPInputStream(new FileInputStream(inFileName));
		FileOutputStream out = null;
		File temFile = new File(inFileName);
		// remove the extension and put the new file into tmp folder
		String outFileName = this.saveDir.getAbsolutePath() + "/"
				+ getFileName(temFile.getName());
		out = new FileOutputStream(outFileName);
		// read and write from inputfile to outputfile through a buffer bytes
		// this buff array could be improved by using PureBAOS class, but I
		// haven't fully
		// understand the GZIP mechanism
		byte[] buf = new byte[20480];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		System.out.println("To: " + outFileName);
		in.close();
		out.close();
		return outFileName;
	}

	private String getExtension(String f) {
		// get extension of a file
		String ext = "";
		int i = f.lastIndexOf('.');

		if (i > 0 && i < f.length() - 1) {
			ext = f.substring(i + 1);
		}
		return ext;
	}

	private String getFileName(String f) {
		// get file name of a file
		String fname = "";
		int i = f.lastIndexOf('.');

		if (i > 0 && i < f.length() - 1) {
			fname = f.substring(0, i);
		}
		return fname;
	}

}