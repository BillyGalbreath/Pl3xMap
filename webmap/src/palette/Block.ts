export class Block {
    private readonly _block: number;
    private readonly _biome: number;
    private readonly _yPos: number;
    private readonly _minY: number;

    constructor(packed: number, minY: number) {
        this._block = packed >>> 22;
        this._biome = (packed << 10) >> 22;
        this._yPos = ((packed << 20) >> 20);
        this._minY = minY;
    }

    get block(): number {
        return this._block;
    }

    get biome(): number {
        return this._biome;
    }

    get yPos(): number {
        return this._yPos + this._minY;
    }
}
