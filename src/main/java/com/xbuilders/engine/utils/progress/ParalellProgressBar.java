package com.xbuilders.engine.utils.progress;

///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.utils.progress;
//
//import java.util.ArrayList;
//
///**
// *
// * @author zipCoder933
// */
//public class ParalellProgressBar extends ProgressBar {
//
//    /**
//     * @return the tasks
//     */
//    public int getTasks() {
//        return tasks;
//    }
//
//    /**
//     * @return the completedTasks
//     */
//    public int getCompletedTasks() {
//        return completedTasks;
//    }
//
//    public ArrayList<ProgressBar> progressValues;
//    private int tasks = 0;
//
//    public ParalellProgressBar() {
//        progressValues = new ArrayList<ProgressBar>();
//    }
//
//    ProgressData progData = null;
//    String str;
//
//    private int completedTasks = 0;
//
//    public void setTask(ProgressData progData, String str) {
//        this.progData = progData;
//        this.str = str;
//    }
//
//    int barSize = 0;
//
//    public void setBarSize(int size) {
//        barSize = size;
//    }
//
//    public void resetBarSize() {
//        barSize = 0;
//    }
//
//    class EventHandler extends ProgressEventHandler {
//
//        @Override
//        public void onProgressChange(double value) {
//            double progress = 0;
//            int complete = 0;
//            if (barSize <= 0) {
//                barSize = getTasks();
//            }
//            for (int i = 0; i < progressValues.size(); i++) {
//                if (progressValues.get(i).getValue() >= 1) {
//                    complete++;
//                }
//                progress += (double) progressValues.get(i).getValue() / barSize;
//            }
//            completedTasks = complete;
//            if (progData != null) {
//                progData.setTask(str + " (" + complete + "/" + getTasks() + ")");
//            }
//            setValue(progress);
//        }
//    }
//
//    @Override
//    public boolean isComplete() {
//        return getCompletedTasks() >= getTasks();
//    }
//
//    EventHandler eventHandler;
//
//    public ProgressBar newTask() {
//        tasks++;
//        eventHandler = new EventHandler();
//        final ProgressBar bar = new ProgressBar();
//        bar.addEventHandler(eventHandler);
//        progressValues.add(bar);
//        return bar;
//    }
//
//}
