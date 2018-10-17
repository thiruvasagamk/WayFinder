import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FloorMapFileProcessor {

	public void fmEditorFileReader(String floorMapWayDetailsFile,String filePrefix) {
		try {
			BufferedReader editorOPFileBr = new BufferedReader(new FileReader(floorMapWayDetailsFile));
			String line;
			Path path = Paths.get(filePrefix+ListFilesUtil.FLOORMAP_COORDINATE_XY_FILENAME);
			BufferedWriter coordinatesWriter = Files.newBufferedWriter(path);
			
			Path coordinateJsonFile = Paths.get(filePrefix+ListFilesUtil.FLOORMAP_COORDINATE_XY_JSON_FILENAME);
			BufferedWriter coordinateJsonWriter = Files.newBufferedWriter(coordinateJsonFile);
			
			Path coordinatesPair = Paths.get(filePrefix+ListFilesUtil.FLOORMAP_COORDINATE_PARIS_FILENAME);
			BufferedWriter coordinatesPairWriter = Files.newBufferedWriter(coordinatesPair);
			
			
			boolean isEOD = false;
			coordinateJsonWriter.write("{");
			while ((line = editorOPFileBr.readLine()) != null) {
				StringBuffer coordinatesBr = new StringBuffer();
				StringBuffer coordinatesJSONBr = new StringBuffer();
				StringBuffer coordinatesPairBr = new StringBuffer();
				
				if(line.contains("EOD")) {
					isEOD= true;
				}
				//System.out.println("line"+line);
				String[] parts = line.split(",");
				if (parts.length<4) {
					editorOPFileBr.close();
					throw new IOException("Invalid line in WFEditorOutput file " + line);
				}
				
				if(parts.length>=5) {
					//System.out.println(parts[0] + parts[1] + parts[2] + parts[3] + parts[4]);
					coordinatesBr.append(parts[1]);
					coordinatesBr.append(",");
					coordinatesBr.append(parts[2]);
					coordinatesBr.append(",");
					coordinatesBr.append(parts[3]);
					
					coordinatesJSONBr.append("\"");
					coordinatesJSONBr.append(parts[1]);
					coordinatesJSONBr.append("\"");
					coordinatesJSONBr.append(":");
					coordinatesJSONBr.append("\"");
					coordinatesJSONBr.append(parts[2]);
					coordinatesJSONBr.append("#");
					coordinatesJSONBr.append(parts[3]);
					coordinatesJSONBr.append("#");
					coordinatesJSONBr.append(parts[0]);
					coordinatesJSONBr.append("#");
					coordinatesJSONBr.append(parts[4]);
					coordinatesJSONBr.append("\"");
					
					if(!parts[4].equals("NA")) {
						String[] links = parts[4].split("-");
						StringBuffer pairs= new StringBuffer();
						for(int i = 0;i<links.length;i++) {
							pairs.append(parts[1]+","+links[i]+","+"200");
							if(i==links.length-1 && isEOD) {
								//System.out.println("yes last line"+parts[4]);
							}else {
								pairs.append("\n");
							}
						}
						coordinatesPairBr.append(pairs);
					}
					
				}else  {
					//System.out.println(parts[0] + parts[1] + parts[2] + parts[3]);
					coordinatesBr.append(parts[1]);
					coordinatesBr.append(",");
					coordinatesBr.append(parts[2]);
					coordinatesBr.append(",");
					coordinatesBr.append(parts[3]);
					
					coordinatesJSONBr.append("\"");
					coordinatesJSONBr.append(parts[1]);
					coordinatesJSONBr.append("\"");
					coordinatesJSONBr.append(":");
					coordinatesJSONBr.append("\"");
					coordinatesJSONBr.append(parts[2]);
					coordinatesJSONBr.append("#");
					coordinatesJSONBr.append(parts[3]);
					coordinatesJSONBr.append("#");
					coordinatesJSONBr.append(parts[0]);
					coordinatesJSONBr.append("#");
					coordinatesJSONBr.append("NA");
					coordinatesJSONBr.append("\"");
				}
				coordinatesPairWriter.write(coordinatesPairBr.toString());
				coordinatesWriter.write(coordinatesBr.toString());
				coordinateJsonWriter.write(coordinatesJSONBr.toString());
				if(!isEOD) {
					//coordinatesPairWriter.write("\n");
					coordinatesWriter.write("\n");	
					coordinateJsonWriter.write(",");
				}	
			}
			coordinatesPairWriter.close();
			coordinateJsonWriter.write("}");
			coordinateJsonWriter.close();
			coordinatesWriter.close();
			editorOPFileBr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		ListFilesUtil listFilesUtil = new ListFilesUtil();
		final String directoryWindows = Paths.get(".").toAbsolutePath().normalize().toString();
		//System.out.println(directoryWindows);
		List<String> fileNames = listFilesUtil.listFiles(directoryWindows);
		for (String floorMapWayFile : fileNames) {
			//System.out.println(floorMapWayFile);
			String filePrefix= floorMapWayFile.split(ListFilesUtil.FLOORMAP_WAY_DETAILS_FILENAME)[0];
			//System.out.println(floorMapWayFile  + "Prefix" + filePrefix);
			FloorMapFileProcessor fileProcessor = new FloorMapFileProcessor();
			fileProcessor.fmEditorFileReader(floorMapWayFile,filePrefix);
		}

		Map<String,ArrayList<String>> map = listFilesUtil.listCoordinateXYAndPairsFile(directoryWindows);
		Iterator entries = map.entrySet().iterator();
		while (entries.hasNext()) {
		    Map.Entry entry = (Map.Entry) entries.next();
		    String key = entry.getKey().toString();
		    ArrayList<String> value = (ArrayList<String>)entry.getValue();
		    System.out.println("Key = " + key + ", Value = " + value);
		    
		    String coordinatesXYFile ="";
		    if(value.get(0).contains(ListFilesUtil.FLOORMAP_COORDINATE_XY_FILENAME)) {
		    	coordinatesXYFile = value.get(0);
		    }else {
		    	coordinatesXYFile = value.get(1);
		    }
			String coordinatesPairFile  = "";
		    if(value.get(0).contains(ListFilesUtil.FLOORMAP_COORDINATE_PARIS_FILENAME)) {
		    	coordinatesPairFile = value.get(0);
		    }else {
		    	coordinatesPairFile = value.get(1);
		    }
			String outputShortestPathFile = key;
			System.out.println("Generating path for "+ coordinatesXYFile + "--"+ coordinatesPairFile + "--" + outputShortestPathFile);
			PathFinder pathFinder = new PathFinder(coordinatesXYFile, coordinatesPairFile, outputShortestPathFile);
		}
		
	}
}
