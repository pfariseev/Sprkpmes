package com.example.fariseev_ps;



import android.Manifest;

public class AppPermissions {

    // Группы разрешений для разных функциональностей

    public static class Camera {
        public static final String[] PERMISSIONS = {
                Manifest.permission.CAMERA
        };
    }

    public static class Storage {
        public static final String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    public static class Location {
        public static final String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }

    public static class Audio {
        public static final String[] PERMISSIONS = {
                Manifest.permission.RECORD_AUDIO
        };
    }

    public static class Contacts {
        public static final String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        };
    }

    public static class Phone {
        public static final String[] PERMISSIONS = {
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE
        };
    }

    // Комбинированные группы
    public static class CameraAndStorage {
        public static final String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    public static class AllBasic {
        public static final String[] PERMISSIONS = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO
        };
    }
}