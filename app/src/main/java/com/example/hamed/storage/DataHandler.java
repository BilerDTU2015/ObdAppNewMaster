package com.example.hamed.storage;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class DataHandler {

    //PID 6FA vehicle identification number
    public String vinRawToReal(byte[] obd_data) {
        String vin = "";
        String hex_from_buffer;
        StringBuilder output = new StringBuilder();
        try {
            hex_from_buffer = new String(obd_data, "ASCII").substring(5,19) +
                    new String(obd_data, "ASCII").substring(25,39) + new String(obd_data, "ASCII").substring(45,51);
            for (int i = 0; i < hex_from_buffer.length(); i+=2) {
                String str = hex_from_buffer.substring(i, i+2);
                output.append((char)Integer.parseInt(str, 16));
            }
            vin = String.valueOf(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("vin", vin);
        return vin;
    }

    //PID 374 state of charge
    public String stateOfChargeRawToReal(byte[] obd_data) {
        String state_of_charge = "";
        try {
            long hex_to_long = Long.parseLong(new String(obd_data, "ASCII").substring(5,7), 16);
            state_of_charge = (hex_to_long * 0.5) - 5 + "%";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return state_of_charge;
    }

    //PID 346 EV Power
    public String evPowerRawToReal(byte[] obd_data) {
        String ev_power = "";
        try {
            long hex_to_long = Long.parseLong(new String(obd_data, "ASCII").substring(3,7), 16);
            ev_power = (hex_to_long * 10) - 100000 + "W";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ev_power;
    }

    //PID 412 Velocity
    public String velocityRawToReal(byte[] obd_data) {
        String velocity = "";
        try {
            long hex_to_long_v = Long.parseLong(new String(obd_data, "ASCII").substring(3,7), 16);
            velocity = hex_to_long_v - 65024 + "km/t";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return velocity;
    }

    //PID 412 Odemeter same pid as velocity
    public String odometerRawToReal(byte[] obd_data) {
        String odometer ="";
        try {
            long hex_to_long_o = Long.parseLong(new String(obd_data, "ASCII").substring(7,13), 16);
            odometer = hex_to_long_o + "km";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return odometer;
    }

    //PID 389 Onboard Charging Status
    public String chargingRawToReal(byte[] obd_data) {
        String onboard_charging_status = "Not charging";
        try {
            String bin =  new BigInteger(new String(obd_data, "ASCII").substring(13, 15), 16).toString(2);
            int temp = Integer.parseInt(bin);
            bin = String.format("%08d", temp);
            if(bin.substring(4,4).equals("1")) {
                onboard_charging_status = "Charging";
            }
        } catch (Exception e) {
    }

        return onboard_charging_status;
    }

    //PID 389 voltage same pid as charging
    public String voltageRawToReal(byte[] obd_data) {
        String voltage = "";
        try {
            String bin =  new BigInteger(new String(obd_data, "ASCII").substring(13, 15), 16).toString(2);
            int temp = Integer.parseInt(bin);
            bin = String.format("%08d", temp);
            temp = Integer.parseInt(bin.substring(5,7));
            voltage = ""; // not sure how to convert this value

        } catch (Exception e) {
        }

        return voltage;
    }

    //PID 696 Quick Charge Status
    public String quickChargeStatusRawToReal(byte[] obd_data) {
        String quick_charge_status = "Quick charge off";
        try {
            String bin =  new BigInteger(new String(obd_data, "ASCII").substring(13, 15), 16).toString(2);
            int temp = Integer.parseInt(bin);
            bin = String.format("%08d", temp);
            if(bin.substring(3,3).equals("1")) {
                quick_charge_status = "Quick charge on";
            }
        } catch (Exception e) {
        }
        return quick_charge_status;
    }

    //PID 418 Gear Shift Position
    public String gearShiftPositionRawToReal(byte[] obd_data) {
        String gear_shift_position = "";
        try {
            switch (new String(obd_data, "ASCII").substring(3, 5)) {
                case "50":
                    gear_shift_position = "P";
                    break;
                case "52":
                    gear_shift_position = "R";
                    break;
                case "4E":
                    gear_shift_position = "N";
                    break;
                case "44":
                    gear_shift_position = "D";
                    break;
                case "83":
                    gear_shift_position = "E";
                    break;
                case "32":
                    gear_shift_position = "D";
                    break;
                default:
                    gear_shift_position = "Unknown";
                    break;
            }
        } catch (Exception e) {
        }
        return gear_shift_position;
    }

    //PID 3A4 Air Condition
    public String airConditionRawToReal(byte[] obd_data) {
        String air_condition = "Aircon off";
        try {
            String bin =  new BigInteger(new String(obd_data, "ASCII").substring(3, 5), 16).toString(2);
            int temp = Integer.parseInt(bin);
            bin = String.format("%08d", temp);
            if(bin.substring(0,0).equals("1")) {
                air_condition = "Aircon on";
            }
        } catch (Exception e) {
        }
        return air_condition;
    }

    //PID 424 Light Status
    public String frontLightRawToReal(byte[] obd_data) {
        String light_status = "Front light off";
        try {
            String bin =  new BigInteger(new String(obd_data, "ASCII").substring(5, 7), 16).toString(2);
            int temp = Integer.parseInt(bin);
            bin = String.format("%08d", temp);
            if(bin.substring(2,2).equals("1")) {
                light_status = "Front light on";
            }
        } catch (Exception e) {
        }
        return light_status;
    }

    //PID 424 Light Status
    public String leftBlinkLightRawToReal(byte[] obd_data) {
        String light_status = "Left blink light off";
        try {
            String bin =  new BigInteger(new String(obd_data, "ASCII").substring(5, 7), 16).toString(2);
            int temp = Integer.parseInt(bin);
            bin = String.format("%08d", temp);
            if(bin.substring(6,6).equals("1")) {
                light_status = "Left blink light on";
            }
        } catch (Exception e) {
        }
        return light_status;
    }

    //PID 424 Light Status
    public String rightBlinkLightRawToReal(byte[] obd_data) {
        String light_status = "Left blink light off";
        try {
            String bin =  new BigInteger(new String(obd_data, "ASCII").substring(5, 7), 16).toString(2);
            int temp = Integer.parseInt(bin);
            bin = String.format("%08d", temp);
            if(bin.substring(7,7).equals("1")) {
                light_status = "Right blink light on";
            }
        } catch (Exception e) {
        }
        return light_status;
    }

    //PID 231 Break Lamp
    public String breakLampRawToReal(byte[] obd_data) {
        String break_lamp = "Break light off";
        try {
            String hex = new String(obd_data, "ASCII").substring(11,13);
            if(hex.equals("02")) {
                break_lamp = "Break light on";
            }
        } catch (Exception e) {
        }
        return break_lamp;
    }
}