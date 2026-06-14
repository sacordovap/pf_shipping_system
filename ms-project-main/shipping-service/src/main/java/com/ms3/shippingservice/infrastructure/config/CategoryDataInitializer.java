package com.ms3.shippingservice.infrastructure.config;

import com.ms3.shippingservice.infrastructure.persistency.entity.CategoryEntity;
import com.ms3.shippingservice.infrastructure.persistency.repository.JpaCategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryDataInitializer implements CommandLineRunner {

    private final JpaCategoryRepository categoryRepository;

    public CategoryDataInitializer(JpaCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Lista de generica
        List<CategoryEntity> categoriesToInit = List.of(
                new CategoryEntity(null, "Documentos", "Papelería, contratos, facturas y archivos"),
                new CategoryEntity(null, "Electrónicos", "Teléfonos, laptops, componentes y gadgets"),
                new CategoryEntity(null, "Frágil", "Artículos de vidrio, cerámica o alta sensibilidad"),
                new CategoryEntity(null, "Vestimenta", "Ropa, calzado y textiles en general")
        );

        for (CategoryEntity cat : categoriesToInit) {
            // Si no existe, se guarda y se genera automáticamente
            if (!categoryRepository.existsByName(cat.getName())) {
                categoryRepository.save(cat);
            }
        }
        System.out.println("Catálogo verificado e inicializado.");
    }
}