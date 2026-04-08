package com.mycompany.skilltest.service.impl;

import com.mycompany.skilltest.domain.Score;
import com.mycompany.skilltest.repository.ScoreRepository;
import com.mycompany.skilltest.service.ScoreService;
import com.mycompany.skilltest.service.dto.ScoreDTO;
import com.mycompany.skilltest.service.mapper.ScoreMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.skilltest.domain.Score}.
 */
@Service
@Transactional
public class ScoreServiceImpl implements ScoreService {

    private static final Logger LOG = LoggerFactory.getLogger(ScoreServiceImpl.class);

    private final ScoreRepository scoreRepository;

    private final ScoreMapper scoreMapper;

    public ScoreServiceImpl(ScoreRepository scoreRepository, ScoreMapper scoreMapper) {
        this.scoreRepository = scoreRepository;
        this.scoreMapper = scoreMapper;
    }

    @Override
    public ScoreDTO save(ScoreDTO scoreDTO) {
        LOG.debug("Request to save Score : {}", scoreDTO);
        Score score = scoreMapper.toEntity(scoreDTO);
        score = scoreRepository.save(score);
        return scoreMapper.toDto(score);
    }

    @Override
    public ScoreDTO update(ScoreDTO scoreDTO) {
        LOG.debug("Request to update Score : {}", scoreDTO);
        Score score = scoreMapper.toEntity(scoreDTO);
        score = scoreRepository.save(score);
        return scoreMapper.toDto(score);
    }

    @Override
    public Optional<ScoreDTO> partialUpdate(ScoreDTO scoreDTO) {
        LOG.debug("Request to partially update Score : {}", scoreDTO);

        return scoreRepository
            .findById(scoreDTO.getId())
            .map(existingScore -> {
                scoreMapper.partialUpdate(existingScore, scoreDTO);

                return existingScore;
            })
            .map(scoreRepository::save)
            .map(scoreMapper::toDto);
    }

    public Page<ScoreDTO> findAllWithEagerRelationships(Pageable pageable) {
        return scoreRepository.findAllWithEagerRelationships(pageable).map(scoreMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScoreDTO> findOne(Long id) {
        LOG.debug("Request to get Score : {}", id);
        return scoreRepository.findOneWithEagerRelationships(id).map(scoreMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Score : {}", id);
        scoreRepository.deleteById(id);
    }
}
