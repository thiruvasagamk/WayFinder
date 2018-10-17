import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class PathFinder  {
  private Dijkstra dijkstra;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
	    
  }

  public static Dijkstra readGraph(String vertexFile, String edgeFile) {

    Dijkstra dijkstra = new Dijkstra();
    try {
      String line;
      // Read in the vertices
      BufferedReader vertexFileBr = new BufferedReader(new FileReader(vertexFile));
      while ((line = vertexFileBr.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length != 3) {
          vertexFileBr.close();
          throw new IOException("Invalid line in vertex file " + line);
        }
        String cityname = parts[0];
        int x = Integer.valueOf(parts[1]);
        int y = Integer.valueOf(parts[2]);
        Vertex vertex = new Vertex(cityname, x, y);
        dijkstra.addVertex(vertex);
      }
      vertexFileBr.close();
      // Now read in the edges
      BufferedReader edgeFileBr = new BufferedReader(new FileReader(edgeFile));
      while ((line = edgeFileBr.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length != 3) {
          edgeFileBr.close();
          throw new IOException("Invalid line in edge file " + line);
        }
        dijkstra.addUndirectedEdge(parts[0], parts[1], Double.parseDouble(parts[2]));
      }
      edgeFileBr.close();
    } catch (IOException e) {
      System.err.println("Could not read the dijkstra: " + e);
      return null;
    }
    return dijkstra;
  }

  /**
   * Create the frame.
   */
  public PathFinder(String coordinatesXYFile,String coordinatesPairFile,String outputShortestPathFile) {
    dijkstra = readGraph(coordinatesXYFile, coordinatesPairFile);
    
    List<String> cityNameList = new ArrayList<>();
    for (Vertex v : dijkstra.getVertices())
        cityNameList.add(v.name);
    Collections.sort(cityNameList);
    String[] fromPoints = cityNameList.toArray(new String[cityNameList.size()]);
    String[] toPoints = cityNameList.toArray(new String[cityNameList.size()]);
    Path path = Paths.get(outputShortestPathFile+ListFilesUtil.FLOORMAP_PATH_DETAILS_FILENAME);
		try {
			BufferedWriter pathWriter = Files.newBufferedWriter(path);
			pathWriter.write("{");
	        for(int i = 0 ; i < fromPoints.length;  i++ ) {
	        	String startPoint = fromPoints[i];
	        	for(int j = 0 ; j < toPoints.length;  j++ ) {
	            	String endPoint = toPoints[j];
	            	if(startPoint.equals(endPoint))continue;
	            	List<Edge> weightedPathPoints = dijkstra.getDijkstraPath(startPoint, endPoint);
	            	//System.out.println("For Start Point" + startPoint + " and to the end point "+ endPoint + " weight is " + weightedPathPoints);
	            	int count=0;
	            	StringBuffer pathBuffer = new StringBuffer();
	            	pathBuffer.append("\"");
                	pathBuffer.append(startPoint);
                	pathBuffer.append("-");
                	pathBuffer.append(endPoint);
                	pathBuffer.append("\"");
                	pathBuffer.append(":");
                	pathBuffer.append("\"");
	            	for (Edge edge : weightedPathPoints) {
	            		count++;
	                	pathBuffer.append(edge.source.name);
	                	if(count!=weightedPathPoints.size())
	                		pathBuffer.append("-");
	        		}
	            	pathBuffer.append("\"");
	            	if(i!=fromPoints.length-1 || j!=toPoints.length-2) {
	            		pathBuffer.append(",");
	            	}
	            	pathWriter.write(pathBuffer.toString());
	            	System.out.println("For Start Point" + startPoint + " and to the end point "+ endPoint + " path is " + pathBuffer.toString());
	            	
	    	    }
	        	
		    }
	        pathWriter.write("}");
        	pathWriter.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
      }


  private void updateGraphPanel() {
    String vertexFile = "cityxtyfile.txt";
    String edgeFile = "citypairsfile.txt";
    dijkstra = readGraph(vertexFile, edgeFile);
    System.out.println("Constructing new file from " + vertexFile + " and " + edgeFile);

    List<String> cityNameList = new ArrayList<>();
    for (Vertex v : dijkstra.getVertices())
        cityNameList.add(v.name);
    Collections.sort(cityNameList);
    String[] cityNames = cityNameList.toArray(new String[cityNameList.size()]);
  }

}
