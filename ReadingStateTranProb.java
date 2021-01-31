package demos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ReadingStateTranProb {
	String getTransitionProb(int offset,String actionName) throws IOException {
		
		Path path = Paths.get("D:\\prism-master\\StateTranProb\\"+actionName);
		List<String> totalTransitions = Files.readAllLines(path, StandardCharsets.UTF_8);
		String StateTran = totalTransitions.get(offset);
		String[] varValue = StateTran.split(":");
		String Values = varValue[0].trim();
		String[] eachValue = Values.split(",");
		String prob = varValue[1].trim();
		return prob;
		
	
		
}
}
