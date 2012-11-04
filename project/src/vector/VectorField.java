package vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

public class VectorField {
	private Vector< Vector< Vector<Vector3d>>> field;
	
	public VectorField(String file_path){
		File file = new File(file_path);
		int n_x, n_y, n_z;
		String text[] = null;
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			n_x = Integer.parseInt(reader.readLine());
			n_y = Integer.parseInt(reader.readLine());
			n_z = Integer.parseInt(reader.readLine());
		
			field.ensureCapacity(n_x);
			for(int i = 0; i < n_x; i++){
				field.add(i, new Vector< Vector<Vector3d> >() );
				field.get(i).ensureCapacity(n_y);
				for(int j = 0; j < n_y; j++){
					field.get(i).add(j, new Vector<Vector3d>() );
					field.get(i).get(j).ensureCapacity(n_z);
				}
			}
			
			for(int i = 0; i < n_x; i++){
				for(int j = 0; j < n_y; j++){
					for(int k = 0; k < n_z; k++){
						text = reader.readLine().split("\\s*");
						this.add(i, j, k, new Vector3d(Double.parseDouble(text[0]), Double.parseDouble(text[1]), Double.parseDouble(text[2])));
					}
				}
			}			
		}catch(Exception e){
			
		}
	}
	
	private void add(int x, int y, int z, Vector3d v){
		field.get(x).get(y).add(z, v);
	}
	
	public Vector3d get(int x, int y, int z){
		return field.get(x).get(y).get(z);
	}
	
	public int n_x(){
		return field.size();
	}
	
	public int n_y(){
		return field.get(0).size();
	}
	
	public int n_z(){
		return field.get(0).get(0).size();
	}
}
