package org.openlmis.referencedata.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.referencedata.domain.*;
import org.springframework.beans.factory.annotation.Autowired;

public class FacilityTypeApprovedProductRepositoryTest extends BaseCrudRepositoryIntegrationTest<FacilityTypeApprovedProduct>{

  @Autowired
  private FacilityTypeApprovedProductRepository ftapRepository;

  @Autowired
  private FacilityTypeRepository facilityTypeRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ProductCategoryRepository productCategoryRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProgramProductRepository programProductRepository;

  FacilityTypeApprovedProductRepository getRepository() {
    return this.ftapRepository;
  }

  private FacilityType facilityType;
  private FacilityType facilityType2;
  private ProgramProduct programProduct;
  private ProductCategory productCategory;
  private Program program;
  private Product product;

  private static final double DELTA = 1e-15;

  @Before
  public void setUp(){
    facilityTypeRepository.deleteAll();
    facilityType = new FacilityType();
    facilityType.setCode("facilityType");
    facilityTypeRepository.save(facilityType);
    facilityType2 = new FacilityType();
    facilityType2.setCode("newFacilityType");
    facilityTypeRepository.save(facilityType2);
    programRepository.deleteAll();
    program = new Program();
    program.setCode("programCode");
    programRepository.save(program);
    productRepository.deleteAll();
    product = new Product();
    product.setCode("productCode");
    product.setPrimaryName("productPrimaryName");
    product.setDispensingUnit("unit");
    product.setDosesPerDispensingUnit(10);
    product.setPackSize(1);
    product.setPackRoundingThreshold(0);
    product.setRoundToZero(false);
    product.setActive(true);
    product.setFullSupply(true);
    product.setTracer(false);
    productRepository.save(product);
    productCategoryRepository.deleteAll();
    productCategory = new ProductCategory();
    productCategory.setCode("productCategoryCode");
    productCategory.setName("productCategoryName");
    productCategory.setDisplayOrder(1);
    productCategoryRepository.save(productCategory);
    programProductRepository.deleteAll();
    programProduct = new ProgramProduct();
    programProduct.setProgram(program);
    programProduct.setProduct(product);
    programProduct.setProductCategory(productCategory);
    programProduct.setActive(true);
    programProduct.setDosesPerMonth(1);
    programProduct.setFullSupply(true);
    programProduct.setPricePerPack(new Money("20.99"));
    programProductRepository.save(programProduct);
  }

  @Override
  FacilityTypeApprovedProduct generateInstance() {
    FacilityTypeApprovedProduct ftap = new FacilityTypeApprovedProduct();
    ftap.setFacilityType(facilityType);
    ftap.setProgramProduct(programProduct);
    ftap.setMaxMonthsOfStock(12.00);
    return ftap;
  }

  @Test
  public void testEditExistingProducts(){
    ftapRepository.save(this.generateInstance());
    Iterable<FacilityTypeApprovedProduct> all = ftapRepository.findAll();
    FacilityTypeApprovedProduct ftap = all.iterator().next();
    ftap.setMaxMonthsOfStock(10.00);
    ftap.setFacilityType(facilityType2);
    ftapRepository.save(ftap);
    Assert.assertEquals("newFacilityType", ftap.getFacilityType().getCode());
    Assert.assertEquals(10.00, ftap.getMaxMonthsOfStock(), DELTA);
  }
}