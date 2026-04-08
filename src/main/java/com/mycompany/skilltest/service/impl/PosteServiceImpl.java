package com.mycompany.skilltest.service.impl;

import com.mycompany.skilltest.domain.Poste;
import com.mycompany.skilltest.repository.PosteRepository;
import com.mycompany.skilltest.service.PosteService;
import com.mycompany.skilltest.service.dto.PosteDTO;
import com.mycompany.skilltest.service.mapper.PosteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.skilltest.domain.Poste}.
 */
@Service
@Transactional
public class PosteServiceImpl implements PosteService {

    private static final Logger LOG = LoggerFactory.getLogger(PosteServiceImpl.class);

    private final PosteRepository posteRepository;

    private final PosteMapper posteMapper;

    public PosteServiceImpl(PosteRepository posteRepository, PosteMapper posteMapper) {
        this.posteRepository = posteRepository;
        this.posteMapper = posteMapper;
    }

    @Override
    public PosteDTO save(PosteDTO posteDTO) {
        LOG.debug("Request to save Poste : {}", posteDTO);
        Poste poste = posteMapper.toEntity(posteDTO);
        poste = posteRepository.save(poste);
        return posteMapper.toDto(poste);
    }

    @Override
    public PosteDTO update(PosteDTO posteDTO) {
        LOG.debug("Request to update Poste : {}", posteDTO);
        Poste poste = posteMapper.toEntity(posteDTO);
        poste = posteRepository.save(poste);
        return posteMapper.toDto(poste);
    }

    @Override
    public Optional<PosteDTO> partialUpdate(PosteDTO posteDTO) {
        LOG.debug("Request to partially update Poste : {}", posteDTO);

        return posteRepository
            .findById(posteDTO.getId())
            .map(existingPoste -> {
                posteMapper.partialUpdate(existingPoste, posteDTO);

                return existingPoste;
            })
            .map(posteRepository::save)
            .map(posteMapper::toDto);
    }

    public Page<PosteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return posteRepository.findAllWithEagerRelationships(pageable).map(posteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PosteDTO> findOne(Long id) {
        LOG.debug("Request to get Poste : {}", id);
        return posteRepository.findOneWithEagerRelationships(id).map(posteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Poste : {}", id);
        posteRepository.deleteById(id);
    }
}
