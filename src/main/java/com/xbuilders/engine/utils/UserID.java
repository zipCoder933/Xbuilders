///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.engine.utils;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.util.UUID;
//
///**
// * Used to give a unique identifier to the player?
// *
// * @author zipCoder933
// */
//public class UserID {
//
//    final public String userName, uuid;
//
//    public UserID() throws IOException {
//        File uuidPath = ResourceUtils.appDataResource("uuid.txt");
//        userName = System.getProperty("user.name");
//        if (uuidPath.exists()) {
//            uuid = Files.readString(uuidPath.toPath());
//        } else {
//            uuid = UUID.randomUUID().toString();
//            Files.writeString(uuidPath.toPath(), uuid);
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "UserUID{" + "userName=" + userName + ", uuid=" + uuid + '}';
//    }
//}
