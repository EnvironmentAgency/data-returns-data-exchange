package uk.gov.defra.datareturns.tests.integration.api;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.defra.datareturns.MasterDataApi;
import uk.gov.defra.datareturns.data.loader.DataLoader;
import uk.gov.defra.datareturns.tests.rules.RestAssuredRule;
import uk.gov.defra.datareturns.tests.util.ApiResource;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.withArgs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MasterDataApi.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IntegrationTests")
@Slf4j
public class DataLoaderTests {
    private static boolean dbInitialised = false;

    @Inject
    @Rule
    public RestAssuredRule restAssuredRule;

    @Inject
    private DataLoader loader;

    @Before
    public void setupDb() {
        if (!dbInitialised) {
            dbInitialised = true;
            loader.loadAll();
        }
    }

    @Test
    public void testParameters() {
        given()
                .contentType(ContentType.JSON)
                .when().get(ApiResource.PARAMETERS.url())
                .then()
                .statusCode(HttpStatus.OK.value())
                .root("_embedded.parameters[%d]")
                .body("nomenclature", withArgs(0), equalTo("P01"))
                .body("nomenclature", withArgs(1), equalTo("P02"))
                .body("nomenclature", withArgs(2), equalTo("P03"));

    }

    @Test
    public void testParameterAliases() {
        given().param("projection", "inlineAliases")
                .when().get(ApiResource.PARAMETERS.url())
                .then()
                .statusCode(HttpStatus.OK.value())
                .root("_embedded.parameters.find {it.nomenclature == '%s'}")
                .body("aliases", withArgs("P01"), hasItems("P01_A1", "P01_A2"))
                .body("aliases", withArgs("P02"), hasItems("P02_A1", "P02_A2"));
    }


    @Test
    public void testUnits() {
        given()
                .contentType(ContentType.JSON)
                .when().get(ApiResource.UNITS.url())
                .then()
                .statusCode(HttpStatus.OK.value())
                .root("_embedded.units[%d]")
                .body("nomenclature", withArgs(0), equalTo("UOM1A"))
                .body("nomenclature", withArgs(1), equalTo("UOM1B"))
                .body("nomenclature", withArgs(2), equalTo("UOM2A"))
                .body("nomenclature", withArgs(3), equalTo("UOM2B"));
    }

    @Test
    public void testRegimes() {
        final int expectedRegimeCount = 2;

        final ValidatableResponse response = given()
                .contentType(ContentType.JSON)
                .when().get(ApiResource.REGIMES.url())
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all();


        final ExtractableResponse<?> res = response.extract();
        final List<Map<String, String>> regimes = res.jsonPath().getList("_embedded.regimes");

        log.info("Found regimes {}", regimes);

        for (int i = 0; i < expectedRegimeCount; i++) {
            final Map<String, ?> regimeData = regimes.get(i);
            Assertions.assertThat(regimeData.get("nomenclature")).isEqualTo("Regime" + i);
        }
    }

}
