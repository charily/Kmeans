package king.Utils;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

public class Instance  implements Writable {
    ArrayList<Double> value;

    public Instance ()
    {
        value = new ArrayList<Double>();
    }
    public Instance (String line)
    {
        String[] valueString = line.split(",");
        value = new ArrayList<Double>();
        for(int i=0;i<valueString.length;i++)
        {
            value.add(Double.parseDouble(valueString[i]));
        }
    }
    public Instance (Instance ins)
    {
        value = new ArrayList<Double>();
        for(int i=0;i<ins.getValue().size();i++)
        {
            value.add(new Double(ins.getValue().get(i)));
        }
    }
    public Instance(int k)
    {
        value = new ArrayList<Double>();
        for(int i =0; i<k ;i++)
        {
            value.add(0.0);
        }
    }
    public ArrayList<Double>getValue()
    {
        return value;
    }
    public Instance add(Instance instance)
    {
        if(value.size()==0)
        {
            return new Instance(instance);
        }
        else if(instance.getValue().size() == 0)
        {
            return new Instance (this);
        }
        else if(value.size() != instance.getValue().size())
        {
            try
            {
                throw new Exception("can not add! dimension not compatible!" + value.size() + ","
                        + instance.getValue().size());
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else {
            Instance result = new Instance();
            for(int i =0 ;i<value.size();i++)
            {
                result.getValue().add(value.get(i)+instance.getValue().get(i));
            }
            return result;
        }

    }
    public Instance multiply(double num){
        Instance result = new Instance();
        for(int i = 0; i < value.size(); i++){
            result.getValue().add(value.get(i) * num);
        }
        return result;
    }
    public Instance divide(double num){
        Instance result = new Instance();
        for(int i = 0; i < value.size(); i++){
            result.getValue().add(value.get(i) / num);
        }
        return result;
    }
    public String toString(){
        String s = new String();
        for(int i = 0;i < value.size() - 1; i++){
            s += (value.get(i) + ",");
        }
        s += value.get(value.size() - 1);
        return s;
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.write(value.size());
        for(int i=0;i<value.size();i++)
        {
            dataOutput.writeDouble(value.get(i));
        }
    }

    public void readFields(DataInput dataInput) throws IOException {
        int size =0;
        value = new ArrayList<Double>();
        if((size=dataInput.readInt())!=0)
        {
            for(int i=0;i<size;i++)
                value.add(dataInput.readDouble());
        }
    }
}
