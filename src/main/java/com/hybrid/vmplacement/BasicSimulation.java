package com.hybrid.vmplacement;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;

public class BasicSimulation {
    private static final int HOSTS = 2;
    private static final int HOST_PES = 4;
    private static final int VMS = 4;
    private static final int CLOUDLETS = 4;

    public static void main(String[] args) {
        System.out.println("Starting CloudSim Plus Simulation...");

        CloudSim simulation = new CloudSim();

        Datacenter datacenter = createDatacenter(simulation);

        DatacenterBrokerSimple broker = new DatacenterBrokerSimple(simulation);

        List<VmSimple> vmList = createVms();
        broker.submitVmList(vmList);

        List<Cloudlet> cloudletList = createCloudlets();
        broker.submitCloudletList(cloudletList);

        simulation.start();

        cloudletList.forEach(cloudlet ->
                System.out.printf("Cloudlet %d finished in %.2f seconds on VM %d%n",
                        cloudlet.getId(),
                        cloudlet.getActualCpuTime(),
                        cloudlet.getVm().getId())
        );
    }

    private static Datacenter createDatacenter(CloudSim simulation) {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < HOSTS; i++) {
            List<Pe> peList = new ArrayList<>();
            for (int j = 0; j < HOST_PES; j++) {
                peList.add(new PeSimple(1000)); // 1000 MIPS per PE
            }
            Host host = new HostSimple(8000, 16000, 1000000, peList);
            host.setVmScheduler(new VmSchedulerTimeShared());
            hostList.add(host);
        }
        return new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());
    }

    private static List<VmSimple> createVms() {
        List<VmSimple> vmList = new ArrayList<>();
        for (int i = 0; i < VMS; i++) {
            VmSimple vm = new VmSimple(i, 1000, 2);
            vm.setRam(2048)
                    .setBw(1000)
                    .setSize(10000);
            vmList.add(vm);
        }
        return vmList;
    }


    private static List<Cloudlet> createCloudlets() {
        List<Cloudlet> cloudletList = new ArrayList<>();
        for (int i = 0; i < CLOUDLETS; i++) {
            Cloudlet cloudlet = new CloudletSimple(10000, 2);
            cloudletList.add(cloudlet);
        }
        return cloudletList;
    }
}
