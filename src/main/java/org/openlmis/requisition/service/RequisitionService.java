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
  private final String requisitionNullMessage = "requisition cannot be null";
  private final String requisitionBadStatusMessage = "requisition has bad status";

  private Logger logger = LoggerFactory.getLogger(RequisitionService.class);

  @Autowired
  RequisitionRepository requisitionRepository;

  public boolean tryDelete(Requisition requisition) {
    if (requisition == null) {
      logger.debug("Delete failed - " + requisitionNullMessage);
    } else if (!requisition.getStatus().equals(RequisitionStatus.INITIATED)) {
      logger.debug("Delete failed - " + requisitionBadStatusMessage);
    } else {
      logger.debug("Requisition deleted");
      requisitionRepository.delete(requisition);
      return true;
    }

    return false;
  }

  public boolean skip(Requisition requisition) {

    if (requisition == null) {
      logger.debug("Skip failed - " + requisitionNullMessage);
    } else if (!requisition.getStatus().equals(RequisitionStatus.INITIATED)) {
      logger.debug("Skip failed - " + requisitionBadStatusMessage);
    } else if (!requisition.getProgram().getSkippable()) {
      logger.debug("Skip failed - requisition program does not allow skipping");
    } else {
      logger.debug("Requisition skipped");
      requisition.setStatus(RequisitionStatus.SKIPPED);
      requisitionRepository.save(requisition);
      return true;
    }
    return false;
  }

}
