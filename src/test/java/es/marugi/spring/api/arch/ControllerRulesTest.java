package es.marugi.spring.api.arch;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMember;
import com.tngtech.archunit.core.domain.PackageMatchers;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Functions.GET_PACKAGE_NAME;
import static com.tngtech.archunit.core.domain.JavaMember.Predicates.declaredIn;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
        packages = "es.marugi.spring.api",
        importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class ControllerRulesTest {

//    @ArchTest
//    static final ArchRule controllers_should_only_call_secured_methods =
//            classes().that().resideInAPackage("..controller..")
//                    .should()
//                    .onlyCallMethodsThat(areDeclaredInController());


    @ArchTest
    static final ArchRule controllers_should_only_call_secured_constructors =
            classes()
                    .that().resideInAPackage("..controller..")
                    .should().onlyCallConstructorsThat(areDeclaredInController())
                    .allowEmptyShould(true);

//    @ArchTest
//    static final ArchRule controllers_should_only_call_services =
//            classes()
//                    .that().resideInAPackage("..controller..")
//                    .should().onlyCallCodeUnitsThat(are(declaredIn(GET_PACKAGE_NAME.is(PackageMatchers.of("..service..", "..mapper.."))))
//                            );

    @ArchTest
    static final ArchRule controllers_should_only_access_secured_fields =
            classes()
                    .that().resideInAPackage("..controller..")
                    .should().onlyAccessFieldsThat(areDeclaredInController())
                    .allowEmptyShould(true);

//    @ArchTest
//    static final ArchRule controllers_should_only_access_secured_members =
//            classes()
//                    .that().resideInAPackage("..controller..")
//                    .should().onlyAccessMembersThat(areDeclaredInController());

    private static DescribedPredicate<JavaMember> areDeclaredInController() {
        DescribedPredicate<JavaClass> aPackageController = GET_PACKAGE_NAME.is(PackageMatchers.of("..controller..", "java.."))
                .as("a package '..controller..'");
        return are(declaredIn(aPackageController));
    }

}