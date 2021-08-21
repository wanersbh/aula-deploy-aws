package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;
	
	long existingId;
	long nonExistingId;
	long countTotalProducts;

	@BeforeEach
	void setUp() {
		// Arrange: preparar os dados
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	void findByIdShouldReturnNonEmptyOptionalProductWhenIdExists() {

		// Act: ação desejada
		Optional<Product> productOpt = repository.findById(existingId);
		
		// Assertions: resultado esperado
		Assertions.assertTrue(productOpt.isPresent());
	}
	
	@Test
	void findByIdShouldRetunrEmptyOptionalWhenIdDoesNotExist() {

		// Act: ação desejada
		Optional<Product> productOpt = repository.findById(nonExistingId);
		
		// Assertions: resultado esperado
		Assertions.assertTrue(productOpt.isEmpty());
	}
	
	@Test
	void saveShouldPersistWithAutoIncrementtWhenIdIsNull() {
		
		//Arrange: Prepara os objetos
		Product product = Factory.createdProduct();
		product.setId(null);
		
		//Act: ação desejada
		product = repository.save(product);
		
		//Assertion: verificar o resultado esperado
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}

	@Test
	void deleteShouldDeleteObjectWhenIdExists() {

		// Act: ação do teste
		repository.deleteById(existingId);

		// Assertions: verificar
		Optional<Product> result = repository.findById(existingId);
		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

		// Assertions: verificar
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			// Act: ação do teste
			repository.deleteById(nonExistingId);
		});
	}

}
