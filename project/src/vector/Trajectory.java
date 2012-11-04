package vector;

import java.util.Vector;

public class Trajectory {
	private Vector<Vector3d> points;
	
	public void add(Vector3d v){
		points.add(v);
	}
	
	public Vector3d get(int i){
		return points.get(i);
	}
	
	public int size(){
		return points.size();
	}
}