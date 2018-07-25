package charily.KMeans;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;


/**
 * Created by Charily on 2018/7/18
 */
public class Cluster  implements Writable {
    private int clusterID;
    private long numOfPoints;
    private Instance center;

    public Cluster() {
        this.setClusterID(-1);
        this.setNumOfPoints(0);
        this.setCenter(new Instance());
    }

    public Cluster(int clusterID, Instance center) {
        this.setClusterID(clusterID);
        this.setNumOfPoints(0);
        this.setCenter(center);
    }

    public Cluster(String line)
    {
        String[] value = line.split(",",3);
        clusterID = Integer.parseInt(value[0]);
        numOfPoints = Long.parseLong(value[1]);
        center = new Instance(value[2]);
    }
    public String toString()
    {
        String result = String.valueOf(clusterID)+","
                        +String.valueOf(numOfPoints)+","
                        +center.toString();
        return result;
    }
    public void observeInstance (Instance instance)
    {
        try
        {
            Instance sum = center.multiply(numOfPoints).add(instance);
        }
        catch(Exception e )
        {
            e.printStackTrace();
        }
    }
    public void write(DataOutput dataOutput) {

    }

    public void readFields(DataInput dataInput) {

    }
    public int getClusterID() {
        return clusterID;
    }
    public long getNumOfPoints() {
        return numOfPoints;
    }

    public Instance getCenter() {
        return center;
    }
    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
    }

    public void setNumOfPoints(long numOfPoints) {
        this.numOfPoints = numOfPoints;
    }

    public void setCenter(Instance center) {
        this.center = center;
    }
}
