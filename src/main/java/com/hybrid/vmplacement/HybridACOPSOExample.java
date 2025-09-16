package com.hybrid.vmplacement;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;

public class HybridACOPSOExample {

    private static final int HOSTS = 4;
    private static final int HOST_PES = 8;
    private static final int VMS = 4;
    private static final int CLOUDLETS = 4;

    public static void main(String[] args) {
        CloudSim simulation = new CloudSim();

        Datacenter datacenter = createDatacenter(simulation);
        DatacenterBrokerSimple broker = new DatacenterBrokerSimple(simulation);

        List<Vm> vmList = createVms();
        List<Cloudlet> cloudletList = createCloudlets();

        broker.submitVmList(vmList);
        broker.submitCloudletList(cloudletList);

        simulation.start();

        cloudletList.forEach(cloudlet ->
                System.out.printf("Cloudlet %d finished in %.2f seconds on VM %d%n",
                        cloudlet.getId(), cloudlet.getFinishTime(), cloudlet.getVm().getId())
        );
    }

    private static Datacenter createDatacenter(CloudSim simulation) {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < HOSTS; i++) {
            List<Pe> peList = new ArrayList<>();
            for (int j = 0; j < HOST_PES; j++) {
                peList.add(new PeSimple(1000, new PeProvisionerSimple()));
            }
            Host host = new HostSimple(16000, 32000, 1000000, peList);
            host.setVmScheduler(new VmSchedulerTimeShared());
            hostList.add(host);
        }

        VmAllocationPolicy policy = new VmAllocationPolicyHybridACOPSO();
        return new DatacenterSimple(simulation, hostList, policy);
    }

    private static List<Vm> createVms() {
        List<Vm> vmList = new ArrayList<>();
        for (int i = 0; i < VMS; i++) {
            Vm vm = new VmSimple(i, 1000, 2);
            vm.setRam(2048).setBw(1000).setSize(10000);
            vmList.add(vm);
        }
        return vmList;
    }

    private static List<Cloudlet> createCloudlets() {
        List<Cloudlet> cloudletList = new ArrayList<>();
        for (int i = 0; i < CLOUDLETS; i++) {
            Cloudlet cloudlet = new CloudletSimple(10000, 2);
            cloudlet.setUtilizationModelCpu(cloudlet.getUtilizationModelFull());
            cloudlet.setUtilizationModelRam(cloudlet.getUtilizationModelFull());
            cloudlet.setUtilizationModelBw(cloudlet.getUtilizationModelFull());
            cloudletList.add(cloudlet);
        }
        return cloudletList;
    }
}
