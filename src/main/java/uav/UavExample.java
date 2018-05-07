/*
 * Copyright (C) 2011-2018 Rinde R.S. van Lon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uav;

import javax.measure.unit.SI;

import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.road.CollisionPlaneRoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModelBuilders;
import com.github.rinde.rinsim.core.model.time.TimeModel;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.ui.View;
import com.github.rinde.rinsim.ui.renderers.PlaneRoadModelRenderer;

/**
 * Example showcasing the {@link CollisionPlaneRoadModel}.
 * @author Hoang Tung Dinh
 * @author Rinde van Lon
 */
public final class UavExample {
    static final double PLANE_SIZE = 25;
    static final Point MIN_POINT = new Point(0, 0);
    static final Point MAX_POINT = new Point(PLANE_SIZE, PLANE_SIZE);
    static final double MAX_SPEED = 1;
    static final double UAV_RADIUS = .5;
    static final long TICK_LENGTH = 250;
    static final int NUM_UAVS = 12;

    static final int TESTING_SPEEDUP = 16;
    static final long TESTING_END_TIME = 180000L;
    private UavExample() {}

    /**
     * @param args No args.
     */
    public static void main(String[] args) {
        run(false);
    }

    /**
     * Runs the example.
     * @param testing If <code>true</code> the example will run in testing mode,
     *          automatically starting and stopping itself such that it can be run
     *          from a unit test.
     */
    public static void run(boolean testing) {
        View.Builder viewBuilder = View.builder()
                .with(PlaneRoadModelRenderer.builder())
                .with(UavRenderer.builder()
                        // Several visualization options are available, see UavRenderer for
                        // details.
                        .withDifferentColors())
                .withAutoPlay()
                .withTitleAppendix("UAV example");

        if (testing) {
            viewBuilder = viewBuilder.withAutoClose()
                    .withSimulatorEndTime(TESTING_END_TIME)
                    .withSpeedUp(TESTING_SPEEDUP);
        }

        final Simulator sim =
                Simulator.builder()
                        .addModel(TimeModel.builder().withTickLength(TICK_LENGTH))
                        .addModel(
                                RoadModelBuilders.plane()
                                        .withCollisionAvoidance()
                                        .withObjectRadius(UAV_RADIUS)
                                        .withMinPoint(MIN_POINT)
                                        .withMaxPoint(MAX_POINT)
                                        .withDistanceUnit(SI.METER)
                                        .withSpeedUnit(SI.METERS_PER_SECOND)
                                        .withMaxSpeed(MAX_SPEED))
                        .addModel(viewBuilder)
                        .build();

        final CollisionPlaneRoadModel model =
                sim.getModelProvider().getModel(CollisionPlaneRoadModel.class);
        int counter = 0;
        for (int i = 0; i < NUM_UAVS; i++) {
            final Point pos =
                    model.getRandomUnoccupiedPosition(sim.getRandomGenerator());
            sim.register(new UavAgent(pos, Integer.toString(counter++), MAX_SPEED));
        }
        sim.start();
    }
}
