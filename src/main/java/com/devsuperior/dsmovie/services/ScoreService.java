package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.utils.ScoreCustomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreService {

	@Autowired
	private UserService userService;
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private ScoreRepository scoreRepository;

	@Transactional
	public MovieDTO saveScore(ScoreDTO dto) {

		UserEntity user = userService.authenticated();

		MovieEntity movie = movieRepository.findById(dto.getMovieId())
				.orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));

		ScoreEntity score = new ScoreEntity();
		score.setMovie(movie);
		score.setUser(user);
		score.setValue(dto.getScore());

		score = scoreRepository.saveAndFlush(score);

		List<ScoreEntity> scoresList = new ArrayList<>(movie.getScores());

		double avg = ScoreCustomUtil.calculateAverage(scoresList);
		int count = ScoreCustomUtil.getScoreCount(scoresList);

		movie.setScore(avg);
		movie.setCount(count);

		movie = movieRepository.save(movie);

		return new MovieDTO(movie);
	}
}
