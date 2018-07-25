package charily.KMeans;

import java.util.List;

public class HammingDistance<T> implements Distance<T> {

    public double getDistance(List<T> a, List<T> b) throws Exception {
        if(a.size() != b.size())
        {
            throw new Exception("size not compatible!");
        }
        else
            {
                double sum = 0.0;
                for( int i=0;i<a.size();i++)
                {
                    if(!a.get(i).equals( b.get( i ) ))
                    {
                        sum = sum+1;
                    }
                }
                return sum;
            }

    }
}
