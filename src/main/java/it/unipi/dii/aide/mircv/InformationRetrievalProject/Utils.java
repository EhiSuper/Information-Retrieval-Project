package it.unipi.dii.aide.mircv.InformationRetrievalProject;

public class Utils {
    public static float getMemoryUsage(){
        float totalMemory = Runtime.getRuntime().totalMemory();
        float memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return (memoryUsage / totalMemory) * 100;
    }
}
