package java.utils;

//Import all needed packages

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	private List<String> fileList;
	private String outpuZipFile ;//= "xsa.app.zip";
	private String sourceFolder;// = "libfolder/libfolder/xsa.app"; // SourceFolder
																// path

	public ZipUtils(String outputZipFile, String sourceFolder) {
		this.outpuZipFile = outputZipFile;
		this.sourceFolder = sourceFolder;
		fileList = new ArrayList<String>();
		generateFileList(new File(sourceFolder));
		zipIt(outpuZipFile);
	}

	private ZipUtils() {
		new Exception("not supported");
	}
	public static void main(String[] args) {
		ZipUtils appZip = new ZipUtils("xsa.app.zip","libfolder/xsa.app" );
	
	}

	public void zipIt(String zipFile) {
		System.out.println("file zip file is: " + zipFile);
		byte[] buffer = new byte[1024];
		String source = "";
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			try {
				source = sourceFolder.substring(
						sourceFolder.lastIndexOf("\\") + 1,
						sourceFolder.length());
			} catch (Exception e) {
				source = sourceFolder;
			}
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);

			//System.out.println("Output to Zip : " + zipFile);
			FileInputStream in = null;

			for (String file : this.fileList) {
				System.out.println("File Added : " + file);
				ZipEntry ze = new ZipEntry(  file);
				zos.putNextEntry(ze);
				try {
					in = new FileInputStream(sourceFolder + File.separator
							+ file);
					int len;
					while ((len = in.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
				} finally {
					in.close();
				}
			}

			zos.closeEntry();
			System.out.println("Folder successfully compressed");

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				zos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void generateFileList(File node) {
		//System.out.println("file folder is: " + node.getName());
		// add file only
	//	outpuZipFile = node.getName();
		if (node.isFile()) {
			fileList.add(generateZipEntry(node.toString()));

		}

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileList(new File(node, filename));
			}
		}
	}

	private String generateZipEntry(String file) {
		return file.substring(sourceFolder.length() + 1, file.length());
	}
}