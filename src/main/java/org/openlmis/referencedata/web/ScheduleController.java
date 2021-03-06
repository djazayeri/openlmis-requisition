package org.openlmis.referencedata.web;

import org.openlmis.referencedata.domain.Period;
import org.openlmis.referencedata.domain.Schedule;
import org.openlmis.referencedata.i18n.ExposedMessageSource;
import org.openlmis.referencedata.repository.PeriodRepository;
import org.openlmis.referencedata.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@RepositoryRestController
public class ScheduleController {
  private Logger logger = LoggerFactory.getLogger(ScheduleController.class);

  @Autowired
  private ScheduleRepository scheduleRepository;

  @Autowired
  private PeriodRepository periodRepository;

  @Autowired
  private ExposedMessageSource messageSource;

  @RequestMapping(value = "/schedules/{id}/difference", method = RequestMethod.GET)
  @ResponseBody
  public String getTotalDifference(@PathVariable("id") UUID scheduleId) {
    Schedule schedule = scheduleRepository.findOne(scheduleId);

    Iterable<Period> allPeriods = periodRepository.findByProcessingSchedule(schedule);
    if (!allPeriods.equals(null)) {
      Period firstPeriod = allPeriods.iterator().next();
      Period lastPeriod = periodRepository.findFirst1ByOrderByEndDateDesc();
      java.time.Period total = java.time.Period.between(firstPeriod.getStartDate(),
              lastPeriod.getEndDate());
      String months = Integer.toString(total.getMonths());
      String days = Integer.toString(total.getDays());

      String[] msgArgs = {months, days};
      logger.debug("Returning total days and months of schedule periods");

      return messageSource.getMessage("requisition.message.totalPeriod", msgArgs,
              LocaleContextHolder.getLocale());
    } else {
      String[] messageArgs = {"0","0"};
      return messageSource.getMessage("requisition.message.totalPeriod", messageArgs,
              LocaleContextHolder.getLocale());
    }
  }
}