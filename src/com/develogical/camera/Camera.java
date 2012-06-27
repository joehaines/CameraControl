package com.develogical.camera;

public class Camera implements WriteListener {


    private Sensor sensor;
    private MemoryCard memoryCard;

    private boolean currentlyWriting = false;
    private boolean powerOffRequested = false;

    private boolean cameraIsOn = false;

    //constructor
    public Camera(Sensor sensor, MemoryCard memoryCard) {
        this.sensor = sensor;
        this.memoryCard = memoryCard;
    }

    public void pressShutter() {
        if (cameraIsOn) {
            currentlyWriting = true;
            memoryCard.write(sensor.readData());
        }
    }

    public void powerOn() {
        cameraIsOn = true;
        sensor.powerUp();
    }

    public void powerOff() {
        if (!currentlyWriting) {
            sensor.powerDown();
        } else {
            //do nothing
            powerOffRequested = true;
        }

    }

    @Override
    public void writeComplete() {
        currentlyWriting = false;
        if(powerOffRequested) {
            powerOff();
        }
    }
}
