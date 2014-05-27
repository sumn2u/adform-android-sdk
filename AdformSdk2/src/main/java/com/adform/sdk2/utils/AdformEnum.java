package com.adform.sdk2.utils;

/**
 * Created by mariusm on 27/05/14.
 */
public class AdformEnum {
    public enum VisibilityGeneralState {
        LOAD_SUCCESSFUL(0),
        LOAD_FAIL(1);

        private int value;

        private VisibilityGeneralState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static VisibilityGeneralState parseType(int status) {
            switch (status) {
                case 0: return LOAD_SUCCESSFUL;
                case 1: return LOAD_FAIL;
                default: return LOAD_SUCCESSFUL;
            }
        }
        public static String printType(VisibilityGeneralState state) {
            switch (state) {
                case LOAD_SUCCESSFUL: return "LOAD_SUCCESSFUL";
                case LOAD_FAIL: return "LOAD_FAIL";
            }
            return null;
        }

    }

    public enum VisibilityOnScreenState {
        ON_SCREEN(0),
        OFF_SCREEN(1);
        private int value;

        private VisibilityOnScreenState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static VisibilityOnScreenState parseType(int status) {
            switch (status) {
                case 1:
                    return ON_SCREEN;
                case 2:
                    return OFF_SCREEN;
                default:
                    return OFF_SCREEN;
            }
        }

        public static String printType(VisibilityOnScreenState state) {
            switch (state) {
                case ON_SCREEN:
                    return "ON_SCREEN";
                case OFF_SCREEN:
                    return "OFF_SCREEN";
            }
            return null;
        }
    }
}
