package com.pi4j.io.gpio.test;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.PinDirection;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.exception.GpioPinExistsException;
import com.pi4j.io.gpio.exception.UnsupportedPinModeException;

public class GpioPinProvisionDigitalOutputTest   
{
    private static GpioProvider provider;
    private static GpioController gpio;
    private static GpioPinDigitalOutput pin;
    
    @BeforeClass 
    public static void setup()
    {
        // create a mock gpio provider and controller
        provider = new MockGpioProvider();
        GpioFactory.setDefaultProvider(provider);
        gpio = GpioFactory.getInstance();
        
        // provision pin for testing
        pin = gpio.provisionDigitalOuputPin(MockPin.DIGITAL_OUTPUT_PIN,  "digitalOutputPin", PinState.LOW);
    }

    @Test
    public void testPinProvisioned() 
    {
        // make sure that pin is provisioned
        Collection<GpioPin> pins = gpio.getProvisionedPins();        
        assertTrue(pins.contains(pin));
    }    

    @Test(expected=GpioPinExistsException.class)
    public void testPinDuplicatePovisioning() 
    {
        // make sure that pin cannot be provisioned a second time
        gpio.provisionDigitalOuputPin(MockPin.DIGITAL_OUTPUT_PIN,  "digitalOutputPin", PinState.LOW);
    }    
    
    @Test(expected=UnsupportedPinModeException.class)
    public void testPinInvalidModePovisioning() 
    {       
        // make sure that pin cannot be provisioned that does not support DIGITAL OUTPUT 
        gpio.provisionDigitalOuputPin(MockPin.DIGITAL_INPUT_PIN,  "analogOutputPin");
    }    
    
    @Test
    public void testPinProvider()
    {
        // verify pin mode
        assertEquals(pin.getProvider(), provider);                
    }
    
    @Test
    public void testPinExport()
    {
        // verify is exported
        assertTrue(pin.isExported());
    }
    
    @Test
    public void testPinInstance()
    {
        // verify pin instance
        assertEquals(pin.getPin(), MockPin.DIGITAL_OUTPUT_PIN);                
    }
    
    @Test
    public void testPinAddress()
    {    
        // verify pin address
        assertEquals(pin.getPin().getAddress(), MockPin.DIGITAL_OUTPUT_PIN.getAddress());
    }

    @Test
    public void testPinName()
    {
        // verify pin name
        assertEquals(pin.getName(), "digitalOutputPin");
    }
     
    @Test
    public void testPinMode()
    {
        // verify pin mode
        assertEquals(pin.getMode(), PinMode.DIGITAL_OUTPUT);
    }

    @Test
    public void testPinValidSupportedMode()
    {
        // verify valid pin mode
        assertTrue(pin.getPin().getSupportedPinModes().contains(PinMode.DIGITAL_OUTPUT));
    }

    @Test
    public void testPinInvalidSupportedMode()
    {
        // verify invalid pin mode
        assertFalse(pin.getPin().getSupportedPinModes().contains(PinMode.DIGITAL_INPUT));
        
        // verify invalid pin mode
        assertFalse(pin.getPin().getSupportedPinModes().contains(PinMode.ANALOG_OUTPUT));
        
        // verify invalid pin mode
        assertFalse(pin.getPin().getSupportedPinModes().contains(PinMode.ANALOG_INPUT));        

        // verify invalid pin mode
        assertFalse(pin.getPin().getSupportedPinModes().contains(PinMode.PWM_OUTPUT));              
    }
    
    @Test
    public void testPinDirection()
    {
        // verify pin direction
        assertEquals(pin.getMode().getDirection(), PinDirection.OUT);                
    }

    @Test
    public void testPinInitialState()
    {
        // verify pin initial state
        assertTrue(pin.isLow());
        assertEquals(pin.getState(), PinState.LOW);                
    }

    @Test
    public void testPinHiState()
    {
        pin.setState(PinState.HIGH);

        // verify pin hi state
        assertTrue(pin.isHigh());
        assertEquals(pin.getState(), PinState.HIGH);                
    }

    @Test
    public void testPinLowState()
    {
        pin.setState(PinState.LOW);

        // verify pin low state
        assertTrue(pin.isLow());
        assertEquals(pin.getState(), PinState.LOW);                
    }

    @Test
    public void testPinHiStateBoolean()
    {
        pin.setState(true);

        // verify pin hi state
        assertTrue(pin.isHigh());
        assertEquals(pin.getState(), PinState.HIGH);                
    }

    @Test
    public void testPinLowStateBoolean()
    {
        pin.setState(false);

        // verify pin low state
        assertTrue(pin.isLow());
        assertEquals(pin.getState(), PinState.LOW);                
    }

    @Test
    public void testPinHi()
    {
        pin.high();

        // verify pin hi state
        assertTrue(pin.isHigh());
        assertEquals(pin.getState(), PinState.HIGH);                
    }

    @Test
    public void testPinLow()
    {
        pin.low();

        // verify pin low state
        assertTrue(pin.isLow());
        assertEquals(pin.getState(), PinState.LOW);                
    }

    @Test
    public void testPinToggle()
    {
        // set known start state
        pin.low();
        
        // toggle hi
        pin.toggle();

        // verify pin hi state
        assertTrue(pin.isHigh());
        
        // toggle low
        pin.toggle();

        // verify pin low state
        assertTrue(pin.isLow());
    }

    @Test
    public void testPinPulse() throws InterruptedException
    {
        // set known start state
        pin.low();
        
        // pulse pin hi for 1/5 second
        pin.pulse(200, PinState.HIGH);

        // verify pin hi state
        assertTrue(pin.isHigh());

        // wait 1/2 second before continuing test
        Thread.sleep(500);
        
        // verify pin low state
        assertTrue(pin.isLow());
    }
    
    @Test
    public void testPinBlink() throws InterruptedException
    {
        // set known start state
        pin.low();
        
        // blink pin for 1 seconds with a blink rate of 1/5 second 
        pin.blink(200, 1000, PinState.HIGH);

        // verify pin hi state
        assertTrue(pin.isHigh());

        // wait before continuing test
        Thread.sleep(250);

        // verify pin low state
        assertTrue(pin.isLow());

        // wait before continuing test
        Thread.sleep(250);
        
        // verify pin hi state
        assertTrue(pin.isHigh());

        // wait before continuing test
        Thread.sleep(250);

        // verify pin low state
        assertTrue(pin.isLow());
        
        // wait before continuing test
        Thread.sleep(500);
        
        // verify pin low state
        assertTrue(pin.isLow());
    }
    
    @Test
    public void testPinUnexport() 
    {
        // unexport pin
        pin.unexport();
        
        // verify is not exported
        assertFalse(pin.isExported());
    }
    
    @Test
    public void testPinUnprovision() 
    {
        // make sure that pin is provisioned before we start
        Collection<GpioPin> pins = gpio.getProvisionedPins();
        assertTrue(pins.contains(pin));

        // un-provision pin
        gpio.unprovisionPin(pin);

        // make sure that pin is no longer provisioned
        pins = gpio.getProvisionedPins();        
        assertFalse(pins.contains(pin));
    }    
    
}