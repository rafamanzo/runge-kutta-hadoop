package vector;

public class Vector3d {
	private double x;
	private double y;
	private double z;
	
	public Vector3d(){
		x = 0.0;
		y = 0.0;
		z = 0.0;
	}
	
	public Vector3d(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public void setZ(double z){
		this.z = z;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getZ(){
		return z;
	}
	
	public String toString(){
		return x+" "+y+" "+z;
	}
}
