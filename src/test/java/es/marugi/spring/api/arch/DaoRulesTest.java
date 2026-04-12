package es.marugi.spring.api.arch;

import java.sql.SQLException;

import com.tngtech.archunit.core.importer.ImportOption;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

@AnalyzeClasses(
        packages = "es.marugi.spring.api",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class DaoRulesTest {
    @ArchTest
    static final ArchRule DAOs_must_reside_in_a_dao_package =
            classes().that().haveNameMatching(".*Dao").should().resideInAPackage("..dao..")
                    .as("DAOs should reside in a package '..dao..'")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule entities_must_reside_in_a_domain_package =
            classes().that().areAnnotatedWith(Entity.class).should().resideInAPackage("..domain..")
                    .as("Entities should reside in a package '..domain..'");

    @ArchTest
    static final ArchRule only_DAOs_may_use_the_EntityManager =
            noClasses().that().resideOutsideOfPackage("..dao..")
                    .should().accessClassesThat().areAssignableTo(EntityManager.class)
                    .as("Only DAOs may use the " + EntityManager.class.getSimpleName())
                    .allowEmptyShould(true);


    @ArchTest
    static final ArchRule DAOs_must_not_throw_SQLException =
            noMethods().that().areDeclaredInClassesThat().haveNameMatching(".*Dao")
                    .should().declareThrowableOfType(SQLException.class)
                    .allowEmptyShould(true);
}