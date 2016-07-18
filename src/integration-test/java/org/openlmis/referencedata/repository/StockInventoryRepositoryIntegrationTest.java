package org.openlmis.referencedata.repository;

import org.openlmis.referencedata.domain.StockInventory;
import org.springframework.beans.factory.annotation.Autowired;

public class StockInventoryRepositoryIntegrationTest
    extends BaseCrudRepositoryIntegrationTest<StockInventory> {

  @Autowired
  private StockInventoryRepository stockInventoryRepository;

  public StockInventoryRepository getRepository() {
    return this.stockInventoryRepository;
  }

  public StockInventory generateInstance() {
    StockInventory stockInventory = new StockInventory();
    stockInventory.setName("stockInventoryName");
    return stockInventory;
  }
}
