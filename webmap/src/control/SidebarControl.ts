import {Control, DomEvent, DomUtil} from "leaflet";
import WorldsTab from "../sidebar/WorldsTab";
import PlayersTab from "../sidebar/PlayersTab";
import MarkersTab from "../sidebar/MarkersTab";
import disableClickPropagation = DomEvent.disableClickPropagation;
import disableScrollPropagation = DomEvent.disableScrollPropagation;
import {Pl3xMap} from "../Pl3xMap";
import Pl3xmapLeafletMap from "../map/Pl3xmapLeafletMap";
import LayersTab from "../sidebar/LayersTab";

export interface SidebarTab {
    get button(): HTMLElement;
    get content(): HTMLElement;
    get id(): string;
    onAdd?: (map: Pl3xmapLeafletMap) => void;
    onActivate?: () => void;
    onDeactivate?: () => void;
}

export default class SidebarControl extends Control {
    private readonly _container: HTMLDivElement;
    private readonly _buttons: HTMLElement;
    private readonly _content: HTMLElement;

    private _tabs: Set<SidebarTab> = new Set();
    private _expanded: boolean = false;
    private _currentTab?: SidebarTab;

    constructor(pl3xmap: Pl3xMap) {
        super({position: 'topright'});

        this._container = DomUtil.create('div');
        this._container.id = 'sidebar';

        this._buttons = DomUtil.create('header', '', this._container);
        this._buttons.id = 'sidebar__buttons';

        this._content = DomUtil.create('section', '', this._container);
        this._content.id = 'sidebar__content';

        disableClickPropagation(this._container);
        disableScrollPropagation(this._container);

        this.addTab(new WorldsTab(pl3xmap));
        this.addTab(new LayersTab(pl3xmap));
        this.addTab(new PlayersTab(pl3xmap));
        this.addTab(new MarkersTab(pl3xmap));
    }

    onAdd(map: Pl3xmapLeafletMap) {
        for (const tab of this._tabs) {
            if (tab.onAdd) {
                tab.onAdd(map);
            }
        }

        return this._container;
    }

    toggle() {
        if (this._expanded) {
            this.collapse();
        } else {
            this.expand();
        }
    }

    expand() {
        this._expanded = true;
        this._container.classList.add('sidebar--expanded');
        this._content.setAttribute('aria-hidden', 'false');
    }

    collapse() {
        if (this._currentTab) {
            this.deactivateTab(this._currentTab, true);
        }

        this._expanded = false;
        this._container.classList.remove('sidebar--expanded');
        this._content.setAttribute('aria-hidden', 'true');
    }

    addTab(tab: SidebarTab) {
        if (this._tabs.has(tab)) {
            return;
        }

        this._tabs.add(tab);

        tab.button.addEventListener('click', () => this.toggleTab(tab));
        this._buttons.appendChild(tab.button);

        tab.content.addEventListener('keydown', (e: KeyboardEvent) => {
            if (e.key === 'Escape') {
                this.collapse();
            }
        });

        this._content.appendChild(tab.content);
    }

    removeTab(tab: SidebarTab) {
        if (!this._tabs.has(tab)) {
            return;
        }

        this.deactivateTab(tab, false);
        tab.button.remove();
    }

    toggleTab(tab: SidebarTab) {
        if (this._currentTab === tab) {
            this.collapse();
        } else {
            this.activateTab(tab);
        }
    }

    activateTab(tab: SidebarTab) {
        if (!this._expanded) {
            this.expand();
        }

        if (this._currentTab) {
            this.deactivateTab(this._currentTab, false);
        }

        tab.button.setAttribute('aria-expanded', 'true');
        tab.content.hidden = false;
        tab.content.setAttribute('aria-hidden', 'false');
        this._currentTab = tab;

        if (tab.onActivate) {
            tab.onActivate();
        }
    }

    deactivateTab(tab: SidebarTab, moveFocus: boolean) {
        if (tab !== this._currentTab) {
            return;
        }

        tab.button.setAttribute('aria-expanded', 'false');
        tab.content.hidden = true;
        tab.content.setAttribute('aria-hidden', 'true');
        this._currentTab = undefined;

        if (moveFocus) {
            tab.button.focus();
        }

        if (tab.onDeactivate) {
            tab.onDeactivate();
        }
    }
}
