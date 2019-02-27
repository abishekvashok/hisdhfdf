import java.util.*;
import java.io.*;

public class pizza {

	public class Slice {
		int r1;
		int r2;
		int c1;
		int c2;
		int count;

		ArrayList<String> elements = new ArrayList<String>();
		Slice(int r1, int c1, int r2, int c2, int count) {
			this.r1 = r1;
			this.r2 = r2;
			this.c1 = c1;
			this.c2 = c2;
			this.count = count;

			for(int i = r1; i <= r2; i++) {
				for(int j = c1; j<=c2; j++) {
					String row = String.valueOf(i);
					String col = String.valueOf(j);
					String element = row + col;
					elements.add(element);
				}
			}
		}
	}

	ArrayList<Slice> sliceList = new ArrayList<Slice>();
	int minIngredients;

	public static void main(String args[]) {
		mainly("a_example");
		mainly("b_small");
		mainly("c_medium");
		mainly("d_large");
	}
	public static void mainly(String fileName) {
		System.out.println("------------------------------");
		System.out.println("Reading: "+fileName);
		long startTime = System.nanoTime()/1000000000;
		int rows = 0;
		int cols = 0;
		int maxCells = 0;
		pizza p = new pizza();

		File file = new File("./"+fileName+".in");
		BufferedReader bc;
		String st;
		try {
			bc = new BufferedReader(new FileReader(file));
			st = bc.readLine();
			String[] firstLine = st.split(" ", 0);
			int z = 0;
			for (String s: firstLine) {
				int y = Integer.valueOf(s);
				if(z == 0){
					rows = y;
				}else if(z == 1)
					cols = y;
				else if(z == 2)
					p.minIngredients = y;
				else
					maxCells = y;
				z = z+1;
			}	
		} catch(FileNotFoundException fnf){
			System.out.println("File not found");
			return;		
		} catch(IOException ioe) {
			System.out.println("IOEXCEPTION");
			return;
		}
		int[][] pizzaSet = new int[rows][cols];
		try{
			int n = 0;
			while((st = bc.readLine()) != null){
				String[] firstLine = st.split("", 0);
				int z = 0;
				for (String s: firstLine) {
					if(s.equals("T")){
						pizzaSet[n][z] = 0;
					}else {
						pizzaSet[n][z] = 1;
					}
					z = z+1;
				}
				n = n + 1;
			}
		} catch (IOException ioes) {
			System.out.println("IOEXCEPTION");
			return;
		}
		
		System.out.println("Generating slices...");
		// Generate permutations
		for (int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				// Standing at i,j
				// Go j+1...k to the right
				for(int k = j; k < cols && k < maxCells;k++){
					p.trySlice(i,j,i,k,pizzaSet);
					// Go right and down simultaneously
					int d = maxCells / (k-j+1); 
					// We can go i+1...d units down now (if possible)
					// since we went right already
					for(int m = i+1; m < d && m < rows; m++) {
						p.trySlice(i,j,m,k,pizzaSet);
					}
				}
				// Stanbding at i,j
				// Go i+1...l to the bottom
				for(int l = i+1; l < rows && l < maxCells; l++){
					p.trySlice(i,j,l,j,pizzaSet);
				}
			}
		}
		System.out.println("Sorting slices...");
		p.sortSlices(fileName);

		long endTime = System.nanoTime()/1000000000;
		long duration = (endTime - startTime);
		int mins = (int) Math.floor(duration / 60);
		int hours = (int) Math.floor(mins / 60);
		int sec = (int) Math.floor((hours * 3600) - duration);
		System.out.println("Duration: "+hours+" hours "+mins+" mins "+sec+" secs.");
	}
	public void trySlice(int r1, int c1, int r2, int c2, int[][] pizza) {
		int tomato = 0;
		int mushroom = 0;
		int count = 0;
		for(int i = r1; i <= r2; i++) {
			for(int j = c1; j <= c2; j++) {
				count++;
				if(pizza[i][j] == 0) {
					tomato++;
				} else {
					mushroom++;
				}
			}
		}
		if(tomato >= minIngredients && mushroom >= minIngredients) {
			registerSlice(r1,c1,r2,c2,count);
		}
	}
	public void registerSlice(int r1, int c1, int r2, int c2, int count) {
		Slice slice = new Slice(r1,c1,r2,c2,count);
		sliceList.add(slice);
	}
	public void sortSlices(String fileName) {
		ArrayList<Slice> maxCountedList = new ArrayList<Slice>();
		int maxPoints = 0;	
		for(int i = 0; i < sliceList.size(); i++){
			ArrayList<Slice> currentList = new ArrayList<Slice>();
			Slice currentSlice = sliceList.get(i);
			int points = currentSlice.count;
			currentList.add(currentSlice);
			for(int j = 0; j < sliceList.size(); j++) {
				boolean invalid = false;
				// Individual Slice elements iteration
				for(int l = 0; l < currentList.size(); l++) {
					Slice cs = currentList.get(l);
					for(int k = 0; k < cs.count; k++) {
						if(sliceList.get(j).elements.contains(cs.elements.get(k))) {
							invalid = true;
							break;
						}
					}
				}
				if(invalid == false) { 
					currentList.add(sliceList.get(j));
					points = points + sliceList.get(j).count;
				}
			}
			if(points > maxPoints) {
				maxCountedList.clear();
				maxCountedList = (ArrayList<Slice>) currentList.clone();
				maxPoints = points;
			}
		}
		System.out.println("Writing out...");
		// Print the smartest cuts
		try{
			PrintWriter writer = new PrintWriter((fileName+".out"), "UTF-8");
			writer.println(maxCountedList.size());
			for(int i = 0; i < maxCountedList.size(); i++){
				Slice cSlice = maxCountedList.get(i);
				writer.println((cSlice.r1)+" "+(cSlice.c1)+" "+(cSlice.r2)+" "+(cSlice.c2));
			}
			writer.close();
			System.out.println("File written out: "+fileName+".out");
		} catch(IOException iop) {
			System.out.println("An exception occured no output");
		}
	}

}
