package com.example.turtlegang.gbthvolunteertracking;

import java.util.concurrent.Semaphore;

public class ConcurrencyManagerSingleton {
    private static ConcurrencyManagerSingleton single_instance = null;

    public Semaphore locationArrMutex;

    private ConcurrencyManagerSingleton() {
        locationArrMutex = new Semaphore(1);
    }

    public static synchronized ConcurrencyManagerSingleton getInstance() {
        if (single_instance == null) {
            single_instance = new ConcurrencyManagerSingleton();
        }
        return single_instance;
    }
}
