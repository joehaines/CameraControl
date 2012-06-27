package com.develogical.camera;

public class Camera implements WriteListener {


    private Sensor sensor;
    private MemoryCard memoryCard;


    //constructor
    public Camera(Sensor sensor, MemoryCard memoryCard) {
        this.sensor = sensor;
        this.memoryCard = memoryCard;
    }

    public void pressShutter() {
        memoryCard.write(sensor.readData());
        writeComplete();
    }

    public void powerOn() {
       sensor.powerUp();
    }

    public void powerOff() {
       sensor.powerDown();
    }

    @Override
    public void writeComplete() {
        //do nothing
    }
}

