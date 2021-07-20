package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	//Anotação para envolver toda operação em uma transação
	//A Propriedade readOnly = true é para evitar um look na base de dados, pq nesse caso somente é uma consulta.
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {

		List<Category> list = categoryRepository.findAll();

		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		
		Category categoria = categoryRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("Entity not Found!"));
		
		return new CategoryDTO(categoria);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = categoryRepository.save(entity);
		
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {

		try {
			Category entity = categoryRepository.getOne(id);

			entity.setName(dto.getName());
			entity = categoryRepository.save(entity);

			return new CategoryDTO(entity);

		}catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found "+ id);
		}
		
	}

}
