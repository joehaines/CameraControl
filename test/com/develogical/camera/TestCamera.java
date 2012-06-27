package com.develogical.camera;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(value = JMock.class)
public class TestCamera {

    //set up mockery context
    Mockery context = new Mockery();

    //set up new mock instance of the sensor
    private final Sensor sensor = context.mock(Sensor.class);

    //set up new mock instance of the memory card
    private final MemoryCard memoryCard = context.mock(MemoryCard.class);

    //set up new mock instance of the write listener
    private final WriteListener writeListener = context.mock(WriteListener.class);

    //set up new instance of the camera
    private final Camera camera = new Camera(sensor, memoryCard);

    @Test
    public void switchingTheCameraOnPowersUpTheSensor() {

        //placed in to method as reused throughout
        powerOnCameraTest();

    }

    private void powerOnCameraTest() {
        // check that only one sensor power up occurs ...
        context.checking(new Expectations() {{
            exactly(1).of(sensor).powerUp();
        }});

        //...when we power on the camera
        camera.powerOn();
    }

    @Test
    public void switchingTheCameraOffPowersDownTheSensor() {

        context.checking(new Expectations() {{
            exactly(1).of(sensor).powerDown();
        }});
        camera.powerOff();

    }

    @Test
    public void pressingShutterWithPowerOffDoesNothing() {

        //assume that the camera is off by default

        //check that nothing happens
        context.checking(new Expectations() {{
            never(sensor);
        }});

        //press the shutter
        camera.pressShutter();

    }

    @Test
    public void pressingShutterWithPowerOnCopiesDataFromSensorToMemoryCard() {

        //power on the camera first
        powerOnCameraTest();

        //check that data copies from the sensor to the memory card
        context.checking(new Expectations() {{
            //create fake data
            final byte[] sensorData = new byte[]{1,2,3};
            //check that the sensor reads the data
            oneOf(sensor).readData();
            //check that the read data method returns the same data
            will(returnValue(sensorData));
            //check that the memory card is written to with the same data
            //same below (as opposed to equals) checks the exact same object is placed in - if i commented out will and used same the test would fail because the sensor object is making a new byte array (empty one) for us
            oneOf(memoryCard).write(with(same(sensorData)));
        }});

        //press the shutter
        camera.pressShutter();

    }

    @Test
    public void ifDataBeingWritten_powerDownDoesNotPowerDownSensor() {

        //power on the camera first
        powerOnCameraTest();

        //check that data copies from the sensor to the memory card
        context.checking(new Expectations() {{
            //check that the sensor reads the data
            oneOf(sensor).readData();
            //ignore mem card
            ignoring(memoryCard);
        }});

        //press the shutter
        camera.pressShutter();
        //power off the camera
        camera.powerOff();

    }

    @Test
    public void ifDataHasBeenWritten_powerDownSensorOnceWritingComplete() {

        //power on the camera first
        powerOnCameraTest();

        context.checking(new Expectations() {{
            //check that the sensor reads the data
            oneOf(sensor).readData();
            //ignore mem card
            ignoring(memoryCard);
            //check for a power off
            exactly(1).of(sensor).powerDown();
        }});

        //press the shutter
        camera.pressShutter();
        //write complete
        camera.writeComplete();
        //power off the camera
        camera.powerOff();

    }

}
