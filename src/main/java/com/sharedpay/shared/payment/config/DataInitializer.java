package com.sharedpay.shared.payment.config;
import com.sharedpay.shared.payment.entity.*;
import com.sharedpay.shared.payment.exception.RoleSeedingException;
import com.sharedpay.shared.payment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PaymentRateRepository paymentRateRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) {
        try {
            seedRoles();
            seedPaymentRate();
            seedParentsAndStudents();
        } catch (Exception e) {
            System.err.println("Data initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seedRoles() {
        try {
            seedRole("ADMIN");
            seedRole("USER");

            System.out.println("Roles seeded successfully.");
        } catch (Exception e) {
            throw new RoleSeedingException("Failed to seed roles properly. Reason: " + e.getMessage());
        }
    }

    private void seedRole(String roleName) {
        try {

            String roleNameWithPrefix = "ROLE_" + roleName;

            roleRepository.findByName(roleNameWithPrefix)
                    .orElseGet(() -> {
                        System.out.println("Seeding missing role: " + roleNameWithPrefix);
                        return roleRepository.save(new Role(roleNameWithPrefix));
                    });
        } catch (Exception e) {
            throw new RoleSeedingException(" Failed to seed role '" + roleName + "': " + e.getMessage());
        }
    }

    private void seedPaymentRate() {
        try {
            if (paymentRateRepository.count() == 0) {
                PaymentRate defaultRate = new PaymentRate();
                defaultRate.setDynamicRate(new BigDecimal("0.05"));
                paymentRateRepository.save(defaultRate);
                System.out.println("Default payment rate set.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error seeding payment rate: " + e.getMessage(), e);
        }
    }

    private void seedParentsAndStudents() {
        try {
            Parent parentA = parentRepository.save(new Parent("Parent A", new BigDecimal("1000.00"), new HashSet<>()));
            Parent parentB = parentRepository.save(new Parent("Parent B", new BigDecimal("1000.00"), new HashSet<>()));

            Student s1 = new Student("Student 1", new BigDecimal("0.00"), Set.of(parentA, parentB));
            Student s2 = new Student("Student 2", new BigDecimal("0.00"), Set.of(parentA));
            Student s3 = new Student("Student 3", new BigDecimal("0.00"), Set.of(parentB));

            studentRepository.saveAll(List.of(s1, s2, s3));
            System.out.println("Parents and Students seeded.");
        } catch (Exception e) {
            throw new RuntimeException("Error seeding parents and students: " + e.getMessage(), e);
        }
    }
}
