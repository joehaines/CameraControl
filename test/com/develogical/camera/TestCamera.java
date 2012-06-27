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

        //power down the camera first
        context.checking(new Expectations(){{
            ignoring(sensor);
        }});

        camera.powerOff();

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

        //check that power on occurs
        context.checking(new Expectations(){{
            exactly(1).of(sensor).powerUp();
        }});

        camera.powerOn();

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

        //check that the write is complete
        context.checking(new Expectations(){{
            exactly(1).of(writeListener).writeComplete();
        }});

        //finish the write
        camera.writeComplete();

    }

}
