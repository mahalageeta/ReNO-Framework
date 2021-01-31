package demos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class NormsReading {
	static Map<String, Map<String, String>> norm = new HashMap<String, Map<String, String>>();
	static Map<String, String> normPreconditions = new HashMap<String,String>();
	// Set the agents
		static ArrayList<String> agents = new ArrayList<String>();
	public static void main(String[] args) throws IOException {
		getNormsData();
		
		
	}
	public static Map<String, Map<String, String>> getNormsData() {
		readingNormsNew();
	    return norm;
	}
	public static Map<String, String> getNormsPreconditions() {
		readingNormsNew();
	    return normPreconditions;
	}
	public static void readingNormsNew() {
		String data = "";

		try {
			File myNorm = new File("Norms");
			Scanner myNormReader = new Scanner(myNorm);
			while (myNormReader.hasNextLine()) {
				data = myNormReader.nextLine();
				normsVarExtraction(data);
			}
			myNormReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}
	public static void normsVarExtraction(String data) {

		List<String> pvariableList1 = new ArrayList<String>();
		List<String> cvariableList1 = new ArrayList<String>();
		List<String> avariableList1 = new ArrayList<String>();
		List<String> svariableList1 = new ArrayList<String>();
		List<String> pConsequentQuantities = new ArrayList<String>();
		List<String> cConsequentQuantities = new ArrayList<String>();
		List<String> aConsequentQuantities = new ArrayList<String>();
		List<String> sConsequentQuantities = new ArrayList<String>();
		Map<String, String> nConseValue = new HashMap<>();
		String subject = "";
		String object = "";
		String antecedent = "";
		String consequent = "";
		// String timeCons = "";

		// Identify norm type

		String[] normtype = data.split(Pattern.quote("="), 2);
		String nt = normtype[0];

		String[] line = normtype[1].split(Pattern.quote("("), 2);

		String normId = line[0];
		// System.out.println("norm identification = " + normId);

		String[] p = line[1].split(Pattern.quote(","), 4); // 4 to split sub,obj,ant and con
		// System.out.println("length = " + p.length);
		if (p.length == 4) {
			subject = p[0].trim();
			agents.add(subject);
			// System.out.println(subject);
			object = p[1].trim();
			agents.add(object);
			// System.out.println(object);
			antecedent = p[2].trim();
			// System.out.println("antecedent" + antecedent);
			normPreconditions.put(normId, antecedent);
			consequent = p[3].replace(")", " ").trim();
			// System.out.println("consequent = " + consequent);
			// timeCons = p[4].replace(")", " ").trim();
			// System.out.println("timeCons =" + timeCons);
		} else {
			System.out.println("Syntax is not correct, Please check Norm file for " + data);
		}
//store consequent and corresponding value
		if (normtype[0].contains("P")) {
			// extract the variable name from consequent

			if (consequent.contains(">=")) {
				String[] nv = consequent.split(">=");
				pvariableList1.add(nv[0].trim());
				pConsequentQuantities.add(nv[1].trim());
				nConseValue.put(nv[0].trim(), nv[1].trim());
				norm.put(line[0], nConseValue);

			} else if (consequent.contains("<=")) {
				String[] nv = consequent.split("<=");
				pvariableList1.add(nv[0].trim());
				pConsequentQuantities.add(nv[1].trim());
				nConseValue.put(nv[0].trim(), nv[1].trim());
				norm.put(line[0], nConseValue);
			} else {
				System.out.println("Norms Prohibition should have some quantities in consequent");
			}

		} else if (normtype[0].contains("C")) {
			// extract the variable name from consequent

			// cvariableList.add(parts[0]);
			if (consequent.contains(">=")) {
				String[] nv = consequent.split(">=");
				cvariableList1.add(nv[0].trim());
				cConsequentQuantities.add(nv[1].trim());
				nConseValue.put(nv[0].trim(), nv[1].trim());
				norm.put(line[0], nConseValue);
			} else if (consequent.contains("<=")) {
				String[] nv = consequent.split("<=");
				cvariableList1.add(nv[0].trim());
				cConsequentQuantities.add(nv[1].trim());
				nConseValue.put(nv[0].trim(), nv[1].trim());
				norm.put(line[0], nConseValue);
			} else {
				System.out.println("Norms Commitment should have some quantities in consequent");
			}

		} else if (normtype[0].contains("A")) {
			// extract the variable name from consequent
			String[] parts = consequent.split(" ");
			if (consequent.contains(">=")) {
				String[] nv = consequent.split(">=");
				avariableList1.add(nv[0].trim());
				aConsequentQuantities.add(nv[1].trim());
				nConseValue.put(nv[0].trim(), nv[1].trim());
				norm.put(line[0], nConseValue);
			} else if (consequent.contains("<=")) {
				String[] nv = consequent.split("<=");
				avariableList1.add(nv[0].trim());
				aConsequentQuantities.add(nv[1].trim());
				nConseValue.put(nv[0].trim(), nv[1].trim());
				norm.put(line[0], nConseValue);
			} else {
				System.out.println("Norms Authorization should have some quantities in consequent");
			}

		} else if (normtype[0].contains("S")) {
			// extract the variable name from consequent

			if (consequent.contains(">=")) {
				String[] nv = consequent.split(">=");
				svariableList1.add(nv[0].trim());
				sConsequentQuantities.add(nv[1].trim());
				nConseValue.put(nv[0].trim(), nv[1].trim());
				norm.put(line[0], nConseValue);
			} else if (consequent.contains("<=")) {
				String[] nv = consequent.split("<=");
				svariableList1.add(nv[0].trim());
				sConsequentQuantities.add(nv[1].trim());
				nConseValue.put(nv[0].trim(), nv[1].trim());
				norm.put(line[0], nConseValue);
			} else {
				System.out.println("Norms Sanction should have some quantities in consequent");
			}

		}
	}
}
