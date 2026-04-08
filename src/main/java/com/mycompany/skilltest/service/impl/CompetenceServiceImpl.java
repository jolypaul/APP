package com.mycompany.skilltest.service.impl;

import com.mycompany.skilltest.domain.Competence;
import com.mycompany.skilltest.repository.CompetenceRepository;
import com.mycompany.skilltest.service.CompetenceService;
import com.mycompany.skilltest.service.dto.CompetenceDTO;
import com.mycompany.skilltest.service.mapper.CompetenceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.skilltest.domain.Competence}.
 */
@Service
@Transactional
public class CompetenceServiceImpl implements CompetenceService {

    private static final Logger LOG = LoggerFactory.getLogger(CompetenceServiceImpl.class);

    private final CompetenceRepository competenceRepository;

    private final CompetenceMapper competenceMapper;

    public CompetenceServiceImpl(CompetenceRepository competenceRepository, CompetenceMapper competenceMapper) {
        this.competenceRepository = competenceRepository;
        this.competenceMapper = competenceMapper;
    }

    @Override
    public CompetenceDTO save(CompetenceDTO competenceDTO) {
        LOG.debug("Request to save Competence : {}", competenceDTO);
        Competence competence = competenceMapper.toEntity(competenceDTO);
        competence = competenceRepository.save(competence);
        return competenceMapper.toDto(competence);
    }

    @Override
    public CompetenceDTO update(CompetenceDTO competenceDTO) {
        LOG.debug("Request to update Competence : {}", competenceDTO);
        Competence competence = competenceMapper.toEntity(competenceDTO);
        competence = competenceRepository.save(competence);
        return competenceMapper.toDto(competence);
    }

    @Override
    public Optional<CompetenceDTO> partialUpdate(CompetenceDTO competenceDTO) {
        LOG.debug("Request to partially update Competence : {}", competenceDTO);

        return competenceRepository
            .findById(competenceDTO.getId())
            .map(existingCompetence -> {
                competenceMapper.partialUpdate(existingCompetence, competenceDTO);

                return existingCompetence;
            })
            .map(competenceRepository::save)
            .map(competenceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompetenceDTO> findOne(Long id) {
        LOG.debug("Request to get Competence : {}", id);
        return competenceRepository.findById(id).map(competenceMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Competence : {}", id);
        competenceRepository.deleteById(id);
    }
}
