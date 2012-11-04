package core;

import java.util.Vector;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import vector.Trajectory;
import vector.Vector3d;

public class Tracer {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		JobConf conf = new JobConf(Tracer.class);
		conf.setJobName("rungekutta4");

		conf.setOutputKeyClass(Vector3d.class);
		conf.setOutputValueClass(Trajectory.class);

		conf.setMapperClass(Map.class);
		conf.setReducerClass(Reduce.class);
		
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat((Class<? extends OutputFormat>) TextOutputFormat.class);
		
		conf.setStrings("field_file", args[0]);
		conf.setFloat("step_size", Float.parseFloat(args[1]));

		FileInputFormat.setInputPaths(conf, new Path(args[2]));
		FileOutputFormat.setOutputPath(conf, new Path(args[3]));

		JobClient.runJob(conf);
	}
}
