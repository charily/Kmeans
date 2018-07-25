package charily.KNN;

import charily.KMeans.ListWritable;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KNearestNeighbour {
    public static class KNNMap extends Mapper<LongWritable,Text,LongWritable,ListWritable<DoubleWritable>>
    {
        private int k;
        private ArrayList<Instance> trainSet;
        protected  void setup(Context context) throws IOException {
            k = context.getConfiguration().getInt( "k",1 );
            trainSet = new ArrayList<Instance>();
            Path[] trainFile = DistributedCache.getLocalCacheFiles( context.getConfiguration() );
            BufferedReader br = null;
            String line;
            for(int i=0;i<trainFile.length;i++)
            {
                br = new BufferedReader( new FileReader( (trainFile[0].toString()) ) );
                while((line= br.readLine())!=null)
                {
                    Instance trainInstance = new Instance(line);
                    trainSet.add( trainInstance );
                }
            }
        }
        public void map(LongWritable textIndex, Text textLine, Mapper.Context context) throws IOException, InterruptedException {
            ArrayList<Double> distance = new ArrayList<Double>(k);
            ArrayList<DoubleWritable> trainLable = new ArrayList<DoubleWritable>( k );
            for(int i=0; i<k ;i++)
            {
                distance .add( Double.MAX_VALUE );
                trainLable.add( new DoubleWritable( -1.0 ) );
            }
            ListWritable<DoubleWritable> lables = new ListWritable<DoubleWritable>( DoubleWritable.class );
            Instance testInstance = new Instance( textLine.toString() );
            for(int i=0;i<trainSet.size();i++)
            {
                try
                {
                    double dis = Distance.EuclideanDistance( trainSet.get( i ).getAtrributeValue(),testInstance.getAtrributeValue() );
                    int index = indexOfMax(distance);
                    if(dis<distance.get( index ))
                    {
                        distance.remove( index );
                        trainLable.remove( index );
                        distance.add( dis );
                        trainLable.add(new DoubleWritable( trainSet.get( i ).getLable() ));
                    }
                }
                catch (Exception e )
                {
                    e.printStackTrace();
                }
            }
           // lables.setList(trainLable);
            context.write( textIndex,lables );
        }
        public int indexOfMax(ArrayList<Double> array){
            int index = -1;
            Double min = Double.MIN_VALUE;
            for (int i = 0;i < array.size();i++){
                if(array.get(i) > min){
                    min = array.get(i);
                    index = i;
                }
            }
            return index;
        }
    }
    public static class KNNReduce extends Reducer<LongWritable,charily.KNN.ListWritable<DoubleWritable>,NullWritable,DoubleWritable>
    {
        public void reduce(LongWritable index, Iterable<charily.KNN.ListWritable<DoubleWritable>>kLables,Context context) throws IOException, InterruptedException {
            DoubleWritable predictedLable = new DoubleWritable(  );
            for(charily.KNN.ListWritable<DoubleWritable>val:kLables)
            {
                try
                {
                    predictedLable = valueOfMostFrequent(val);
                    break;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            context.write(NullWritable.get(), predictedLable);
        }
        public DoubleWritable valueOfMostFrequent(charily.KNN.ListWritable<DoubleWritable> list) throws Exception{
            if(list.isEmpty())
                throw new Exception("list is empty!");
            else{
                HashMap<DoubleWritable,Integer> tmp = new HashMap<DoubleWritable,Integer>();
                for(int i = 0 ;i < list.size();i++){
                    if(tmp.containsKey(list.get(i))){
                        Integer frequence = tmp.get(list.get(i)) + 1;
                        tmp.remove(list.get(i));
                        tmp.put(list.get(i), frequence);
                    }else{
                        tmp.put(list.get(i), new Integer(1));
                    }
                }
                //find the value with the maximum frequence.
                DoubleWritable value = new DoubleWritable();
                Integer frequence = new Integer(Integer.MIN_VALUE);
                Iterator<Map.Entry<DoubleWritable, Integer>> iter = tmp.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<DoubleWritable,Integer> entry = (Map.Entry<DoubleWritable,Integer>) iter.next();
                    if(entry.getValue() > frequence){
                        frequence = entry.getValue();
                        value = entry.getKey();
                    }
                }
                return value;
            }
        }
    }
    public static  void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Job kNNJob = new Job();
        kNNJob.setJobName( "kNNJob" );
        kNNJob.setJarByClass( KNearestNeighbour.class );
        DistributedCache.addCacheFile(URI.create(args[2]), kNNJob.getConfiguration());
        kNNJob.getConfiguration().setInt( "k" ,Integer.parseInt( args[3] ));
        kNNJob.setMapperClass(KNNMap.class );
        kNNJob.setMapOutputKeyClass( LongWritable.class );
        kNNJob.setMapOutputValueClass( charily.KNN.ListWritable.class );

        kNNJob.setReducerClass( KNNReduce.class );
        kNNJob.setOutputKeyClass( NullWritable.class );
        kNNJob.setOutputValueClass( DoubleWritable.class );

        kNNJob.setInputFormatClass( TextInputFormat.class );
        kNNJob.setOutputFormatClass( TextOutputFormat.class ) ;
        FileInputFormat.addInputPath(kNNJob, new Path(args[0]));
        FileOutputFormat.setOutputPath(kNNJob, new Path(args[1]));

        kNNJob.waitForCompletion(true);
        System.out.println("finished!");
    }

}
