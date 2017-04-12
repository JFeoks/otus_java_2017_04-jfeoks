package ru.otus.l21;

import java.util.ArrayList;

/**
 * Created by JFeoks on 12.04.2017.
 */
public class ObjectSizeCalculator {
    private static final int COUNT = 100000;
    private static final Runtime runtime = Runtime.getRuntime ();

    public static void main (String [] args) throws Exception {
        objectMemoryUsage(Object.class);
        objectMemoryUsage(String.class);
        objectMemoryUsage(ArrayList.class);
        objectMemoryUsage(int[].class);
        arrayDeltaPerNewElementMemoryUsage(int[].class, 123);
    }

    public static void objectMemoryUsage(Class clazz) throws Exception {
        runGC ();
        usedMemory ();

        Object [] objects = new Object [COUNT];
        long startMemory = 0;

        runGC();
        startMemory = usedMemory();

        for (int i = 0; i < COUNT; ++ i) {
            Object object = null;
            switch (clazz.getCanonicalName()){
                case "java.lang.String" :
                    object = new String("");
                    break;
                case "java.lang.Object" :
                    object = new Object();
                    break;
                case "java.util.ArrayList" :
                    object = new ArrayList<>();
                    break;
                case "int[]" :
                    object = new int[0];
                    break;
            }
            objects[i] = object;
        }

        runGC();
        long endMemory = usedMemory();

        final int size = Math.round (((float)(endMemory - startMemory))/COUNT);
        System.out.println ("Memory usage size of " + objects[0].getClass().getCanonicalName() + " = " + size + " bytes");
        for (int i = 0; i < COUNT; ++ i) objects[i] = null;
        objects = null;
    }

    public static void arrayDeltaPerNewElementMemoryUsage(Class clazz, int N) throws Exception {
        int null_size = calculateMemorySize(int[].class, 0);
        int n_size = calculateMemorySize(int[].class, N);
        int result = Math.round((float)(n_size - null_size) / N);
        System.out.println ("Delta memory per element in " + clazz.getCanonicalName() + " = " + result + " bytes");
    }

    public static int calculateMemorySize(Class clazz, int N) throws Exception {
        runGC ();
        usedMemory ();

        Object [] objects = new Object [COUNT];
        long startMemory = 0;

        runGC();
        startMemory = usedMemory();

        for (int i = 0; i < COUNT; ++ i) {
            Object object = null;
            switch (clazz.getCanonicalName()){
                case "int[]" :
                    object = new int[N];
                    break;
            }
            objects[i] = object;
        }

        runGC();
        long endMemory = usedMemory();

        final int size = Math.round (((float)(endMemory - startMemory)) / COUNT);
        for (int i = 0; i < COUNT; ++ i) objects[i] = null;
        objects = null;
        return size;
    }

    private static void runGC() throws Exception {
        for (int r = 0; r < 4; ++ r) _runGC();
    }

    private static void _runGC() throws Exception {
        long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
        for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++ i) {
            runtime.runFinalization();
            runtime.gc();
            Thread.currentThread ().yield();
            usedMem2 = usedMem1;
            usedMem1 = usedMemory();
        }
    }
    private static long usedMemory() throws Exception {
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
