
package com.workout.healthmanager.WalkandStep;

import android.content.pm.PackageManager;

import com.workout.healthmanager.WalkandStep.services.AbstractStepDetectorService;
import com.workout.healthmanager.WalkandStep.services.AccelerometerStepDetectorService;
import com.workout.healthmanager.WalkandStep.services.HardwareStepDetectorService;
import com.workout.healthmanager.WalkandStep.utils.AndroidVersionHelper;




public class Factory {



    public static Class<? extends AbstractStepDetectorService> getStepDetectorServiceClass(PackageManager pm){
        if(pm != null && AndroidVersionHelper.supportsStepDetector(pm)) {
            return HardwareStepDetectorService.class;
        }else{
            return AccelerometerStepDetectorService.class;
        }
    }
}
