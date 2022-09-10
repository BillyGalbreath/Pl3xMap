package net.pl3x.map.heightmap;

import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import net.pl3x.map.coordinate.BlockCoordinate;
import net.pl3x.map.render.ScanData;
import net.pl3x.map.util.Mathf;

public abstract class Heightmap extends Keyed {
    public int[] x = new int[16];
    public int[] z = new int[16];

    public Heightmap(Key key) {
        super(key);
    }

    public abstract int getColor(BlockCoordinate coordinate, ScanData data, ScanData.Data scanData);

    public int getColor(ScanData data1, ScanData data2, int heightColor, int step) {
        if (data2 != null) {
            if (data1.getBlockPos().getY() > data2.getBlockPos().getY()) {
                heightColor -= step;
            } else if (data1.getBlockPos().getY() < data2.getBlockPos().getY()) {
                heightColor += step;
            }
        }
        return Mathf.clamp(0x00, 0x44, heightColor);
    }
}
