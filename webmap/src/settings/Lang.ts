/**
 * Represents language settings.
 */
export class Lang {
    private readonly _title: string;
    private readonly _coords: Label;
    private readonly _blockInfo: Label;
    private readonly _layers: Label;
    private readonly _markers: Label;
    private readonly _players: Label;
    private readonly _worlds: Label;

    constructor(title: string, coords: Label, blockInfo: Label, layers: Label, markers: Label, players: Label, worlds: Label) {
        this._title = title;
        this._coords = coords;
        this._blockInfo = blockInfo;
        this._layers = layers;
        this._markers = markers;
        this._players = players;
        this._worlds = worlds;
    }

    get title(): string {
        return this._title;
    }

    get coords(): Label {
        return this._coords;
    }

    get blockInfo(): Label {
        return this._blockInfo;
    }

    get layers(): Label {
        return this._layers;
    }

    get markers(): Label {
        return this._markers;
    }

    get players(): Label {
        return this._players;
    }

    get worlds(): Label {
        return this._worlds;
    }
}

/**
 * Represents a label and value.
 */
export class Label {
    private readonly _label: string
    private readonly _value: string;

    constructor(label: string, value: string) {
        this._label = label;
        this._value = value;
    }

    get label(): string {
        return this._label;
    }

    get value(): string {
        return this._value;
    }
}
