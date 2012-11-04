package core;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import vector.Trajectory;
import vector.Vector3d;
import vector.VectorField;

public class Map extends MapReduceBase implements Mapper<LongWritable, Text, Vector3d, Trajectory>{
	private static VectorField field;
	private static double h;
	
	public void configure(JobConf conf){
		field = new VectorField(conf.get("field_file"));
		h = Double.parseDouble(conf.get("step_size"));
	}
	
	private Vector3d sum(Vector3d v1, Vector3d v2){
		return new Vector3d(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ());
	}
	
	private Vector3d subtract(Vector3d v1, Vector3d v2){
		return new Vector3d(v1.getX() - v2.getX(), v1.getY() - v2.getY(), v1.getZ() - v2.getZ());
	}
	
	private Vector3d mult_scalar(Vector3d v, double s){
		return new Vector3d(v.getX()*s, v.getY()*s, v.getZ()*s);
	}
	
	private double module(Vector3d v){
		return Math.sqrt(Math.pow(v.getX(), 2) + Math.pow(v.getY(), 2) + Math.pow(v.getZ(), 2));
	}
	
	private Vector3d nearest_neighbour(Vector3d v){
		int x, y, z;
		
		if( ( v.getX() - Math.floor(v.getX()) ) > 0){
			x = (int) Math.ceil(v.getX());
		}else{
			x = (int) Math.floor(v.getX());
		}
		
		if( ( v.getY() - Math.floor(v.getY()) ) > 0){
			y = (int) Math.ceil(v.getY());
		}else{
			y = (int) Math.floor(v.getY());
		}
		
		if( ( v.getZ() - Math.floor(v.getZ()) ) > 0){
			z = (int) Math.ceil(v.getZ());
		}else{
			z = (int) Math.floor(v.getZ());
		}
		
		if(x >= field.n_x() || y >= field.n_y() || z >= field.n_z() || x < 0 || y < 0 || z < 0){
			return new Vector3d();
		}else{
			return field.get(x, y, z);
		}
	}
	
	private Vector3d trilinear_interpolation(Vector3d v){
		int x1, y1, z1, x0, y0, z0;
		double xd, yd, zd;
		Vector3d P1, P2, P3, P4, P5, P6, P7, P8, X1, X2, X3, X4, Y1, Y2;
		
		x1 = (int) Math.ceil(v.getX());
		y1 = (int) Math.ceil(v.getY());
		z1 = (int) Math.ceil(v.getZ());
		x0 = (int) Math.floor(v.getX());
		y0 = (int) Math.floor(v.getY());
		z0 = (int) Math.floor(v.getZ());
		xd = v.getX() - ((double) x0);
		yd = v.getY() - ((double) y0);
		zd = v.getZ() - ((double) z0);
		
		if(x1 >= field.n_x() || y1 >= field.n_y() || z1 >= field.n_z() || x0 < 0 || y0 < 0 || z0 < 0){
			return nearest_neighbour(v);
		}else{
			P1 = field.get(x0, y0, z0);
			P2 = field.get(x1, y0, z0);
			P3 = field.get(x0, y0, z1);
			P4 = field.get(x1, y0, z1);
			P5 = field.get(x0, y1, z0);
			P6 = field.get(x1, y1, z0);
			P7 = field.get(x0, y1, z1);
			P8 = field.get(x1, y1, z1);
			
			X1 = sum(P1, mult_scalar(subtract(P2, P1), xd));
			X2 = sum(P3, mult_scalar(subtract(P4, P3), xd));
			X3 = sum(P5, mult_scalar(subtract(P6, P5), xd));
			X4 = sum(P7, mult_scalar(subtract(P8, P7), xd));
			
			Y1 = sum(X1, mult_scalar(subtract(X3, X1), yd));
			Y2 = sum(X2, mult_scalar(subtract(X4, X2), yd));
			
			return sum(Y1, mult_scalar(subtract(Y2, Y1), zd));
		}
	}
	
	private Trajectory RungeKutta4(Vector3d initial_point){
		Trajectory t = new Trajectory();
		Vector3d k1, k2, k3, k4, initial, direction;
		
		initial = initial_point;
		direction = field.get((int) initial.getX(), (int) initial.getY(), (int) initial.getZ());
		while(module(direction) > 0.0){
			t.add(initial);
			
			k1 = mult_scalar( direction, h );
			k2 = mult_scalar( trilinear_interpolation(sum(initial, mult_scalar( k1, 0.5 ))), h);
			k3 = mult_scalar( trilinear_interpolation(sum(initial, mult_scalar( k2, 0.5 ))), h);
			k4 = mult_scalar( trilinear_interpolation(sum(initial, k3)), h);
			
			initial = sum( initial, sum( mult_scalar( k1 , 0.166666667 ), sum( mult_scalar( k2, 0.333333333 ), sum( mult_scalar( k3, 0.333333333 ), mult_scalar( k4, 0.166666667 ) ) ) ) );
			direction = trilinear_interpolation(initial);
		}
		
		return t;
	}

	@Override
	public void map(LongWritable key, Text value, OutputCollector<Vector3d, Trajectory> output, Reporter reporter) throws IOException {
		String text[] = null;
		Vector3d initial_point;
		
		text = value.toString().split("\\s*");
		initial_point = new Vector3d(Double.parseDouble(text[0]), Double.parseDouble(text[1]), Double.parseDouble(text[2]));
		
		output.collect(initial_point, RungeKutta4(initial_point));
	}
}