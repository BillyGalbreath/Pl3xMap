import * as L from "leaflet";
import {Marker} from "./Marker";
import {MarkerOptions} from "./options/MarkerOptions";
import {Util} from "../../util/Util";

export class Polygon extends Marker {

    // [[0,0],[0,0],[0,0]]

    constructor(data: unknown[], options: MarkerOptions | undefined) {
        const poly = [];
        for (const points of data as unknown[][]) {
            const line = [];
            for (const point of points) {
                line.push(Util.toLatLng(point as L.PointTuple))
            }
            poly.push(line);
        }

        const marker = L.polygon(poly, {
            ...options?.properties,
            smoothFactor: 1.0,
            noClip: false,
            bubblingMouseEvents: true,
            interactive: true,
            attribution: undefined
        });

        super(marker);
    }
}
