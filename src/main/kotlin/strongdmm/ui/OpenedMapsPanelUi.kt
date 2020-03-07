package strongdmm.ui

import imgui.ImGui.*
import imgui.enums.ImGuiCol
import imgui.enums.ImGuiCond
import strongdmm.byond.dmm.Dmm
import strongdmm.event.Event
import strongdmm.event.EventConsumer
import strongdmm.event.EventSender
import strongdmm.util.imgui.RED32
import strongdmm.util.imgui.setItemHoveredTooltip
import strongdmm.util.imgui.smallButton
import strongdmm.util.imgui.window
import strongdmm.window.AppWindow

class OpenedMapsPanelUi : EventConsumer, EventSender {
    private var openedMaps: Set<Dmm> = emptySet()

    init {
        consumeEvent(Event.Global.Provider.OpenedMaps::class.java, ::handleProviderOpenedMaps)
    }

    fun process() {
        if (openedMaps.isEmpty()) {
            return
        }

        setNextWindowPos(AppWindow.windowWidth - 160f, 30f, ImGuiCond.Once)
        setNextWindowSize(150f, 150f, ImGuiCond.Once)
        setNextWindowCollapsed(true, ImGuiCond.Once)

        sendEvent(Event.MapHolderController.FetchSelected { selectedMap ->
            window("${selectedMap?.mapName}###opened_maps") {
                openedMaps.forEach { map ->
                    pushStyleColor(ImGuiCol.ButtonHovered, RED32)
                    smallButton("X##close_map_${map.visibleMapPath}") {
                        sendEvent(Event.MapHolderController.Close(map.id))
                    }
                    popStyleColor()

                    sameLine()

                    if (selectable(map.mapName, selectedMap == map)) {
                        if (selectedMap != map) {
                            sendEvent(Event.MapHolderController.Switch(map.id))
                        }
                    }
                    setItemHoveredTooltip(map.visibleMapPath)
                }
            }
        })
    }

    private fun handleProviderOpenedMaps(event: Event<Set<Dmm>, Unit>) {
        openedMaps = event.body
    }
}
