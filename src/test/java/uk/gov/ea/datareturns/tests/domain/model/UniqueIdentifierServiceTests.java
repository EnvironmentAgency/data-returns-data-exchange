package uk.gov.ea.datareturns.tests.domain.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.service.UniqueIdentifierService;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by graham on 08/11/16.
 */
@SpringBootTest(classes=App.class)
@DirtiesContext
@RunWith(SpringRunner.class)
public class UniqueIdentifierServiceTests {
    @Inject
    SiteDao siteDao;

    @Inject
    UniqueIdentifierService uniqueIdentifierService;

    /**
     * Test the retrieval of a UniqueIdentifier from its name.
     * Also tests a second retrieval from teh name cache
     */
    @Test
    public void getUniqueIdentifierFromName() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierService.getUniqueIdentifier("ZP3933LD");
        Assert.assertEquals(uniqueIdentifier.getName(), "ZP3933LD");
        uniqueIdentifier = uniqueIdentifierService.getUniqueIdentifier("BL9500IJ");
        Assert.assertEquals(uniqueIdentifier.getName(), "BL9500IJ");
    }

    /**
     * Test the retrieval of a UniqueIdentifier from its alias name
     */
    @Test
    public void getUniqueIdentifierFromAliasName() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierService.getUniqueIdentifier("KP3030NG");
        Assert.assertEquals(uniqueIdentifier.getName(), "BS7722ID");
        uniqueIdentifier = uniqueIdentifierService.getUniqueIdentifier("JB3937RN");
        Assert.assertEquals(uniqueIdentifier.getName(), "104554");
    }

    /**
     * Null test for a not found alias or ID
     */
    @Test
    public void getNullUniqueIdentifier() {
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierService.getUniqueIdentifier("jdghasfcighwfv");
        Assert.assertNull(uniqueIdentifier);
    }

    /**
     * Test the retrieval of a UniqueIdentifier from its alias name
     */
    @Test
    public void getUniqueIdentifierFound() {
        boolean found = uniqueIdentifierService.uniqueIdentifierExists("KP3030NG");
        Assert.assertTrue(found);
    }

    /**
     * Null test for a not found alias or ID
     */
    @Test
    public void getUniqueIdentifierNotFound() {
        boolean found = uniqueIdentifierService.uniqueIdentifierExists("jdghasfcighwfv");
        Assert.assertNotNull(found);
    }

    /**
     * Get a site - the site names are exact - we may not use a relaxed name
     */
    @Test
    public void getSite() {
        Site s1 = siteDao.getByName("Land North Of The Sewage Works");
        Assert.assertNotNull(s1);
        Site s2 = siteDao.getByNameRelaxed("Land  North Of   The Sewage  WORKS");
        Assert.assertNull(s2);
    }

    /**
     * Permit set used for tests involving aliases
     */
    private static Set<String> permitSet = new HashSet<String>() {{
        add("YP3638SX");
        add("XP3732XP");
        add("NP3935DM");
        add("FP3935GQ");
        add("ZP3134NK");
    }};

    /**
     * Get the set of names from a given unique identifier name
     */
    @Test
    public void getNamesFromUniqueIdentifierName() {
        Set<String> names = uniqueIdentifierService.getAllUniqueIdentifierNames("YP3638SX");
        Assert.assertTrue(names.containsAll(permitSet) && permitSet.containsAll(names));
    }

    /**
     * Get the set of names from a given alias identifier name
     */
    @Test
    public void getNamesFromUniqueIdentifierAliasName() {
        Set<String> names = uniqueIdentifierService.getAllUniqueIdentifierNames("ZP3134NK");
        Assert.assertTrue(names.containsAll(permitSet) && permitSet.containsAll(names));
    }
}