package uk.gov.ea.datareturns.tests.integration.model;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ControlledListsList;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Parameter;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReleasesAndTransfers;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReturnType;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchyLevel;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.implementations.ParameterHierarchy;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.ParameterRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.ReleasesAndTransfersRepository;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.ReturnTypeRepository;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by graham on 17/11/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
public class ParameterHierarchyNavigationTests {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ParameterHierarchyNavigationTests.class);

    @Inject
    private ParameterRepository parameterRepository;

    @Inject
    private ReturnTypeRepository returnTypeRepository;

    @Inject
    private ReleasesAndTransfersRepository releasesAndTransfersRepository;

    @Inject
    ParameterHierarchy parameterHierarchy;

    @Test
    public void traverseReturningLandfillAllParameters() {
        ReturnType returnType = returnTypeRepository.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);

        Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> result = parameterHierarchy
                .children(returnType);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.PARAMETER, result.getLeft().getControlledList());

        //        printList(result.getRight());
    }

    @Test
    public void traverseReturningLandfillAllUnits() {
        ReturnType returnType = returnTypeRepository.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);

        Parameter parameter = parameterRepository.getByName("Ziram");
        Assert.assertNotNull(parameter);

        Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> result = parameterHierarchy
                .children(returnType, parameter);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.UNIT, result.getLeft().getControlledList());

        //        printList(result.getRight());
    }

    @Test
    public void traverseReturningNullForLandfillWithReleasesAndTransfers() {
        ReturnType returnType = returnTypeRepository.getByName("Emissions to sewer");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersRepository.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> result = parameterHierarchy
                .children(returnType, releasesAndTransfers);

        Assert.assertNull(result.getRight());
        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFER, result.getLeft().getControlledList());
    }

    @Test
    public void traverseReturningNullForExcludedParameter() {
        ReturnType returnType = returnTypeRepository.getByName("Emissions to groundwater");
        Assert.assertNotNull(returnType);

        ReleasesAndTransfers releasesAndTransfers = releasesAndTransfersRepository.getByName("Air");
        Assert.assertNotNull(releasesAndTransfers);

        Parameter parameter = parameterRepository.getByName("Dichlorvos");
        Assert.assertNotNull(parameter);

        Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> result = parameterHierarchy
                .children(returnType, releasesAndTransfers, parameter);

        Assert.assertNull(result.getRight());
        Assert.assertEquals(ControlledListsList.RELEASES_AND_TRANSFER, result.getLeft().getControlledList());
    }

    @Test
    public void traverseAllNullsReturnsReturnTypes() {
        Pair<HierarchyLevel<? extends MasterDataEntity>, List<? extends MasterDataEntity>> result = parameterHierarchy
                .children((MasterDataEntity) null);

        Assert.assertNotNull(result.getRight());
        Assert.assertEquals(ControlledListsList.RETURN_TYPE, result.getLeft().getControlledList());
    }

    //    private void printList(List<? extends MasterDataEntity> list) {
    //        for (MasterDataEntity e : list) {
    //            LOGGER.info(e.getName());
    //        }
    //    }

}
