package charily.NaiveBayes;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Scanner;


public class NaiveBayesTest {
    public static  class TestMapper
            extends Mapper<Object ,Text,Text,Text>
    {
        public NaiveBayesConf NBConf;
        public NaiveBayesTrainData NBTData;
        public void setup(Context context)
        {
            try
            {
                Configuration conf = context.getConfiguration();
                NBConf = new NaiveBayesConf();
                NBConf.ReadNaiveBayesConf( conf.get("conf"),conf );
                NBTData = new NaiveBayesTrainData();
                NBTData.getData( conf.get("train_result"),conf );
            }
            catch (Exception e)
            {e.printStackTrace();
                System.exit(1);}
        }
        public void map(Object key,Text value,Context context) throws IOException, InterruptedException {
            Scanner scan =  new Scanner(value.toString());
            String str,vals[],temp;
            int i,j,k,fxyi,fyi,fyij,maxf,idx;
            Text id;
            Text cls;
            while(scan.hasNextLine())
            {
                str = scan.nextLine();
                vals = str.split(" ");
                maxf = -100;
                idx = -1;
                for(i = 0; i<NBConf.class_num; i++)
                {
                    fxyi = 1;
                    String cl = NBConf.classNames.get(i);
                    Integer integer = NBTData.freq.get(cl);
                    if(integer == null)
                        fyi = 0;
                    else
                        fyi = integer.intValue();
                    for(j = 1; j<vals.length; j++)
                    {
                        temp = cl + "#" + NBConf.proNames.get(j-1) + "#" + vals[j];

                        integer = NBTData.freq.get(temp);
                        if(integer == null)
                            fyij = 0;
                        else
                            fyij = integer.intValue();
                        fxyi = fxyi*fyij;
                    }
                    if(fyi*fxyi > maxf)
                    {
                        maxf = fyi*fxyi;
                        idx = i;
                    }
                }
                id = new Text(vals[0]);
                cls = new Text(NBConf.classNames.get(idx));
                context.write(id, cls);
            }
        }
    }












}
