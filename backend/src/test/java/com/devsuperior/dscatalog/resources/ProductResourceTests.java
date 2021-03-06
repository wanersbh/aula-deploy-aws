package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.devsuperior.dscatalog.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;

	// Usar quando a classe de teste carrega o contexto da aplicação e precisa
	// mockar algum bean do sistema @WebMvcTest @SpringBootTest
	@MockBean
	private ProductService service;
	
	@Autowired
	private TokenUtil tokenUtil;

	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private Page<ProductDTO> page;
	private ProductDTO productDTO;
	private String adminUsername;
	private String adminPassword;
	private String accessToken;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1234L;
		nonExistingId = 4321L;
		dependentId = 1111L;
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		
		accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
		

		productDTO = Factory.createdProductDTO();

		page = new PageImpl<>(List.of(productDTO));

		when(service.findAllPaged(any(),  any(), any())).thenReturn(page);

		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		when(service.update(eq(existingId), any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);
		
		when(service.insert(any())).thenReturn(productDTO);
		
		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DataBaseException.class).when(service).delete(dependentId);
	}
	
	@Test
	void deleteShouldReturnStatusNoContentWhenIdExists() throws Exception  {
		
		ResultActions result = mockMvc.perform(
				delete("/products/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON)
				);
		
		result.andExpect(status().isNoContent());
	}
	
	@Test
	void deleteShouldReturnStatusNotFoundWhenDependentId() throws Exception  {
		
		ResultActions result = mockMvc.perform(
				delete("/products/{id}", dependentId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON)
				);
		
		result.andExpect(status().isBadRequest());
	}
	
	@Test
	void deleteShouldReturnStatusBadRequestWhenIdDoesNotExists() throws Exception  {
		
		ResultActions result = mockMvc.perform(
				delete("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON)
				);
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	void insertShouldReturnStatusCreatedAndProductDTO() throws Exception {
		
		String jsonBody = mapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(
				post("/products")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				);
		
		result.andExpect(status().isCreated());
		
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}

	@Test
	void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		
		String jsonBody = mapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(
				put("/products/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				);
		
		result.andExpect(status().isOk());
		
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}

	@Test
	void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

		String jsonBody = mapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(
				put("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				);
		
		result.andExpect(status().isNotFound());
		
	}

	@Test
	void findAllShouldReturnPage() throws Exception {
		ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
	}

	@Test
	void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());

		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}

	@Test
	void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

}
