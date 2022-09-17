import * as L from "leaflet";
import {Pl3xMap} from "../Pl3xMap";
import {Label} from "../settings/Lang";
import {World} from "../world/World";
import {createSVGIcon, handleKeyboardEvent} from "../util/Util";
import BaseTab from "./BaseTab";
import '../svg/maps.svg';

interface WorldListItem {
    fieldset: HTMLFieldSetElement;
    inputs: Map<Label, HTMLInputElement>
}

export default class WorldsTab extends BaseTab {
    private readonly _worlds: Map<World, WorldListItem> = new Map();
    private readonly _skeleton: HTMLParagraphElement;
    private readonly _list: HTMLFieldSetElement;

    constructor(pl3xmap: Pl3xMap) {
        super(pl3xmap, 'worlds');

        const lang = pl3xmap.settings!.lang;

        this._button.appendChild(createSVGIcon('maps'));
        this._button.setAttribute('aria-label', lang.worlds.label);

        const heading = L.DomUtil.create('h2', '', this._content);
        heading.innerText = lang.worlds.label;
        heading.id = 'worlds-heading';

        this._skeleton = L.DomUtil.create('p', '', this._content);
        this._skeleton.innerText = lang.worlds.value;
        this._skeleton.tabIndex = -1;

        this._list = L.DomUtil.create('fieldset', 'menu', this._content);
        this._list.setAttribute('aria-labelledby', 'worlds-heading');
        this._list.setAttribute('role', 'radiogroup');

        this.initEvents();
    }

    private initEvents() {
        addEventListener('worldadded', (e: CustomEvent<World>) => this.createListItem(e.detail));
        addEventListener('worldremoved', (e: CustomEvent<World>) => this.removeListItem(e.detail)); //TODO: Refreshless config updates?
        addEventListener('rendererselected', (e: CustomEvent<World>) => {
            this._worlds.get(e.detail)!.inputs.get(e.detail.currentRenderer!)!.checked = true;
        });

        this._list.addEventListener('keydown', (e: KeyboardEvent) =>
            handleKeyboardEvent(e, Array.from(this._list.elements) as HTMLElement[]))
    }

    private createListItem(world: World) {
        const fieldset = L.DomUtil.create('fieldset'),
            legend = L.DomUtil.create('legend');

        legend.innerText = world.displayName;
        fieldset.appendChild(legend);

        const inputs = new Map();

        world.renderers.forEach(renderer => {
            const input = L.DomUtil.create('input'),
                label = L.DomUtil.create('label');

            fieldset.appendChild(input);
            fieldset.appendChild(label);

            //icon.src = `images/icon/${renderer}.png`;
            label.style.backgroundImage = `url('images/icon/registered/${renderer.label}.png')`;
            label.title = renderer.value;
            input.id = label.htmlFor = `${world.name}-${renderer.label}`;
            input.type = 'radio';
            input.name = 'world';
            input.checked = false;
            input.classList.add("renderer");
            input.addEventListener('click', async (e: MouseEvent) => {
                this._pl3xmap.worldManager.setWorld(world, renderer)
                    // Don't update radio button if switch fails
                    .catch(() => e.preventDefault());
            });

            inputs.set(renderer, input);
        });

        this._worlds.set(world, {
            fieldset,
            inputs
        });

        this._skeleton.hidden = true;
        this._list.appendChild(fieldset);
    }

    private removeListItem(world: World) {
        const listItem = this._worlds.get(world);

        if (!listItem) {
            return;
        }

        listItem.fieldset.remove();

        this._worlds.delete(world);

        if (!this._worlds.size) {
            this._skeleton.hidden = false;
        }
    }

    onActivate() {
        if (this._worlds.size) {
            (this._list.querySelector('input:checked') as HTMLElement)!.focus()
        } else {
            this._skeleton.focus();
        }
    }
}
