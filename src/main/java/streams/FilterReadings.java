package streams;

import akka.japi.Predicate;
import akka.stream.Attributes;
import akka.stream.FlowShape;
import akka.stream.Inlet;
import akka.stream.Outlet;
import akka.stream.stage.AbstractInHandler;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;
import utility.DeviceInfo;
import utility.ReadingConfig;

/**
 * Custom stream processing component used to filter DeviceInfo
 * objects based on the type of reading and whether the reading should
 * be pushed or not.
 */
public class FilterReadings extends GraphStage<FlowShape<DeviceInfo, DeviceInfo>> {

    //Predicate function that is used to filter readings.
    private final Predicate<DeviceInfo> p=this::FilterDeviceInfo;

    /**
     * Function used inside predicate to filter device info objects based
     * on type and whether they exceed or are below the required standards.
     * @param info
     * @return
     */
    private final boolean FilterDeviceInfo(DeviceInfo info) {
        if (info.getType()==DeviceInfo.types.TEMP) {
            return info.getReading() < ReadingConfig.tempLower || info.getReading() > ReadingConfig.tempUpper;
        }
        if (info.getType()==DeviceInfo.types.HUMIDITY) {
            return info.getReading() < ReadingConfig.humidityLower || info.getReading() > ReadingConfig.humidityUpper;
        }
        if (info.getType()==DeviceInfo.types.LIGHT) {
            return info.getReading() < ReadingConfig.lightLower || info.getReading() > ReadingConfig.lightUpper;
        }
        return false;
    }

    public final Inlet<DeviceInfo> in = Inlet.create("Map.in");
    public final Outlet<DeviceInfo> out = Outlet.create("Map.out");

    private final FlowShape<DeviceInfo, DeviceInfo> shape = FlowShape.of(in, out);

    @Override
    public FlowShape<DeviceInfo, DeviceInfo> shape() {
        return shape;
    }

    /**
     * Logic for input and output ports.
     * Predicate function from above is applied here.
     */
    @Override
    public GraphStageLogic createLogic(Attributes inheritedAttributes) throws Exception {
        return new GraphStageLogic(shape) {
            {
                setHandler(
                        in,
                        new AbstractInHandler() {
                            @Override
                            public void onPush() throws Exception {
                                DeviceInfo elem = grab(in);
                                if (p.test(elem)){
                                    push(out, elem);
                                } else {
                                    pull(in);
                                }
                            }
                        });
                setHandler(
                        out,
                        new AbstractOutHandler() {
                            @Override
                            public void onPull() throws Exception {
                                pull(in);
                            }
                        });
            }
        };
    }
}
