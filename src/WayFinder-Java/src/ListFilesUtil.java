
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Contains some methods to list files and folders from a directory
 *
 * @author Loiane Groner http://loiane.com (Portuguese) http://loianegroner.com
 *         (English)
 */
public class ListFilesUtil {
	//Step1 - Browser will generate this file
	final static String FLOORMAP_WAY_DETAILS_FILENAME = "-FloorMapWayDetails"; 
	
	//Step2 - File Processor will create these files
	final static String FLOORMAP_COORDINATE_XY_FILENAME = "-coordinatesXY.txt";
	final static String FLOORMAP_COORDINATE_PARIS_FILENAME = "-coordinatesPair.txt";
	final static String FLOORMAP_COORDINATE_XY_JSON_FILENAME = "-coordinatexyJSON.json";
	
	//Step3 - File Processor will create this last shortest path file
	final static String FLOORMAP_PATH_DETAILS_FILENAME = "-pathJson.json";
	
	/**
	 * List all the files and folders from a directory
	 * 
	 * @param directoryName
	 *            to be listed
	 */
	public void listFilesAndFolders(String directoryName) {
		File directory = new File(directoryName);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			System.out.println(file.getName());
		}
	}

/*	*//**
	 * List all the files under a directory
	 * 
	 * @param directoryName
	 *            to be listed
	 *//*
	public void listFiles(String directoryName) {
		File directory = new File(directoryName);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				System.out.println(file.getName());
			}
		}
	}*/
	
	public List<String> listFiles(String directoryName) {
		List<String> fileNames = new ArrayList<String>();
		File directory = new File(directoryName);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				if(file.getName().contains(FLOORMAP_WAY_DETAILS_FILENAME)) {
					fileNames.add(file.getName());
				}
			}
		}
		return fileNames;
	}
	
	public Map<String,ArrayList<String>> listCoordinateXYAndPairsFile(String directoryName) {
		Map<String,ArrayList<String>> filePairDetails = new HashMap<String,ArrayList<String>>()
;		ArrayList<String> fileNames = null;
		File directory = new File(directoryName);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				if(file.getName().contains(FLOORMAP_COORDINATE_XY_FILENAME) || file.getName().contains(FLOORMAP_COORDINATE_PARIS_FILENAME)) {
					String fileName = file.getName();
					String filePrefix="";
					if(file.getName().contains(FLOORMAP_COORDINATE_XY_FILENAME)) {
						filePrefix= fileName.split(FLOORMAP_COORDINATE_XY_FILENAME)[0];
					}
					if(file.getName().contains(FLOORMAP_COORDINATE_PARIS_FILENAME)) {
						filePrefix= fileName.split(FLOORMAP_COORDINATE_PARIS_FILENAME)[0];
					}
					
					if(filePairDetails.containsKey(filePrefix)) {
						filePairDetails.get(filePrefix).add(file.getName());
					}else {
						fileNames = new ArrayList<String>();
						fileNames.add(file.getName());
						filePairDetails.put(filePrefix, fileNames);
					}
				}
			}
		}
		return filePairDetails;
	}

	/**
	 * List all the folder under a directory
	 * 
	 * @param directoryName
	 *            to be listed
	 */
	public void listFolders(String directoryName) {
		File directory = new File(directoryName);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isDirectory()) {
				System.out.println(file.getName());
			}
		}
	}

	/**
	 * List all files from a directory and its subdirectories
	 * 
	 * @param directoryName
	 *            to be listed
	 */
	public void listFilesAndFilesSubDirectories(String directoryName) {
		File directory = new File(directoryName);
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				System.out.println(file.getAbsolutePath());
			} else if (file.isDirectory()) {
				listFilesAndFilesSubDirectories(file.getAbsolutePath());
			}
		}
	}

	public static void main(String[] args) {
		ListFilesUtil listFilesUtil = new ListFilesUtil();
		final String directoryWindows = Paths.get(".").toAbsolutePath().normalize().toString();
		System.out.println(directoryWindows);
		List<String> fileNames = listFilesUtil.listFiles(directoryWindows);
		for (String fileName : fileNames) {
			String filePrefix= fileName.split(FLOORMAP_WAY_DETAILS_FILENAME)[0];
			System.out.println(fileName  + "Prefix" + filePrefix);
		}
		
		Map<String,ArrayList<String>> map = listFilesUtil.listCoordinateXYAndPairsFile(directoryWindows);
		Iterator entries = map.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();
		    String key = entry.getKey().toString();
		    ArrayList<String> value = (ArrayList<String>)entry.getValue();
		    System.out.println("Key = " + key + ", Value = " + value);
		}
		
	}
}