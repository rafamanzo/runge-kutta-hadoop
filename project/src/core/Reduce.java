package core;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import vector.Vector3d;
import vector.Trajectory;

public class Reduce extends MapReduceBase implements Reducer<Vector3d, Trajectory, Text, Text>{

	@Override
	public void reduce(Vector3d key, Iterator<Trajectory> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		Trajectory t;
		
		while(values.hasNext()){
			t = values.next();
			for (int i = 0; i < t.size(); i++) {
				output.collect(new Text(t.get(i).toString()), new Text(""));
			}
		}
	}

}
