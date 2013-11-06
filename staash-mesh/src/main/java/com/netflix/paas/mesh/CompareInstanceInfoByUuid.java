package com.netflix.paas.mesh;

import java.util.Comparator;

public class CompareInstanceInfoByUuid implements Comparator<InstanceInfo>{
    @Override
    public int compare(InstanceInfo arg0, InstanceInfo arg1) {
        return arg0.getUuid().compareTo(arg1.getUuid());
    }
}
