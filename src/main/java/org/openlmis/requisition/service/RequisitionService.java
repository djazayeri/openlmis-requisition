package org.openlmis.requisition.service;

import org.openlmis.requisition.domain.Requisition;
import org.openlmis.requisition.domain.RequisitionStatus;
import org.openlmis.requisition.repository.RequisitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequisitionService {

  Logger logger = LoggerFactory.getLogger(RequisitionService.class);

  @Autowired
  RequisitionRepository requisitionRepository;

  public boolean skip(Requisition requsition) {

    if (requsition == null) {
      logger.debug("Skip failed - requsition cannot be null");
    } else if (!requsition.getStatus().equals(RequisitionStatus.INITIATED)) {
      logger.debug("Skip failed - requsition has bad status");
    } else if (!requsition.getProgram().getSkippable()) {
      logger.debug("Skip failed - requsition program does not allow skipping");
    } else {
      logger.debug("Requsition skipped");
      requsition.setStatus(RequisitionStatus.SKIPPED);
      requisitionRepository.save(requsition);
      return true;
    }
    return false;
  }

}
