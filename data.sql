
INSERT INTO referencedata.users(id, username, firstName, lastName, verified, active)
VALUES ('123e4567-e89b-12d3-a456-426655440003','Bobusername', 'Bob', 'Boby', 'TRUE', 'TRUE');

INSERT INTO referencedata.programs(id, code, name, skippable)
VALUES ('123e4567-e89b-12d3-a456-426655440004','code89', 'ART', 'TRUE');

INSERT INTO referencedata.geographic_levels(id, code, levelNumber)
VALUES ('123e4567-e89b-12d3-a456-426655440001', 'code6', '123');

INSERT INTO referencedata.geographic_zones(id, code, levelid)
VALUES ('123e4567-e89b-12d3-a456-426655440002', 'code9', '123e4567-e89b-12d3-a456-426655440001');

INSERT INTO referencedata.facility_types(id, code)
VALUES ('123e4567-e89b-12d3-a456-426655440005', 'code34');

INSERT INTO referencedata.facilities(id, code, name, geographiczoneid, typeid, active, enabled)
VALUES ('123e4567-e89b-12d3-a456-426655440006', 'code35', 'facility name', '123e4567-e89b-12d3-a456-426655440002',
        '123e4567-e89b-12d3-a456-426655440005', 'TRUE', 'TRUE');

INSERT INTO referencedata.program_categories(id, code, name, displayOrder)
VALUES ('123e4567-e89b-12d3-a456-426655440013', 'code234', 'Category1', '11');

INSERT INTO referencedata.program_categories(id, code, name, displayOrder)
VALUES ('123e4567-e89b-12d3-a456-426655440014', 'code235', 'Category2', '12');

INSERT INTO referencedata.products(id, code, primaryName, dispensingUnit, dosesPerDispensingUnit, packSize,
                                    packRoundingThreshold, roundToZero, active, fullSupply, tracer, productCategoryId)
VALUES ('123e4567-e89b-12d3-a456-426655440010', 'code456', 'primaryname', 'dispensing Unit', '10', '20', '5',
         'TRUE', 'TRUE', 'TRUE', 'TRUE', '123e4567-e89b-12d3-a456-426655440013');

INSERT INTO referencedata.products(id, code, primaryName, dispensingUnit, dosesPerDispensingUnit, packSize,
                                    packRoundingThreshold, roundToZero, active, fullSupply, tracer, productCategoryId)
VALUES ('123e4567-e89b-12d3-a456-426655440011', 'code457', 'primaryname2', 'dispensing Unit2', '10', '20', '5',
         'TRUE', 'TRUE', 'FALSE', 'TRUE', '123e4567-e89b-12d3-a456-426655440014');





INSERT INTO referencedata.schedule(id, code, name)
VALUES ('123e4567-e89b-12d3-a456-426655440019', 'cod129', 'schedule name 129');

--INSERT INTO referencedata.periods(id, processingScheduleId, name)
--VALUES ('123e4567-e89b-12d3-a456-426655440018', '123e4567-e89b-12d3-a456-426655440019',
--        'period name');

--INSERT INTO referencedata.periods(id, processingScheduleId, name, startDate, endDate)
--VALUES ('123e4567-e89b-12d3-a456-426655440018', '123e4567-e89b-12d3-a456-426655440019',
--        'period name', '\\xaced00057372000d6a6176612e74696d652e536572955d84ba1b2248b20c00007870770e05000007e007120d093429a9e4c078',
--         '\\xaced00057372000d6a6176612e74696d652e536572955d84ba1b2248b20c00007870770e05000007e007120d093429a9e4c078');

INSERT INTO requisition.requisitions(id, facilityId, programId, processingPeriodId, status, emergency)
VALUES ('123e4567-e89b-12d3-a456-426655440017','123e4567-e89b-12d3-a456-426655440006',
        '123e4567-e89b-12d3-a456-426655440004', '123e4567-e89b-12d3-a456-426655440018',
        'SUBMITTED', 'TRUE');

INSERT INTO requisition.orders(id, requisitionCode, userId, programId, requestingFacilityId,
                               receivingFacilityId, supplyingFacilityId, orderCode, status, quotedCost)
VALUES ('123e4567-e89b-12d3-a456-426655440007', '123e4567-e89b-12d3-a456-426655440017',
        '123e4567-e89b-12d3-a456-426655440003',
        '123e4567-e89b-12d3-a456-426655440004', '123e4567-e89b-12d3-a456-426655440006',
        '123e4567-e89b-12d3-a456-426655440006', '123e4567-e89b-12d3-a456-426655440006',
        'code36', 'ORDERED', '200');



--INSERT INTO requisition.orders(id, userId, programId, requestingFacilityId,
--                               receivingFacilityId, supplyingFacilityId, orderCode, status, quotedCost)
--VALUES ('123e4567-e89b-12d3-a456-426655440007', '123e4567-e89b-12d3-a456-426655440003',
--        '123e4567-e89b-12d3-a456-426655440004', '123e4567-e89b-12d3-a456-426655440006',
--        '123e4567-e89b-12d3-a456-426655440006', '123e4567-e89b-12d3-a456-426655440006',
--        'code36', 'ORDERED', '200');

INSERT INTO requisition.order_proof_of_deliveries(id, orderId, deliveredBy, receivedBy, receivedDate)
VALUES ('123e4567-e89b-12d3-a456-426655440008', '123e4567-e89b-12d3-a456-426655440007',
        'Bobselivered', 'Bobreceived', '2004-07-18');

INSERT INTO requisition.order_lines(id, orderId, productId, orderedQuantity)
VALUES ('123e4567-e89b-12d3-a456-426655440009', '123e4567-e89b-12d3-a456-426655440007',
        '123e4567-e89b-12d3-a456-426655440010', '100');

INSERT INTO requisition.order_lines(id, orderId, productId, orderedQuantity)
VALUES ('123e4567-e89b-12d3-a456-426655440012', '123e4567-e89b-12d3-a456-426655440007',
        '123e4567-e89b-12d3-a456-426655440011', '200');

INSERT INTO requisition.order_proof_of_delivery_lines(id, orderLineId, orderProofOfDeliveryId, packToShip,
                            quantityShipped, quantityReceived, quantityReturned, replacedProductCode, notes)
VALUES ('123e4567-e89b-12d3-a456-426655440015', '123e4567-e89b-12d3-a456-426655440009',
        '123e4567-e89b-12d3-a456-426655440008', '100', '100', '100', '1', 'code456', 'notes');

INSERT INTO requisition.order_proof_of_delivery_lines(id, orderLineId, orderProofOfDeliveryId, packToShip,
                            quantityShipped, quantityReceived, quantityReturned, replacedProductCode, notes)
VALUES ('123e4567-e89b-12d3-a456-426655440016', '123e4567-e89b-12d3-a456-426655440012',
        '123e4567-e89b-12d3-a456-426655440008', '100', '100', '100', '1', 'code456', 'notes');