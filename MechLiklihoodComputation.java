package demos;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class MechLiklihoodComputation {
	
	public static Map<String, Double> reNormalizedMechProb(Map<String, Double> finalProbMech) {
		Map<String, Double> MechLiklihood = new HashMap<String, Double>();

		if (!finalProbMech.isEmpty()) {
			double min = Collections.min(finalProbMech.values());

			double max = Collections.max(finalProbMech.values());

		} else {
			System.out.println("List is empty there is no softfaction has been executed");
		}

		double sumOfAllMech = 0.0;
		// Step 1 : Normalize the value in such way that minimum value become 0 and
		// maximum value become 1 : this will be used if we have negative values in the
		// list
//			for (Entry<String, Double> f : finalProbMech.entrySet()) {
//
//				double v = (f.getValue() - min) / (max - min);
//				finalProbMech.put(f.getKey(), v);
//
//			}
		// Calculate the sum of all the values
		for (Entry<String, Double> f : finalProbMech.entrySet()) {
			sumOfAllMech = f.getValue() + sumOfAllMech;
		}
		double normalizedValue = 1 / sumOfAllMech;
		// Step 2 : how normalize the values in way that sum of the probabilities should
		// be 1 values * normalizedValue

		for (Entry<String, Double> f : finalProbMech.entrySet()) {

			double valueW = Double.parseDouble(new DecimalFormat("##.##").format(f.getValue() * normalizedValue));
			// System.out.println("Normalized Values " + f.getKey() + " " + valueW);
			MechLiklihood.put(f.getKey(), valueW);

		}

		
		return MechLiklihood;
	}
}
