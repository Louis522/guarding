package GAPDetector;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.thirdparty.com.google.common.util.concurrent.Service;
import org.junit.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureTest {

    @Test
    public void shouldEnforceLayerDependencies() {
        // 导入待检查的Java类
        JavaClasses importedClasses = new ClassFileImporter().importPackages("GAPDetector");

        // 定义架构规则
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .should().beAnnotatedWith(Service.class);

// 检查所有以"Service"结尾的类是否都有@Service注解
        rule.check(importedClasses);
        // 执行规则检查
        rule.check(importedClasses);
       // bllShouldMediateBetweenUiAndDal.check(importedClasses);
    }
}