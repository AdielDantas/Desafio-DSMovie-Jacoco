package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {

	@InjectMocks
	private MovieService service;

	@Mock
	private MovieRepository repository;

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private MovieEntity movie;
	private MovieDTO movieDTO;
	private String title;
	private PageImpl<MovieEntity> page;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		movie = MovieFactory.createMovieEntity();
		movieDTO = MovieFactory.createMovieDTO();
		title = "Test Movie";
		page = new PageImpl<>(List.of(movie));

		when(repository.searchByTitle(any(), (Pageable)any())).thenReturn(page);

		when(repository.findById(existingId)).thenReturn(Optional.of(movie));
		when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		when(repository.save(any())).thenReturn(movie);

		when(repository.getReferenceById(existingId)).thenReturn(movie);
		when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

		when(repository.existsById(existingId)).thenReturn(true);
		when(repository.existsById(nonExistingId)).thenReturn(false);
		when(repository.existsById(dependentId)).thenReturn(true);

		doNothing().when(repository).deleteById(existingId);
		doThrow(ResourceNotFoundException.class).when(repository).deleteById(nonExistingId);
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() throws Exception {
		Pageable pageable = PageRequest.of(0, 12);
		Page<MovieDTO> result = service.findAll(title, pageable);

		assertNotNull(result);
		assertEquals(title, result.iterator().next().getTitle());
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() throws Exception {
		MovieDTO result = service.findById(existingId);

		assertNotNull(result);
		assertEquals(existingId, result.getId());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
	}
	
	@Test
	public void insertShouldReturnMovieDTO() throws Exception {
		MovieDTO result = service.insert(movieDTO);

		assertNotNull(result);
		assertEquals(movieDTO.getTitle(), result.getTitle());
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() throws Exception {
		MovieDTO result = service.update(existingId, movieDTO);

		assertNotNull(result);
		assertEquals(result.getId(), movieDTO.getId());
		assertEquals(result.getTitle(), movie.getTitle());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, movieDTO);
		});
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() throws Exception {
		assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
		assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() throws Exception {
		assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
	}
}
