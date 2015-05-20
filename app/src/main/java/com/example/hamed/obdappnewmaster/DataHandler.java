package com.example.hamed.obdappnewmaster;

import java.io.UnsupportedEncodingException;

public class DataHandler {

    /*
    //PID 6FA vehicle identification number
    public String vinRawToReal(byte[] obd_data) {
        String vin = "";
        return vin;
    }
    */

    //PID 374 state of charge
    public String stateOfChargeRawToReal(byte[] obd_data) {
        String state_of_charge = "";
        String state_of_charge_hex = "";
        String hex_values = "";
        long hex_to_long = 0;
        try {
            hex_values = new String(obd_data, "ASCII");
            state_of_charge_hex = hex_values.substring(3, 5);
            hex_to_long = Long.parseLong(state_of_charge_hex, 16);

            //hex_to_long = Long.parseLong(new String(obd_data, "ASCII").substring(2,4), 16);

            state_of_charge = (hex_to_long * 0.5) - 5 + "%";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return state_of_charge;
    }
    //PID 346 EV Power
    public String evPowerRawToReal(byte[] obd_data) {
        String ev_power = "";
//        String ev_power_hex = "";
//        String hex_values = "";
        long hex_to_long = 0;
        try {
//            hex_values = new String(obd_data, "ASCII");
//            ev_power_hex = hex_values.substring(3, 5);
//            hex_to_long = Long.parseLong(ev_power_hex, 16);

            hex_to_long = Long.parseLong(new String(obd_data, "ASCII").substring(0,4), 16);
            ev_power = (hex_to_long * 10) - 100000 + "W";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ev_power;
    }
    //PID 412 Velocity and Odemeter
    public String[] velocityAndOdometerRawToReal(byte[] obd_data) {
        String [] velocityAndOdometer = new String[2];
        long hex_to_long_v = 0;
        long hex_to_long_o = 0;

        try {
            hex_to_long_v = Long.parseLong(new String(obd_data, "ASCII").substring(0,4), 16);
            velocityAndOdometer[0] = hex_to_long_v - 65024 + "km/t";
            hex_to_long_o = Long.parseLong(new String(obd_data, "ASCII").substring(4,10), 16);
            velocityAndOdometer[1] = hex_to_long_o + "km";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return velocityAndOdometer;
    }

    /*
    //PID 389 Onboard Charging Status
    public String onboardChargingStatusRawToReal(byte[] obd_data) {
        String onboard_charging_status = "";
        return onboard_charging_status;
    }
    //PID 696 Quick Charge Status
    public String quickChargeStatusRawToReal(byte[] obd_data) {
        String quick_charge_status = "";
        return quick_charge_status;
    }


    //PID 418 Gear Shift Position
    public String gearShiftPositionRawToReal(byte[] obd_data) {
        String gear_shift_position = "";
        return gear_shift_position;
    }

    //PID 3A4 Air Condition
    public String AirConditionStatusRawToReal(byte[] obd_data) {
        String air_condition = "";
        return air_condition;
    }

    //PID 424 Light Status
    public String lightStatusRawToReal(byte[] obd_data) {
        String light_status = "";
        return light_status;
    }

    //PID 231 Break Lamp
    public String breakLampRawToReal(byte[] obd_data) {
        String break_lamp = "";
        return break_lamp;
    }

    */
}