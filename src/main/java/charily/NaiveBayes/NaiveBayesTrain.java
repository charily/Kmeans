package charily.NaiveBayes;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import javax.security.auth.login.Configuration;
import java.io.IOException;
import java.util.Scanner;

public class NaiveBayesTrain {
    public static class TrainMapper extends
            Mapper<Object ,Text ,Text,IntWritable>
    {
        public NaiveBayesConf NBconf;
        private final static IntWritable one = new IntWritable( 1 );
        private  Text word;
        public void setup(Context context)
        {
            try
            {
                NBconf = new NaiveBayesConf();
                org.apache.hadoop.conf.Configuration conf = context.getConfiguration();
                NBconf.ReadNaiveBayesConf( ((org.apache.hadoop.conf.Configuration) conf).get( "conf" ),conf );
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            System.out.println("setup");
        }
        public void map(Object key,Text value,Context context) throws IOException, InterruptedException {
            Scanner scanner = new Scanner(value.toString());
            String str,vals[],temp;
            int i;
            word = new Text();
            while(scanner.hasNextLine())
            {
                str = scanner.nextLine();
                vals = str.split( " " );
                word.set( vals[0] );
                context.write( word,one );
                for(i=1;i<vals.length;i++)
                {
                    word = new Text(  );
                    temp = vals[0]+"#"+NBconf.proNames.get( i-1 );
                    temp+="#"+vals[i];
                    word.set( temp );
                    context.write( word,one );
                }
            }
        }
    }
    public static class TrainReducer extends Reducer<Text,IntWritable,Text,IntWritable>
    {
        private IntWritable result = new IntWritable(  );
        public void reduce(Text key,Iterable<IntWritable>values,Context context) throws IOException, InterruptedException {
            int sum=0;
            for(IntWritable val:values)
            {
                sum+=val.get();
            }
            result.set(sum);
            context.write( key,result );
        }
    }















}
