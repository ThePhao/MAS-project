
import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.road.RoadModelBuilders;
import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.Graphs;
import com.github.rinde.rinsim.geom.LengthData;
import com.github.rinde.rinsim.geom.ListenableGraph;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.geom.TableGraph;
import com.github.rinde.rinsim.ui.View;
import com.github.rinde.rinsim.ui.View.Builder;
import com.github.rinde.rinsim.ui.renderers.AGVRenderer;
import com.github.rinde.rinsim.ui.renderers.WarehouseRenderer;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import java.util.Iterator;
import java.util.Map;
import javax.measure.unit.SI;

public final class WarehouseExample {
    private static final double VEHICLE_LENGTH = 2.0D;
    private static final int NUM_AGVS = 1;
    private static final long TEST_END_TIME = 600000L;
    private static final int TEST_SPEED_UP = 16;

    private WarehouseExample() {
    }

    public static void main(String[] args) {
        run(false);
    }

    public static void run(boolean testing) {
        Builder viewBuilder = View.builder().with(WarehouseRenderer.builder().withMargin(VEHICLE_LENGTH)).with(AGVRenderer.builder().withDifferentColorsForVehicles());
        if (testing) {
            viewBuilder = viewBuilder.withAutoPlay().withAutoClose().withSimulatorEndTime(TEST_END_TIME).withTitleAppendix("TESTING").withSpeedUp(TEST_SPEED_UP);
        } else {
            viewBuilder = viewBuilder.withTitleAppendix("Warehouse Example");
        }

        Simulator sim = Simulator.builder().addModel(RoadModelBuilders.dynamicGraph(WarehouseExample.GraphCreator.createSimpleGraph()).withCollisionAvoidance().withDistanceUnit(SI.METER).withVehicleLength(2.0D)).addModel(viewBuilder).build();

        for(int i = 0; i < NUM_AGVS; ++i) {
            sim.register(new AGVAgent(sim.getRandomGenerator()));
        }

        sim.start();
    }

    static class GraphCreator {
        static final int LEFT_CENTER_U_ROW = 4;
        static final int LEFT_CENTER_L_ROW = 5;
        static final int LEFT_COL = 4;
        static final int RIGHT_CENTER_U_ROW = 2;
        static final int RIGHT_CENTER_L_ROW = 4;
        static final int RIGHT_COL = 0;

        GraphCreator() {
        }

        static ImmutableTable<Integer, Integer, Point> createMatrix(int cols, int rows, Point offset) {
            com.google.common.collect.ImmutableTable.Builder<Integer, Integer, Point> builder = ImmutableTable.builder();

            for(int c = 0; c < cols; ++c) {
                for(int r = 0; r < rows; ++r) {
                    builder.put(r, c, new Point(offset.x + (double)c * 2.0D * 2.0D, offset.y + (double)r * 2.0D * 2.0D));
                }
            }

            return builder.build();
        }

        static ListenableGraph<LengthData> createSimpleGraph() {
            Graph<LengthData> g = new TableGraph();
            Table<Integer, Integer, Point> matrix = createMatrix(8, 6, new Point(0.0D, 0.0D));

            for(int i = 0; i < matrix.columnMap().size(); ++i) {
                Object path;
                if (i % 2 == 0) {
                    path = Lists.reverse(Lists.newArrayList(matrix.column(i).values()));
                } else {
                    path = matrix.column(i).values();
                }

                Graphs.addPath(g, (Iterable)path);
            }

            Graphs.addPath(g, matrix.row(Integer.valueOf(0)).values());
            Graphs.addPath(g, Lists.reverse(Lists.newArrayList(matrix.row(matrix.rowKeySet().size() - 1).values())));

            Object path = matrix.row(2).values();
            Graphs.addBiPath(g, (Iterable)path);
            ListenableGraph g2 = new ListenableGraph(g);
            return g2;
        }

        static ListenableGraph<LengthData> createGraph() {
            Graph<LengthData> g = new TableGraph();
            Table<Integer, Integer, Point> leftMatrix = createMatrix(5, 10, new Point(0.0D, 0.0D));
            Iterator var2 = leftMatrix.columnMap().values().iterator();

            while(var2.hasNext()) {
                Map<Integer, Point> column = (Map)var2.next();
                Graphs.addBiPath(g, column.values());
            }

            Graphs.addBiPath(g, leftMatrix.row(Integer.valueOf(4)).values());
            Graphs.addBiPath(g, leftMatrix.row(Integer.valueOf(5)).values());
            Table<Integer, Integer, Point> rightMatrix = createMatrix(10, 7, new Point(30.0D, 6.0D));
            Iterator var6 = rightMatrix.rowMap().values().iterator();

            while(var6.hasNext()) {
                Map<Integer, Point> row = (Map)var6.next();
                Graphs.addBiPath(g, row.values());
            }

            Graphs.addBiPath(g, rightMatrix.column(Integer.valueOf(0)).values());
            Graphs.addBiPath(g, rightMatrix.column(rightMatrix.columnKeySet().size() - 1).values());
            Graphs.addPath(g, new Point[]{(Point)rightMatrix.get(Integer.valueOf(2), Integer.valueOf(0)), (Point)leftMatrix.get(Integer.valueOf(4), Integer.valueOf(4))});
            Graphs.addPath(g, new Point[]{(Point)leftMatrix.get(Integer.valueOf(5), Integer.valueOf(4)), (Point)rightMatrix.get(Integer.valueOf(4), Integer.valueOf(0))});
            return new ListenableGraph(g);
        }
    }
}
