package com.netflix.paas.rest.util;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Supplier;
import com.netflix.astyanax.connectionpool.Host;

public class HostSupplier implements Supplier<List<Host>> {

    public List<Host> get() {
        // TODO Auto-generated method stub
        List<Host>  list = new ArrayList<Host>();
//        Host h1 = new Host("ec2-54-235-224-8.compute-1.amazonaws.com",7102);
        Host h1 = new Host("54.235.224.8",7102);

//        Host h2 = new Host("ec2-54-224-106-243.compute-1.amazonaws.com",7102);
        Host h2 = new Host("54.224.106.243",7102);

//        Host h3 = new Host("ec2-54-242-127-138.compute-1.amazonaws.com",7102);
        Host h3 = new Host("54.242.127.138",7102);

        list.add(h1);
        list.add(h2);
        list.add(h3);
        return list;
    } 

}
