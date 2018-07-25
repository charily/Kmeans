package charily.KMeans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class InstanceGenerator {
    public static void generateInstance()
    {
        File file = new File("Instance.txt");
        try
        {
            BufferedWriter output = new BufferedWriter( new FileWriter( file ) );
            for(int i = 0; i < 30; i++){
                for(int j = 0; j < 6; j++){
                    if(j == 0)
                        output.write(String.valueOf(Math.round(Math.random() * 5)));
                    else {
                        output.write(",");
                        output.write(String.valueOf(Math.round(Math.random() * 5)));
                    }
                }
                output.newLine();
            }
            output.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    public static void main(String[] args)
    {
        InstanceGenerator.generateInstance();
        System.out.println("finished!");
    }
}
