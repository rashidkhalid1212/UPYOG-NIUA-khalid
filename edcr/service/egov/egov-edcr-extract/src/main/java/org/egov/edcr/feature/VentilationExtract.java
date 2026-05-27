package org.egov.edcr.feature;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.entity.blackbox.MeasurementDetail;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.ConfigCacheService;
import org.egov.edcr.service.LayerNames;
import org.egov.edcr.utility.DcrConstants;
import org.egov.edcr.utility.Util;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.kabeja.dxf.DXFLWPolyline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentilationExtract extends FeatureExtract {

    private static final Logger LOG = LogManager.getLogger(VentilationExtract.class);
    @Autowired
    private LayerNames layerNames;

    @Autowired
    private AppConfigValueService appConfigValueService;

    @Autowired
    private ConfigCacheService configCacheService;

    @Override
    public PlanDetail extract(PlanDetail pl) {
        for (Block b : pl.getBlocks()) {
            if (b.getBuilding() != null && b.getBuilding().getFloors() != null
                    && !b.getBuilding().getFloors().isEmpty()) {
                for (Floor f : b.getBuilding().getFloors()) {

                    if (configCacheService.isUnitLayerEnabled()) {
                        unitWiseExtract(pl, b, f);
                    } else {
                        floorWiseExtract(pl, b, f);
                    }
                }
            }
        }

        return pl;
    }

    public void unitWiseExtract(PlanDetail pl, Block b, Floor f) {
        if (f.getUnits() != null && !f.getUnits().isEmpty())
            for (FloorUnit floorUnit : f.getUnits()) {

                /*
                 * Adding general light and ventilation at floor level
                 */
//                List<DXFLWPolyline> lightAndVentilations = Util.getPolyLinesByLayer(pl.getDoc(), String.format(
//                        layerNames.getLayerName("LAYER_NAME_UNIT_LIGHT_VENTILATION"), b.getNumber(), f.getNumber(), floorUnit.getNumber()));
//                if (!lightAndVentilations.isEmpty()) {
//                    List<Measurement> lightAndventilationMeasurements = lightAndVentilations.stream()
//                            .map(polyline -> new MeasurementDetail(polyline, true)).collect(Collectors.toList());
//                    floorUnit.getLightAndVentilation().setMeasurements(lightAndventilationMeasurements);
//
//                    floorUnit.getLightAndVentilation()
//                            .setHeightOrDepth((Util.getListOfDimensionValueByLayer(pl,
//                                    String.format(layerNames.getLayerName("LAYER_NAME_UNIT_LIGHT_VENTILATION"),
//                                            b.getNumber(), f.getNumber(), floorUnit.getNumber()))));
//
//                }


                String layerName = String.format(layerNames.getLayerName("LAYER_NAME_UNIT_LIGHT_VENTILATION"), b.getNumber(), f.getNumber(), floorUnit.getNumber());
                processLightAndVentilation(pl, layerName, floorUnit.getLightAndVentilation());

                /*
                 * Adding regular room wise light and ventilation
                 */
//                for (Room room : floorUnit.getRegularRooms()) {
//                    String regularRoomLayerName = String.format(
//                            layerNames.getLayerName("LAYER_NAME_UNIT_ROOM_LIGHT_VENTILATION"), b.getNumber(),
//                            f.getNumber(), floorUnit.getNumber(), room.getNumber(), "+\\d");
//
//                    List<String> regularRoomLayers = Util.getLayerNamesLike(pl.getDoc(), regularRoomLayerName);
//                    if (!regularRoomLayers.isEmpty()) {
//                        for (String regularRoomLayer : regularRoomLayers) {
//                            List<DXFLWPolyline> lightAndventilations = Util.getPolyLinesByLayer(pl.getDoc(),
//                                    regularRoomLayer);
//                            if (!lightAndventilations.isEmpty()) {
//                                List<Measurement> lightAndventilationMeasurements = lightAndventilations.stream()
//                                        .map(polyline -> new MeasurementDetail(polyline, true))
//                                        .collect(Collectors.toList());
//                                room.getLightAndVentilation().setMeasurements(lightAndventilationMeasurements);
//
//                                room.getLightAndVentilation().setHeightOrDepth(
//                                        (Util.getListOfDimensionValueByLayer(pl, regularRoomLayer)));
//                            }
//                        }
//                    }
//                }

                processRoomVentilation(pl, floorUnit.getRegularRooms(), String.format(layerNames.getLayerName("LAYER_NAME_UNIT_ROOM_LIGHT_VENTILATION"), b.getNumber(), f.getNumber(), floorUnit.getNumber(), "%s", "%s"));

                /*
                 * Adding AC room wise light and ventilation
                 */
//                for (Room room : floorUnit.getAcRooms()) {
//                    String acRoomLayerName = String.format(
//                            layerNames.getLayerName("LAYER_NAME_UNIT_ACROOM_LIGHT_VENTILATION"), b.getNumber(),
//                            f.getNumber(), floorUnit.getNumber(), room.getNumber(), "+\\d");
//
//                    List<String> acRoomLayers = Util.getLayerNamesLike(pl.getDoc(), acRoomLayerName);
//                    if (!acRoomLayers.isEmpty()) {
//                        for (String acRoomLayer : acRoomLayers) {
//
//                            List<DXFLWPolyline> lightAndventilations = Util.getPolyLinesByLayer(pl.getDoc(),
//                                    acRoomLayer);
//                            if (!lightAndventilations.isEmpty()) {
//                                List<Measurement> lightAndventilationMeasurements = lightAndventilations.stream()
//                                        .map(polyline -> new MeasurementDetail(polyline, true))
//                                        .collect(Collectors.toList());
//                                room.getLightAndVentilation().setMeasurements(lightAndventilationMeasurements);
//
//                                room.getLightAndVentilation()
//                                        .setHeightOrDepth((Util.getListOfDimensionValueByLayer(pl, acRoomLayer)));
//
//                            }
//
//                        }
//                    }
//                }

                processRoomVentilation(pl, floorUnit.getAcRooms(), String.format(layerNames.getLayerName("LAYER_NAME_UNIT_ACROOM_LIGHT_VENTILATION"), b.getNumber(), f.getNumber(), floorUnit.getNumber(), "%s", "%s"));
            }
    }

    public void floorWiseExtract(PlanDetail pl, Block b, Floor f) {
        /*
         * Adding general light and ventilation at floor level
         */
//        List<DXFLWPolyline> lightAndVentilations = Util.getPolyLinesByLayer(pl.getDoc(), String.format(
//                layerNames.getLayerName("LAYER_NAME_LIGHT_VENTILATION"), b.getNumber(), f.getNumber()));
//        if (!lightAndVentilations.isEmpty()) {
//            List<Measurement> lightAndventilationMeasurements = lightAndVentilations.stream()
//                    .map(polyline -> new MeasurementDetail(polyline, true)).collect(Collectors.toList());
//            f.getLightAndVentilation().setMeasurements(lightAndventilationMeasurements);
//
//            f.getLightAndVentilation()
//                    .setHeightOrDepth((Util.getListOfDimensionValueByLayer(pl,
//                            String.format(layerNames.getLayerName("LAYER_NAME_LIGHT_VENTILATION"),
//                                    b.getNumber(), f.getNumber()))));
//
//        }

        String layerName = String.format(
                layerNames.getLayerName("LAYER_NAME_LIGHT_VENTILATION"),
                b.getNumber(),
                f.getNumber());

        processLightAndVentilation(
                pl,
                layerName,
                f.getLightAndVentilation());

        /*
         * Adding regular room wise light and ventilation
         */
//        for (Room room : f.getRegularRooms()) {
//            String regularRoomLayerName = String.format(
//                    layerNames.getLayerName("LAYER_NAME_ROOM_LIGHT_VENTILATION"), b.getNumber(),
//                    f.getNumber(), room.getNumber(), "+\\d");
//
//            List<String> regularRoomLayers = Util.getLayerNamesLike(pl.getDoc(), regularRoomLayerName);
//            if (!regularRoomLayers.isEmpty()) {
//                for (String regularRoomLayer : regularRoomLayers) {
//                    List<DXFLWPolyline> lightAndventilations = Util.getPolyLinesByLayer(pl.getDoc(),
//                            regularRoomLayer);
//                    if (!lightAndventilations.isEmpty()) {
//                        List<Measurement> lightAndventilationMeasurements = lightAndventilations.stream()
//                                .map(polyline -> new MeasurementDetail(polyline, true))
//                                .collect(Collectors.toList());
//                        room.getLightAndVentilation().setMeasurements(lightAndventilationMeasurements);
//
//                        room.getLightAndVentilation().setHeightOrDepth(
//                                (Util.getListOfDimensionValueByLayer(pl, regularRoomLayer)));
//                    }
//                }
//            }
//        }

        processRoomVentilation(
                pl,
                f.getRegularRooms(),
                String.format(
                        layerNames.getLayerName("LAYER_NAME_ROOM_LIGHT_VENTILATION"), b.getNumber(),
                        f.getNumber(), "%s",
                        "%s"));

        /*
         * Adding AC room wise light and ventilation
         */
//        for (Room room : f.getAcRooms()) {
//            String acRoomLayerName = String.format(
//                    layerNames.getLayerName("LAYER_NAME_ACROOM_LIGHT_VENTILATION"), b.getNumber(),
//                    f.getNumber(), room.getNumber(), "+\\d");
//
//            List<String> acRoomLayers = Util.getLayerNamesLike(pl.getDoc(), acRoomLayerName);
//            if (!acRoomLayers.isEmpty()) {
//                for (String acRoomLayer : acRoomLayers) {
//
//                    List<DXFLWPolyline> lightAndventilations = Util.getPolyLinesByLayer(pl.getDoc(),
//                            acRoomLayer);
//                    if (!lightAndventilations.isEmpty()) {
//                        List<Measurement> lightAndventilationMeasurements = lightAndventilations.stream()
//                                .map(polyline -> new MeasurementDetail(polyline, true))
//                                .collect(Collectors.toList());
//                        room.getLightAndVentilation().setMeasurements(lightAndventilationMeasurements);
//
//                        room.getLightAndVentilation()
//                                .setHeightOrDepth((Util.getListOfDimensionValueByLayer(pl, acRoomLayer)));
//
//                    }
//
//                }
//            }
//        }

        processRoomVentilation(
                pl,
                f.getAcRooms(),
                String.format(
                        layerNames.getLayerName("LAYER_NAME_ACROOM_LIGHT_VENTILATION"),
                        b.getNumber(),
                        f.getNumber(),
                        "%s",
                        "%s"));
    }

    private void processLightAndVentilation(
            PlanDetail pl,
            String layerName,
            MeasurementWithHeight lightAndVentilation) {

        List<DXFLWPolyline> polylines =
                Util.getPolyLinesByLayer(pl.getDoc(), layerName);

        if (!polylines.isEmpty()) {

            List<Measurement> measurements = polylines.stream()
                    .map(polyline -> new MeasurementDetail(polyline, true))
                    .collect(Collectors.toList());

            lightAndVentilation.setMeasurements(measurements);

            lightAndVentilation.setHeightOrDepth(
                    Util.getListOfDimensionValueByLayer(pl, layerName));
        }
    }

    private void processRoomVentilation(
            PlanDetail pl,
            List<Room> rooms,
            String layerPattern) {

        if (rooms == null || rooms.isEmpty()) {
            return;
        }

        for (Room room : rooms) {
            String roomLayerName = String.format(
                    layerPattern,
                    room.getNumber(),
                    "+\\d");

            List<String> roomLayers = Util.getLayerNamesLike(pl.getDoc(), roomLayerName);

            for (String roomLayer : roomLayers) {
                processLightAndVentilation(
                        pl,
                        roomLayer,
                        room.getLightAndVentilation());
            }
        }
    }

    @Override
    public PlanDetail validate(PlanDetail pl) {
        return pl;
    }
}
