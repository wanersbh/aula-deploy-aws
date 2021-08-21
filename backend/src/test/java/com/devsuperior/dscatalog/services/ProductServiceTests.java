package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
class ProductServiceTests {

	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository repository;

	@Mock
	private CategoryRepository categoryRepository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private ProductDTO productDTO;
	private Category category;

	@BeforeEach
	void setUp() {
		existingId = 1000L;
		nonExistingId = 2000L;
		dependentId = 4;
		product = Factory.createdProduct();
		productDTO = Factory.createdProductDTO();
		page = new PageImpl<>(List.of(product));
		category = Factory.createdCategory();

		// Mockar uma quando os métodos são void
		// <CONDIÇÃO> <AÇÃO> <RESULTADO>
		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		// Mockar uma quando os métodos são void
		// <AÇÃO> <CONDIÇÃO>
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}

	@Test
	void findByIdShouldReturnProductDTOWhenIdExists() {

		// Act
		ProductDTO result = service.findById(existingId);

		// Assertion: o resultado esperado.
		Assertions.assertNotNull(result);

		// times() é opcional
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
	}

	@Test
	void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		// Assertion: o resultado esperado.
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			// Act
			service.findById(nonExistingId);
		});
	}

	@Test
	void updateShouldReturnProductDTOWhenIdExist() {

		// Act
		ProductDTO result = service.update(Long.valueOf(existingId), productDTO);

		// Assertion: o resultado esperado.
		Assertions.assertNotNull(result);
	}

	@Test
	void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		// Assertion: o resultado esperado.
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			// Act
			service.update(Long.valueOf(nonExistingId), productDTO);
		});
	}

	@Test
	void findAllPagedShouldReturnPage() {

		// Arrage: prepara os objetos
		Pageable pageable = PageRequest.of(0, 10);

		// Act: ação desejada
		Page<ProductDTO> result = service.findAllPaged(pageable);

		// Assertion: o resultado esperado.
		Assertions.assertNotNull(result);
		// times() é opcional
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
	}

	@Test
	void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}

	@Test
	void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	void deleteShouldThrowDataBaseExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(dependentId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}

}
