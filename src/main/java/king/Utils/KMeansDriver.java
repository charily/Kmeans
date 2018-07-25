package king.Utils;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;


public class KMeansDriver {
    private int k;
    private int iterationNum;
    private String sourcePath;
    private String outputPath;
    private Configuration conf;
    public KMeansDriver(int k, int iterationNum, String sourcePath, String outputPath, Configuration conf){
        this.k = k;
        this.iterationNum = iterationNum;
        this.sourcePath = sourcePath;
        this.outputPath = outputPath;
        this.conf = conf;
    }
    public void clusterCenterJob() throws IOException, ClassNotFoundException, InterruptedException {
        for(int i=0;i<iterationNum ;i++)
        {
            Job clusterCenterJob = new Job();
            clusterCenterJob.setJobName("clusterCenterJob"+i);
            clusterCenterJob.setJarByClass(KMeans.class);
            clusterCenterJob.getConfiguration().set("clusterPath", outputPath + "/cluster-" + i +"/");
            clusterCenterJob.setMapperClass(KMeans.KMeansMapper.class);
            clusterCenterJob.setMapOutputKeyClass(IntWritable.class);
            clusterCenterJob.setMapOutputValueClass(Cluster.class);
            clusterCenterJob.setCombinerClass(KMeans.KMeansCombiner.class);
            clusterCenterJob.setReducerClass(KMeans.KMeansReducer.class);
            clusterCenterJob.setOutputKeyClass(NullWritable.class);
            clusterCenterJob.setOutputValueClass(Cluster.class);
            org.apache.hadoop.mapreduce.lib.input.FileInputFormat.addInputPath(clusterCenterJob, new Path(sourcePath));
            FileOutputFormat.setOutputPath(clusterCenterJob, new Path(outputPath + "/cluster-" + (i + 1) +"/"));
            clusterCenterJob.waitForCompletion(true);
            System.out.println("finished!");

        }
    }

}
